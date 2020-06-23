package com.sinha.service;

import java.util.ArrayList;
import java.util.List;

import com.sinha.model.Argument;

public class ArgumentUtils {

	public static List<Argument> parseArguments(List<String> lines) {
		List<String> attacks = new ArrayList<>();
		List<Argument> arguments = new ArrayList<>();
		for (String line : lines) {
			if (line.startsWith("att")) {
				attacks.add(line);
				continue;
			}
			String label = line.substring(4, line.length());
			Argument arg = new Argument(label);
			arguments.add(arg);
		}
		return arguments;
	}

}
