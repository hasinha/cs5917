package com.sinha.model;

import java.util.List;

public class Argument {

	private String label;

	private List<Argument> attacks;

	private List<Argument> attackedBy;

	public Argument(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "Argument [label=" + label + ", attacks=" + attacks + ", attackedBy=" + attackedBy + "]";
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<Argument> getAttacks() {
		return attacks;
	}

	public void setAttacks(List<Argument> attacks) {
		this.attacks = attacks;
	}

	public List<Argument> getAttackedBy() {
		return attackedBy;
	}

	public void setAttackedBy(List<Argument> attackedBy) {
		this.attackedBy = attackedBy;
	}
}
