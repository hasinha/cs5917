package com.sinha.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sinha.model.Argument;

@Service
public class NhCatBasedSemantic implements RankingSemantic {

	@Override
	public void generateRanks(List<Argument> arguments) {
		return;
	}

}
