package com.sinha.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AttackRelation {

	private String attackLabel;

	private List<String> attackMembers = new ArrayList<>();

	private Set<Argument> attackers = new HashSet<>();

	private Argument attacked;

	public String getAttackLabel() {
		return attackLabel;
	}

	public void setAttackLabel(String attackLabel) {
		this.attackLabel = attackLabel;
	}

	public List<String> getAttackMembers() {
		return attackMembers;
	}

	public void setAttackMembers(List<String> attackMembers) {
		this.attackMembers = attackMembers;
	}

	public Set<Argument> getAttackers() {
		return attackers;
	}

	public void setAttackers(Set<Argument> attackers) {
		this.attackers = attackers;
	}

	public Argument getAttacked() {
		return attacked;
	}

	public void setAttacked(Argument attacked) {
		this.attacked = attacked;
	}

	public boolean isAttackerContained(Argument argument) {
		return attackers.contains(argument);
	}

	public boolean isAttackedPresent(Argument argument) {
		return attacked.equals(argument);
	}

	@Override
	public String toString() {
		return "AttackRelation [attackers=" + attackers + ", attacked=" + attacked + "]";
	}
}
