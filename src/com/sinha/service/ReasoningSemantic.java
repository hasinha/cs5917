package com.sinha.service;

import java.util.List;

import com.sinha.model.ArgumentFramework;
import com.sinha.model.Candidate;

public interface ReasoningSemantic {

	void generateLabelings(ArgumentFramework af) throws Exception;
}
