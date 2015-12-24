package com.nowellpoint.aws.model.admin;

import com.nowellpoint.aws.model.AbstractLambdaResponse;

public class CreateConfigurationResponse extends AbstractLambdaResponse {
	
	private static final long serialVersionUID = 7454196046925853087L;
	
	private String id;
	
	public CreateConfigurationResponse() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}