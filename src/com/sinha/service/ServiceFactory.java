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

	@Autowired
	private NhCatBasedSemantic nhCategoriser;

	@Autowired
	private DiscussionBasedSemantic discussionSemantic;

	@PostConstruct
	private void postConstruct() {
		rankingFactory = new HashMap<>();
		rankingFactory.put("nhCat", nhCategoriser);
		rankingFactory.put("discussion", discussionSemantic);
	}

	public Map<String, RankingSemantic> getRankingFactory() {
		return rankingFactory;
	}
}