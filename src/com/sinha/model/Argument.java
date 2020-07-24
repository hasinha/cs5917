package com.sinha.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Argument {

	private String label;

	private float strengthValue;

	private float newStrengthValue;

	private String pathCountStr;

	private int rank;

	public Argument(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public float getStrengthValue() {
		return strengthValue;
	}

	public void setStrengthValue(float strengthValue) {
		this.strengthValue = strengthValue;
	}

	public float getNewStrengthValue() {
		return newStrengthValue;
	}

	public void setNewStrengthValue(float newStrengthValue) {
		this.newStrengthValue = newStrengthValue;
	}

	public String getPathCountStr() {
		return pathCountStr;
	}

	public void setPathCountStr(String pathCountStr) {
		this.pathCountStr = pathCountStr;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Argument other = (Argument) obj;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Argument [label=" + label + ", strengthValue=" + strengthValue + "]";
	}

}
