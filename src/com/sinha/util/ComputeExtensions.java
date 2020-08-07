package com.sinha.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.CollectionUtils;

import com.sinha.model.Argument;
import com.sinha.model.ArgumentFramework;
import com.sinha.model.AttackRelation;
import com.sinha.model.Candidate;

public class ComputeExtensions implements Runnable {

	private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(40);

	private static final Logger logger = LoggerFactory.getLogger(ComputeExtensions.class);

	private ArgumentFramework af;

	private Candidate candidate;

	private SimpMessagingTemplate template;

	private String userId;

	private Candidate admissibileSet = null;

	private boolean genExt;

	public ComputeExtensions(ArgumentFramework af, Candidate candidate, SimpMessagingTemplate template, String userId,
			boolean genExt) {
		this.af = af;
		this.candidate = candidate;
		this.template = template;
		this.userId = userId;
		this.genExt = genExt;
	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		Set<Candidate> finalResults = new HashSet<>();
		finalResults.add(candidate);
		if (!this.genExt) {
			sendSpecific(af, userId);
			return;
		}
		try {
			computeCandidates(new ArrayList<>(Arrays.asList(candidate)), af, finalResults);
			List<Candidate> candidatesToRemove = getCandidatesToRemove(finalResults);
			filterEqInArgExtensions(finalResults);
			for (Candidate cand : candidatesToRemove) {
				finalResults.remove(cand);
			}
			List<Candidate> sortedResults = populateCandidateArguments(new ArrayList<>(finalResults), af);
			af.setCandidates(sortedResults);
			sendSpecific(af, this.userId);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		long endTime = System.currentTimeMillis();
		logger.info("Time taken: {}", (endTime - startTime));
	}

	private List<Candidate> populateCandidateArguments(List<Candidate> results, ArgumentFramework af) {
		List<Argument> argsList = new ArrayList<>(af.getArguments());
		int i = 1;
		for (Argument arg : argsList) {
			arg.setRank(i);
			i++;
		}
		Map<String, Argument> argMap = argsList.stream().collect(Collectors.toMap(Argument::getLabel, arg -> arg));
		for (Candidate candidate : results) {
			List<Integer> tempList = new ArrayList<>();
			if (CollectionUtils.isEmpty(candidate.getInArguments())) {
				continue;
			}
			for (String arg : candidate.getInArguments()) {
				tempList.add(argMap.get(arg).getRank());
			}
			Collections.sort(tempList);
			candidate.setInArgRanks(StringUtils.join(tempList, ","));
		}
		Collections.sort(results, new CandidateLexComparator());
		return results;
	}

	private void computeCandidates(List<Candidate> candidates, ArgumentFramework af, Set<Candidate> finalResults)
			throws Exception {
		long startTime = System.currentTimeMillis();
		List<Candidate> tempCandidates = new ArrayList<>();
		List<Callable<List<Candidate>>> callables = new ArrayList<>();
		List<String> totalArgList = af.getArguments().stream().map(s -> s.getLabel()).collect(Collectors.toList());
		for (Candidate cand : candidates) {
			callables.add(prepareCallables(cand, af, new ArrayList<>(cand.getUndecArguments()).get(0), totalArgList));
		}
		List<Candidate> prospects = performTask(callables, EXECUTOR);
		for (Candidate cand : prospects) {
			boolean adm = isAdmissible(cand, af);
			boolean cf = isConflictFree(cand, af);
			if (adm) {
				if (null == this.admissibileSet) {
					this.admissibileSet = cand;
				} else {
					List<String> unionArgs = new ArrayList<>(cand.getInArguments());
					unionArgs.addAll(cand.getUndecArguments());
					if (cand.getInArguments().containsAll(this.admissibileSet.getInArguments())
							&& !cand.getInArguments().equals(this.admissibileSet.getInArguments())) {
						this.admissibileSet = cand;
					} else if (this.admissibileSet.getInArguments().containsAll(unionArgs)
							&& !this.admissibileSet.getInArguments().equals(new HashSet<>(unionArgs))) {
						continue;
					}
				}
			}
			if (cf && adm) {
				finalResults.add(cand);
			}
			if (!CollectionUtils.isEmpty(cand.getUndecArguments())) {
				tempCandidates.add(cand);
			}
		}
		long endTime = System.currentTimeMillis();
		logger.info("Cycle Time Taken: {}, Candidate Count: {}", (endTime - startTime), tempCandidates.size());
		if (!CollectionUtils.isEmpty(tempCandidates)) {
			computeCandidates(tempCandidates, af, finalResults);
		}
	}

	private void filterEqInArgExtensions(Set<Candidate> finalResults) {
		List<Candidate> toRemove = new ArrayList<>();
		for (Candidate cand1 : finalResults) {
			for (Candidate cand2 : finalResults) {
				if (cand1.equals(cand2)) {
					continue;
				}
				if (cand1.getInArguments().equals(cand2.getInArguments())
						&& (!CollectionUtils.isEmpty(cand1.getUndecArguments())
								|| !CollectionUtils.isEmpty(cand2.getUndecArguments()))
						&& cand1.getUndecArguments().containsAll(cand2.getUndecArguments())) {
					toRemove.add(cand2);
				}
			}
		}
		for (Candidate cand : toRemove) {
			finalResults.remove(cand);
		}
	}

	private List<Candidate> getCandidatesToRemove(Set<Candidate> finalResults) {
		logger.info("FinalResults size: {}", finalResults.size());
		long startTime = System.currentTimeMillis();
		List<Candidate> candidatesToRemove = new ArrayList<>();
		List<Candidate> emptySetCandidates = new ArrayList<>();
		boolean isNonEmptyCandidatePresent = Boolean.FALSE;
		for (Candidate cand1 : finalResults) {
			for (Candidate cand2 : finalResults) {
				boolean isEmptyInArg = Boolean.FALSE;
				boolean cand1Empty = CollectionUtils.isEmpty(cand1.getInArguments());
				boolean cand2Empty = CollectionUtils.isEmpty(cand2.getInArguments());
				if (!isNonEmptyCandidatePresent && (!cand1Empty || !cand2Empty)) {
					isNonEmptyCandidatePresent = Boolean.TRUE;
				}
				if (cand1Empty) {
					isEmptyInArg = true;
					emptySetCandidates.add(cand1);
				}
				if (cand2Empty) {
					isEmptyInArg = true;
					emptySetCandidates.add(cand2);
				}
				if (isEmptyInArg) {
					continue;
				}
				if (cand1.equals(cand2)) {
					continue;
				}
				if (!cand2.getInArguments().equals(cand1.getInArguments())
						&& cand2.getInArguments().containsAll(cand1.getInArguments())) {
					candidatesToRemove.add(cand1);
				}
			}
		}
		logger.info("Time Taken: {}", (System.currentTimeMillis() - startTime));
		if (isNonEmptyCandidatePresent) {
			candidatesToRemove.addAll(emptySetCandidates);
		}
		long endTime = System.currentTimeMillis();
		logger.info("Time Taken remove candidates: {}", (endTime - startTime));
		return candidatesToRemove;
	}

	private boolean isConflictFree(Candidate candidate, ArgumentFramework af) {
		for (String inArg : candidate.getInArguments()) {
			for (AttackRelation attack : af.getAttackRelation()) {
				if (attack.getAttacked().getLabel().equals(inArg)
						&& candidate.getInArguments().containsAll(attack.getAttackMembers())) {
					return Boolean.FALSE;
				}
			}
		}
		return Boolean.TRUE;
	}

	private boolean isAdmissible(Candidate candidate, ArgumentFramework af) {
		for (String inArg : candidate.getInArguments()) {
			for (AttackRelation attack : af.getAttackRelation()) {
				if (attack.getAttacked().getLabel().equals(inArg)) {
					boolean isDefended = Boolean.FALSE;
					for (String attMember : attack.getAttackMembers()) {
						for (AttackRelation defAtt : af.getAttackRelation()) {
							if (defAtt.getAttacked().getLabel().equals(attMember)) {
								if (candidate.getInArguments().containsAll(defAtt.getAttackMembers())) {
									isDefended = Boolean.TRUE;
									break;
								}
							}
						}
						if (isDefended) {
							break;
						}
					}
					if (!isDefended) {
						return Boolean.FALSE;
					}
				}
			}
		}
		return Boolean.TRUE;
	}

	private List<Candidate> performTask(List<Callable<List<Candidate>>> callables, ExecutorService executor)
			throws Exception {
		List<Candidate> results = new ArrayList<>();
		List<Future<List<Candidate>>> futures = new ArrayList<>();
		for (Callable<List<Candidate>> callable : callables) {
			futures.add(executor.submit(callable));
		}
		for (Future<List<Candidate>> future : futures) {
			try {
				results.addAll(future.get());
			} catch (Exception e) {
				logger.error("Exception getting result: ", e);
				throw new Exception("FutureException");
			}
		}
		return results;
	}

	private Callable<List<Candidate>> prepareCallables(Candidate candidate, ArgumentFramework af, String argument,
			List<String> argList) {
		return new ComputeCandidate(argument, af.getAttackRelation(), candidate, argList);
	}

	@MessageMapping("/topic")
	public void sendSpecific(ArgumentFramework af, String userId) {
		template.convertAndSendToUser(userId, "topic", af);
		template.convertAndSend("/topic/" + userId, af);
	}
}
