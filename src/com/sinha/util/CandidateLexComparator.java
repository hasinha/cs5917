package com.sinha.util;

import java.util.Comparator;

import com.sinha.model.Candidate;

public class CandidateLexComparator implements Comparator<Candidate> {

	@Override
	public int compare(Candidate arg1, Candidate arg2) {
		return arg1.getInArgRanks().compareTo(arg2.getInArgRanks());
	}
}
