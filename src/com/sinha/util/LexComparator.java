package com.sinha.util;

import java.util.Comparator;

import com.sinha.model.Argument;

public class LexComparator implements Comparator<Argument> {

	@Override
	public int compare(Argument arg1, Argument arg2) {
		return arg1.getPathCountStr().compareTo(arg2.getPathCountStr());
	}

}
