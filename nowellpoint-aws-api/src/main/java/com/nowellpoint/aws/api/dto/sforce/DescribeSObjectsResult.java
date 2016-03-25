package com.nowellpoint.aws.api.dto.sforce;

import java.io.Serializable;
import java.util.List;

public class DescribeSObjectsResult implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 7907186834504971645L;
	
	private String encoding;
	
	private Integer maxBatchSize;
	
	private List<SObject> sobjects;
	
	public DescribeSObjectsResult() {
		
	}
	
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public Integer getMaxBatchSize() {
		return maxBatchSize;
	}

	public void setMaxBatchSize(Integer maxBatchSize) {
		this.maxBatchSize = maxBatchSize;
	}

	public List<SObject> getSobjects() {
		return sobjects;
	}

	public void setSobjects(List<SObject> sobjects) {
		this.sobjects = sobjects;
	}
}