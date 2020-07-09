package com.sinha.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Candidate {

	private Set<String> inArguments = new HashSet<>();

	private Set<String> outArguments = new HashSet<>();

	private Set<String> undecArguments = new HashSet<>();

	public Set<String> getInArguments() {
		return inArguments;
	}

	public void setInArguments(Set<String> inArguments) {
		this.inArguments = inArguments;
	}

	public Set<String> getOutArguments() {
		return outArguments;
	}

	public void setOutArguments(Set<String> outArguments) {
		this.outArguments = outArguments;
	}

	public Set<String> getUndecArguments() {
		return undecArguments;
	}

	public void setUndecArguments(Set<String> undecArguments) {
		this.undecArguments = undecArguments;
	}

	public Candidate clone() {
		Candidate cand = new Candidate();
		cand.setInArguments(new HashSet<>(this.getInArguments()));
		cand.setOutArguments(new HashSet<>(this.getOutArguments()));
		cand.setUndecArguments(new HashSet<>(this.getUndecArguments()));
		return cand;
	}

	public void markIn(String arg) {
		this.inArguments.add(arg);
		this.outArguments.remove(arg);
		this.undecArguments.remove(arg);
	}

	public void markUndec(String arg) {
		this.inArguments.remove(arg);
		this.outArguments.remove(arg);
		this.undecArguments.add(arg);
	}

	public void markOut(String arg) {
		this.inArguments.remove(arg);
		this.outArguments.add(arg);
		this.undecArguments.remove(arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inArguments == null) ? 0 : inArguments.hashCode());
		result = prime * result + ((outArguments == null) ? 0 : outArguments.hashCode());
		result = prime * result + ((undecArguments == null) ? 0 : undecArguments.hashCode());
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
		Candidate other = (Candidate) obj;
		if (inArguments == null) {
			if (other.inArguments != null)
				return false;
		} else if (!inArguments.equals(other.inArguments))
			return false;
		if (outArguments == null) {
			if (other.outArguments != null)
				return false;
		} else if (!outArguments.equals(other.outArguments))
			return false;
		if (undecArguments == null) {
			if (other.undecArguments != null)
				return false;
		} else if (!undecArguments.equals(other.undecArguments))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Candidate [inArguments=" + inArguments + ", outArguments=" + outArguments + ", undecArguments="
				+ undecArguments + "]";
	}
}
