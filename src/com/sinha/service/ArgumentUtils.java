package com.sinha.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.sinha.model.Argument;
import com.sinha.model.ArgumentFramework;
import com.sinha.model.AttackRelation;

public class ArgumentUtils {

	public static ArgumentFramework parseArguments(List<String> lines) {
		List<String> attacks = new ArrayList<>();
		List<Argument> arguments = new ArrayList<>();
		ArgumentFramework af = new ArgumentFramework();
		for (String line : lines) {
			if (StringUtils.isBlank(line)) {
				continue;
			}
			if (line.startsWith("att") || line.startsWith("mem")) {
				attacks.add(line);
				continue;
			}
			String label = line.substring(4, line.length() - 1);
			Argument arg = new Argument(label);
			arguments.add(arg);
		}
		List<AttackRelation> attackRelations = parseAttacks(attacks, arguments);
		af.setArguments(arguments);
		af.setAttackRelation(attackRelations);
		return af;
	}

	public static Argument findByLabel(String label, List<Argument> arguments) throws Exception {
		for (Argument arg : arguments) {
			if (arg.getLabel().equals(label)) {
				return arg;
			}
		}
		throw new Exception("No Argument Found for specified label");
	}

	private static List<AttackRelation> parseAttacks(List<String> attacksList, List<Argument> arguments) {
		List<String> attackMemLines = new ArrayList<>();
		List<AttackRelation> relations = new ArrayList<>();
		for (String attackStr : attacksList) {
			if (attackStr.startsWith("mem")) {
				attackMemLines.add(attackStr);
				continue;
			}
			String attack = attackStr.substring(4, attackStr.length() - 1);
			AttackRelation att = new AttackRelation();
			att.setAttackLabel(attack.split(",")[0]);
			String attacked = attack.split(",")[1];
			for (Argument arg : arguments) {
				if (arg.getLabel().equals(attacked)) {
					att.setAttacked(arg);
				}
			}
			relations.add(att);
		}
		populateMembers(relations, attackMemLines, arguments);
		return relations;
	}

	private static void populateMembers(List<AttackRelation> relations, List<String> attMemLines,
			List<Argument> arguments) {
		for (String member : attMemLines) {
			String memberVal = member.substring(4, member.length() - 1);
			String relation = memberVal.split(",")[0];
			String support = memberVal.split(",")[1];
			for (AttackRelation relationObj : relations) {
				if (relationObj.getAttackLabel().equals(relation)) {
					relationObj.getAttackMembers().add(support);
					for (Argument arg : arguments) {
						if (arg.getLabel().equals(support)) {
							relationObj.getAttackers().add(arg);
						}
					}
				}
			}
		}
	}

}
