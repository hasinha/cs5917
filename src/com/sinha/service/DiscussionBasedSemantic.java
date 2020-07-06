package com.sinha.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.buf.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sinha.model.Argument;
import com.sinha.model.ArgumentFramework;
import com.sinha.model.AttackRelation;
import com.sinha.util.LexComparator;

@Service
public class DiscussionBasedSemantic implements RankingSemantic {

	private static final Logger logger = LoggerFactory.getLogger(DiscussionBasedSemantic.class);

	private static final int MAX_STEPS = 5;

	@Override
	public void generateRanks(ArgumentFramework af) {
		logger.info("Generating Discussion Based Rankings");
		int i = 0;
		Map<Argument, List<String>> argPathLengths = new HashMap<>();
		while (i < MAX_STEPS) {
			for (Argument arg : af.getArguments()) {
				if (!argPathLengths.containsKey(arg)) {
					argPathLengths.put(arg, new ArrayList<>());
				}
				int pathLength = findPathsOfLengths(i + 1, arg, af.getAttackRelation());
				if (i % 2 != 0) {
					pathLength = -1 * pathLength;
				}
				argPathLengths.get(arg).add(String.valueOf(pathLength));
			}
			i++;
		}
		for (Map.Entry<Argument, List<String>> entry : argPathLengths.entrySet()) {
			entry.getKey().setPathCountStr(StringUtils.join(entry.getValue()));
		}
		Collections.sort(af.getArguments(), new LexComparator());
	}

	private int findPathsOfLengths(int i, Argument arg, List<AttackRelation> relations) {
		List<Argument> argsToCheckForAttacks = new ArrayList<>();
		List<Argument> tempList = new ArrayList<>();
		argsToCheckForAttacks.add(arg);
		int j = 0;
		int pathCount = 0;
		while (j < i) {
			j++;
			for (Argument curArg : argsToCheckForAttacks) {
				for (AttackRelation relation : relations) {
					if (relation.getAttacked().equals(curArg)) {
						if (j == i) {
							pathCount++;
						} else {
							tempList.addAll(relation.getAttackers());
						}
					}
				}
			}
			argsToCheckForAttacks.clear();
			argsToCheckForAttacks.addAll(tempList);
			tempList.clear();
		}
		return pathCount;
	}

}
