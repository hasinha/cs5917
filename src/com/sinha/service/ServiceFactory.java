package com.sinha.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceFactory {

	private static final Logger logger = LoggerFactory.getLogger(ServiceFactory.class);

	private Map<String, RankingSemantic> rankingFactory;

	private Map<String, ReasoningSemantic> extensionFactory;

	@Autowired
	private NhCatBasedSemantic nhCategoriser;

	@Autowired
	private DiscussionBasedSemantic discussionSemantic;

	@Autowired
	private MeanBurdenBasedSemantics meanBurdenBasedSemantic;

	@Autowired
	private MinBurdenSemantic minBurdenSemantic;

	@Autowired
	private MeanCategoriser meanCategoriser;

	@Autowired
	private PreferredSemantic preferredSemantic;

	@PostConstruct
	private void postConstruct() {
		rankingFactory = new HashMap<>();
		extensionFactory = new HashMap<>();
		rankingFactory.put("nhCat", nhCategoriser);
		rankingFactory.put("discussion", discussionSemantic);
		rankingFactory.put("meanBurden", meanBurdenBasedSemantic);
		rankingFactory.put("minBurden", minBurdenSemantic);
		rankingFactory.put("meanCat", meanCategoriser);
		extensionFactory.put("prefExt", preferredSemantic);
	}

	public RankingSemantic getRankingSemantic(String semantic) {
		logger.info("Returning ranking semantic for : {}", semantic);
		return rankingFactory.get(semantic);
	}

	public ReasoningSemantic getReasoningSemantic(String semantic) {
		logger.info("Return reasoning semantic for: {}", semantic);
		return extensionFactory.get(semantic);
	}
}
