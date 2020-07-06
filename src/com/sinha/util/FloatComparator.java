package com.sinha.util;

import java.util.Comparator;

import com.sinha.model.Argument;

public class FloatComparator implements Comparator<Argument> {

	@Override
	public int compare(Argument arg0, Argument arg1) {
		return Float.valueOf(arg1.getStrengthValue()).compareTo(Float.valueOf(arg0.getStrengthValue()));
	}

}
