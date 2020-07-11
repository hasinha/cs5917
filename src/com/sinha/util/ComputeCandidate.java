package com.sinha.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sinha.model.AttackRelation;
import com.sinha.model.Candidate;

public class ComputeCandidate implements Callable<List<Candidate>> {

	private static final Logger logger = LoggerFactory.getLogger(ComputeCandidate.class);

	private List<AttackRelation> attacks;

	private Candidate candidate;

	private String argToAdd;

	public ComputeCandidate(String argument, List<AttackRelation> attacks, Candidate candidate) {
		this.argToAdd = argument;
		this.attacks = attacks;
		this.candidate = candidate;
	}

	@Override
	public List<Candidate> call() throws Exception {
//		logger.info("Started thread");
		List<Candidate> candidates = new ArrayList<>();
		candidates.add(argumentAddition());
		candidates.add(argumentRemoval());
		return candidates;
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
