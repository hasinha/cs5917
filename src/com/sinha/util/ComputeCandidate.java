package com.sinha.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.sinha.model.AttackRelation;
import com.sinha.model.Candidate;

public class ComputeCandidate implements Callable<List<Candidate>> {

	private static final Logger logger = LoggerFactory.getLogger(ComputeCandidate.class);

	private List<AttackRelation> attacks;

	private Candidate candidate;

	private String argToAdd;

	private List<String> argumentsComp;

	public ComputeCandidate(String argument, List<AttackRelation> attacks, Candidate candidate,
			List<String> argumentsComp) {
		this.argToAdd = argument;
		this.attacks = attacks;
		this.candidate = candidate;
		this.argumentsComp = argumentsComp;
	}

	@Override
	public List<Candidate> call() throws Exception {
//		logger.info("Started thread");
		List<Candidate> candidates = new ArrayList<>();
		if (checkForAddition()) {
			candidates.add(argumentAddition());
		}
		if (!checkForRemoval()) {
			candidates.add(argumentRemoval());
		}
		return candidates;
	}

	private List<String> attackedArgsInSet(Set<String> setArgs) {
		if (CollectionUtils.isEmpty(setArgs)) {
			return Collections.emptyList();
		}
		List<String> returnVal = new ArrayList<>();
		for (AttackRelation attack : this.attacks) {
			if (setArgs.containsAll(attack.getAttackMembers())) {
				returnVal.add(attack.getAttacked().getLabel());
			}
		}
		return returnVal;
	}

	private Set<String> selfAttArgs(Set<String> args) {
		Set<String> selfAttArgs = new HashSet<>();
		for (String arg : args) {
			for (AttackRelation attack : attacks) {
				if (attack.getAttacked().getLabel().equals(arg)) {
					List<String> tempList = new ArrayList<>(argumentsComp);
					tempList.removeAll(attack.getAttackMembers());
					selfAttArgs.addAll(tempList);
				}
			}
		}
		return selfAttArgs;
	}

	private boolean checkForRemoval() {
		List<String> cond1Args = new ArrayList<>();
		boolean cond1 = false;
		for (AttackRelation relation : this.attacks) {
			if (relation.getAttacked().getLabel().equals(this.argToAdd)) {
				cond1Args.addAll(relation.getAttackMembers());
			}
		}
		List<String> attackedArgsIn = attackedArgsInSet(this.candidate.getInArguments());
		cond1 = !Collections.disjoint(cond1Args, attackedArgsIn);

		boolean cond2 = false;
		Set<String> tempList = new HashSet<>(candidate.getInArguments());
		tempList.addAll(candidate.getUndecArguments());

		List<String> attackedCond2 = attackedArgsInSet(tempList);
		cond2 = !attackedCond2.contains(argToAdd);

		boolean cond3 = false;
		tempList = new HashSet<>(candidate.getInArguments());
		tempList.addAll(candidate.getUndecArguments());
		tempList.remove(argToAdd);
		Set<String> selfAttArgs = selfAttArgs(tempList);
		cond3 = !selfAttArgs.contains(argToAdd);
		return cond1 || cond2 || cond3;
	}

	private boolean checkForAddition() {
		Set<String> argAttackers = new HashSet<>();
		for (AttackRelation attack : attacks) {
			if (attack.getAttacked().getLabel().equals(this.argToAdd)) {
				argAttackers.addAll(attack.getAttackMembers());
			}
		}
		if (CollectionUtils.isEmpty(argAttackers)) {
			return true;
		}
		boolean returnVal = false;
		for (String attacker : argAttackers) {
			for (AttackRelation attack : attacks) {
				if (attack.getAttacked().getLabel().equals(attacker)) {
					List<String> newList = new ArrayList<>(attack.getAttackMembers());
					newList.retainAll(this.candidate.getOutArguments());
					if (CollectionUtils.isEmpty(newList)) {
						returnVal = true;
						break;
					}
				}
			}
		}
		return returnVal;
	}

	private Candidate argumentAddition() {
		Candidate candidate = this.candidate.clone();
		candidate.markIn(this.argToAdd);
		List<String> deltaAtt = deltaAttacked(candidate, this.argToAdd);
		List<String> deltaSelfAtt = deltaSelfAttack(candidate, this.argToAdd);
		for (String att : deltaAtt) {
			candidate.markOut(att);
		}
		for (String selfAtt : deltaSelfAtt) {
			candidate.markOut(selfAtt);
		}
		return candidate;
	}

	private Candidate argumentRemoval() {
		Candidate candidate = this.candidate.clone();
		candidate.markOut(this.argToAdd);
		return candidate;
	}

	private List<String> deltaAttacked(Candidate curCandidate, String argumentModified) {
		List<String> markedArguments = new ArrayList<>();
		for (String undecArg : curCandidate.getUndecArguments()) {
			for (AttackRelation attack : this.attacks) {
				if (attack.getAttacked().getLabel().equals(undecArg)
						&& curCandidate.getInArguments().containsAll(attack.getAttackMembers())) {
					markedArguments.add(undecArg);
				}
			}
		}
		return markedArguments;
	}

	private List<String> deltaSelfAttack(Candidate curCandidate, String argumentModified) {
		List<String> markedArguments = new ArrayList<>();
		List<String> preAddInArguments = new ArrayList<>(curCandidate.getInArguments());
		preAddInArguments.remove(argumentModified);
		for (String undecArg : curCandidate.getUndecArguments()) {
			preAddInArguments.add(undecArg);
			for (AttackRelation attack : this.attacks) {
				if (attack.getAttacked().getLabel().equals(argumentModified)
						&& preAddInArguments.containsAll(attack.getAttackMembers())) {
					markedArguments.add(undecArg);
				}
			}
			preAddInArguments.remove(undecArg);
		}
		for (String inArg : curCandidate.getInArguments()) {
			for (AttackRelation relation : this.attacks) {
				if (relation.getAttacked().getLabel().equals(inArg)) {
					for (String undecArgs : curCandidate.getUndecArguments()) {
						preAddInArguments.add(undecArgs);
						preAddInArguments.add(argumentModified);
						if (preAddInArguments.containsAll(relation.getAttackMembers())) {
							markedArguments.add(undecArgs);
						}
						preAddInArguments.remove(undecArgs);
						preAddInArguments.remove(argumentModified);
					}
				}
			}
		}
		preAddInArguments.add(argumentModified);
		for (String undecArg : curCandidate.getUndecArguments()) {
			preAddInArguments.add(undecArg);
			for (AttackRelation relation : this.attacks) {
				if (relation.getAttacked().getLabel().equals(argumentModified)
						& preAddInArguments.containsAll(relation.getAttackMembers())) {
					markedArguments.add(undecArg);
				}
			}
			preAddInArguments.remove(undecArg);
		}
		return markedArguments;
	}
}
