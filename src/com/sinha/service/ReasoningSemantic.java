package com.sinha.service;

import com.sinha.model.ArgumentFramework;

public interface ReasoningSemantic {

	void generateLabelings(ArgumentFramework af, String uid) throws Exception;
}
