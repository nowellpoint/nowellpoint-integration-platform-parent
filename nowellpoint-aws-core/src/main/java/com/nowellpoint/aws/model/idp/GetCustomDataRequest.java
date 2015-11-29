package com.nowellpoint.aws.model.idp;

import com.nowellpoint.aws.model.AbstractLambdaRequest;

public class GetCustomDataRequest extends AbstractLambdaRequest {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 5375223906096061481L;
	
	private String accessToken;
	
	public GetCustomDataRequest() {
		
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public GetCustomDataRequest withAccessToken(String accessToken) {
		setAccessToken(accessToken);
		return this;
	}
}