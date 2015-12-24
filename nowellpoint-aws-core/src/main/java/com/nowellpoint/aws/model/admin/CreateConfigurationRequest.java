package com.nowellpoint.aws.model.admin;

import com.nowellpoint.aws.model.AbstractLambdaRequest;

public class CreateConfigurationRequest extends AbstractLambdaRequest {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 350380797564170812L;
	
	private String payload;
	
	public CreateConfigurationRequest() {
		
	}

	public String getPayload() {
		return decode(payload);
	}

	public void setPayload(String payload) {
		this.payload = encode(payload);
	}
	
	public CreateConfigurationRequest withPayload(String payload) {
		setPayload(payload);
		return this;
	}
}