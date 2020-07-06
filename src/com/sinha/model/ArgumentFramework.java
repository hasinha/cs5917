package com.sinha.model;

import java.util.List;

public class ArgumentFramework {

	private List<Argument> arguments;

	private List<AttackRelation> attackRelation;

	public List<Argument> getArguments() {
		return arguments;
	}

	public void setArguments(List<Argument> arguments) {
		this.arguments = arguments;
	}

	public List<AttackRelation> getAttackRelation() {
		return attackRelation;
	}

	public void setAttackRelation(List<AttackRelation> attackRelation) {
		this.attackRelation = attackRelation;
	}
}
