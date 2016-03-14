package com.nowellpoint.aws.api.dto.sforce;

import java.io.Serializable;

public class DescribeSObjectsResult implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 7907186834504971645L;
	
	private String encoding;
	
	private Integer maxBatchSize;
	
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



}
