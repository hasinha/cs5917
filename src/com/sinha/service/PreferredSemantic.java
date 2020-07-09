package com.sinha.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.sinha.model.ArgumentFramework;
import com.sinha.model.AttackRelation;
import com.sinha.model.Candidate;
import com.sinha.util.ComputeCandidate;

@Service
public class PreferredSemantic implements ReasoningSemantic {

	private static final Logger logger = LoggerFactory.getLogger(PreferredSemantic.class);

	private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(40);

	@Override
	public void generateLabelings(ArgumentFramework af) throws Exception {
		logger.info("Generating Preferred Extensions");
		List<String> argList = af.getArguments().stream().map(s -> s.getLabel()).collect(Collectors.toList());
		Candidate candidate = new Candidate();
		candidate.setUndecArguments(new HashSet<>(argList));
		Set<Candidate> finalResults = new HashSet<>();
		computeCandidates(candidate, af, finalResults);
		Set<Candidate> candidatesToRemove = getCandidatesToRemove(finalResults);
		for (Candidate cand : candidatesToRemove) {
			finalResults.remove(cand);
		}
		af.setCandidates(new ArrayList<>(finalResults));
	}

	private Set<Candidate> getCandidatesToRemove(Set<Candidate> finalResults) {
		Set<Candidate> candidatesToRemove = new HashSet<>();
		Set<Candidate> emptySetCandidates = new HashSet<>();
		boolean isNonEmptyCandidatePresent = Boolean.FALSE;
		for (Candidate cand1 : finalResults) {
			for (Candidate cand2 : finalResults) {
				if (isNonEmptyCandidatePresent && (!CollectionUtils.isEmpty(cand1.getInArguments())
						|| !CollectionUtils.isEmpty(cand1.getInArguments()))) {
					isNonEmptyCandidatePresent = Boolean.TRUE;
				}
				if (CollectionUtils.isEmpty(cand1.getInArguments())) {
					emptySetCandidates.add(cand1);
				}
				if (CollectionUtils.isEmpty(cand2.getInArguments())) {
					emptySetCandidates.add(cand2);
				}
				if (cand1.equals(cand2)) {
					continue;
				}
				if (cand2.getInArguments().containsAll(cand1.getInArguments())) {
					candidatesToRemove.add(cand1);
				}
			}
		}
		if (isNonEmptyCandidatePresent) {
			candidatesToRemove.addAll(emptySetCandidates);
		}
		return candidatesToRemove;
	}

	private void computeCandidates(Candidate candidate, ArgumentFramework af, Set<Candidate> finalResults)
			throws Exception {
		if (CollectionUtils.isEmpty(candidate.getUndecArguments())) {
			return;
		}
		Callable<List<Candidate>> callables = prepareCallables(candidate, af,
				new ArrayList<>(candidate.getUndecArguments()).get(0));
		List<Candidate> prospects = performTask(callables, EXECUTOR);
		for (Candidate cand : prospects) {
			if (isConflictFree(cand, af) && isAdmissible(cand, af)) {
				finalResults.add(cand);
			}
			computeCandidates(cand, af, finalResults);
		}
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

	private List<Candidate> performTask(Callable<List<Candidate>> callable, ExecutorService executor) throws Exception {
		List<Candidate> results = new ArrayList<>();
		Future<List<Candidate>> future = executor.submit(callable);
		try {
			results.addAll(future.get());
		} catch (Exception e) {
			logger.error("Exception getting result: ", e);
			throw new Exception("FutureException");
		}
		return results;
	}

	private Callable<List<Candidate>> prepareCallables(Candidate candidate, ArgumentFramework af, String argument) {
		return new ComputeCandidate(argument, af.getAttackRelation(), candidate);
	}

}
