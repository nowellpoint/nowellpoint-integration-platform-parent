package com.nowellpoint.client.sforce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DescribeSobjectResult {
	
	private Sobject sobject;
	
	public DescribeSobjectResult() {
		
	}

	public Sobject getSobject() {
		return sobject;
	}

	public void setSobject(Sobject sobject) {
		this.sobject = sobject;
	}
}