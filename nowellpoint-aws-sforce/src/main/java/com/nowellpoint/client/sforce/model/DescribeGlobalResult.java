package com.nowellpoint.client.sforce.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nowellpoint.client.sforce.model.sobject.SObject;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DescribeGlobalResult implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 7907186834504971645L;
	
	@JsonProperty("encoding")
	private String encoding;
	
	@JsonProperty("maxBatchSize")
	private Integer maxBatchSize;
	
	@JsonProperty("sobjects")
	private List<SObject> sobjects;
	
	public DescribeGlobalResult() {
		
	}
	
	public String getEncoding() {
		return encoding;
	}

	public Integer getMaxBatchSize() {
		return maxBatchSize;
	}
	
	public List<SObject> getSObjects() {
		return sobjects;
	}
}