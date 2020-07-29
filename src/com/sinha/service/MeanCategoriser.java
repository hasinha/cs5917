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
public class MeanCategoriser implements RankingSemantic {

	private static final Logger logger = LoggerFactory.getLogger(MeanCategoriser.class);

	private static final int MAX_CYCLES = 50;

	private static final float MAX_DIFF = 0.01f;

	@Override
	public void generateRanks(ArgumentFramework af) {
		logger.info("Generating ranks using Mean Categoriser");
		initializeStrengths(af.getArguments());
		int i = 0;
		while (i < MAX_CYCLES) {
			boolean isPrecAchieved = Boolean.TRUE;
			i++;
			for (Argument arg : af.getArguments()) {
				newStrength(arg, af.getAttackRelation());
				if (isPrecAchieved) {
					isPrecAchieved = Math.abs(arg.getNewStrengthValue() - arg.getStrengthValue()) < MAX_DIFF;
				}
			}
			for (Argument arg : af.getArguments()) {
				arg.setStrengthValue(arg.getNewStrengthValue());
			}
			if (isPrecAchieved) {
				break;
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
				sum += ((float) strengths.stream().mapToDouble(s -> s).sum()) / strengths.size();
			}
		}
		if (attacked) {
			arg.setNewStrengthValue(1 / (1 + sum));
		} else {
			arg.setNewStrengthValue(1f);
		}
	}

}
