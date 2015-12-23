package com.nowellpoint.aws.model.sforce;

import com.nowellpoint.aws.model.AbstractLambdaResponse;

public class CreateSObjectResponse extends AbstractLambdaResponse {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 6237973241119968784L;
	
	private String id;
	
	public CreateSObjectResponse() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}