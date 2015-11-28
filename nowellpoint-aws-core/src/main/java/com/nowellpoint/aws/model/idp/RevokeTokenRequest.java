package com.nowellpoint.aws.model.idp;

import com.nowellpoint.aws.model.AbstractLambdaRequest;

public class RevokeTokenRequest extends AbstractLambdaRequest {
	
	private static final long serialVersionUID = 2302532913990733635L;
	
	private String accessToken;
	
	public RevokeTokenRequest() {
		
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public RevokeTokenRequest withAccessToken(String accessToken) {
		setAccessToken(accessToken);
		return this;
	}
}