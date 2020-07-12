package com.sinha.service;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.sinha.model.ArgumentFramework;
import com.sinha.model.Candidate;
import com.sinha.util.ComputeExtensions;

@Service
public class PreferredSemantic implements ReasoningSemantic {

	private static final Logger logger = LoggerFactory.getLogger(PreferredSemantic.class);

	private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);

	@Autowired
	private SimpMessagingTemplate template;

	@Override
	public void generateLabelings(ArgumentFramework af, String uid) throws Exception {
		logger.info("Generating Preferred Extensions");
		List<String> argList = af.getArguments().stream().map(s -> s.getLabel()).collect(Collectors.toList());
		Candidate candidate = new Candidate();
		candidate.setUndecArguments(new HashSet<>(argList));
		Runnable run = new ComputeExtensions(af, candidate, template, uid);
		EXECUTOR.execute(run);
		return;
	}

}
