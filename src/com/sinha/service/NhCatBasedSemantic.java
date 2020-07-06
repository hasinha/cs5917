package com.sinha.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sinha.model.Argument;
import com.sinha.model.ArgumentFramework;
import com.sinha.model.AttackRelation;
import com.sinha.util.FloatComparator;

@Service
public class NhCatBasedSemantic implements RankingSemantic {

	private static final Logger logger = LoggerFactory.getLogger(NhCatBasedSemantic.class);

	private static final int MAX_CYCLES = 20;

	@Override
	public void generateRanks(ArgumentFramework af) {
		logger.info("Generating Ranks using Nh Categoriser");
		initializeStrengths(af.getArguments());
		int i = 0;
		while (i < MAX_CYCLES) {
			i++;
			for (Argument arg : af.getArguments()) {
				newStrength(arg, af.getAttackRelation());
			}
			for (Argument arg : af.getArguments()) {
				arg.setStrengthValue(arg.getNewStrengthValue());
			}
		}
		Collections.sort(af.getArguments(), new FloatComparator());
		logger.info("Generated Strengths: {}", af.getArguments());
	}

	private void initializeStrengths(List<Argument> arguments) {
		for (Argument arg : arguments) {
			arg.setStrengthValue(1f);
		}
	}

	private void newStrength(Argument arg, List<AttackRelation> relations) {
		float sum = 0;
		boolean attacked = false;
		for (AttackRelation relation : relations) {
			if (relation.getAttacked().equals(arg)) {
				attacked = true;
				List<Float> strengths = relation.getAttackers().stream().map(s -> s.getStrengthValue())
						.collect(Collectors.toList());
				sum += Collections.min(strengths);
			}
		}
		if (attacked) {
			arg.setNewStrengthValue(1 / (1 + sum));
		} else {
			arg.setNewStrengthValue(1f);
		}
	}

}
