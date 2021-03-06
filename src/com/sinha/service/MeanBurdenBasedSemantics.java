package com.sinha.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sinha.model.Argument;
import com.sinha.model.ArgumentFramework;
import com.sinha.model.AttackRelation;
import com.sinha.util.LexComparator;

@Service
public class MeanBurdenBasedSemantics implements RankingSemantic {

	private static final Logger logger = LoggerFactory.getLogger(MeanBurdenBasedSemantics.class);

	private static final int MAX_STEPS = 10;

	private static final float MAX_DIFF = 0.01f;

	@Override
	public void generateRanks(ArgumentFramework af) {
		logger.info("Generating Mean-Burden Based Rankings");
		Map<Argument, List<String>> argumentBurdens = new HashMap<>();
		initializeBurdens(af.getArguments(), argumentBurdens);
		int i = 1;
		while (i < MAX_STEPS) {
			boolean isPrecAchieved = Boolean.TRUE;
			for (Argument arg : af.getArguments()) {
				float sum = 0f;
				boolean attacked = false;
				for (AttackRelation relation : af.getAttackRelation()) {
					if (relation.getAttacked().equals(arg)) {
						attacked = true;
						List<Float> strengths = relation.getAttackers().stream().map(s -> s.getStrengthValue())
								.collect(Collectors.toList());
						float mean = ((float) strengths.stream().mapToDouble(s -> s).sum()) / strengths.size();
						sum += 1 / mean;
					}
				}
				if (attacked) {
					argumentBurdens.get(arg).add(String.valueOf(1f + sum));
					arg.setNewStrengthValue(1f + sum);
				} else {
					argumentBurdens.get(arg).add(String.valueOf(1f));
					arg.setNewStrengthValue(1f);
				}
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
			i++;
		}
		for (Map.Entry<Argument, List<String>> entry : argumentBurdens.entrySet()) {
			entry.getKey().setPathCountStr(StringUtils.join(entry.getValue()));
		}
		Collections.sort(af.getArguments(), new LexComparator());
	}

	private void initializeBurdens(List<Argument> arguments, Map<Argument, List<String>> argumentBurdens) {
		for (Argument arg : arguments) {
			arg.setStrengthValue(1f);
			argumentBurdens.put(arg, new ArrayList<>());
		}
	}
}
