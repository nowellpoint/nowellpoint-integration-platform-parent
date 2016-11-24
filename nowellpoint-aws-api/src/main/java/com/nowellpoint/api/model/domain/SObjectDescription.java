package com.nowellpoint.api.model.domain;

import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;

public class SObjectDescription extends AbstractResource {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3740458498780531334L;
	
	private String environmentKey;
	
	private String name;
	
	private DescribeSobjectResult result;
	
	public SObjectDescription() {
		
	}

	public String getEnvironmentKey() {
		return environmentKey;
	}

	public void setEnvironmentKey(String environmentKey) {
		this.environmentKey = environmentKey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DescribeSobjectResult getResult() {
		return result;
	}

	public void setResult(DescribeSobjectResult result) {
		this.result = result;
	}
}