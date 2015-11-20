package com.nowellpoint.aws.model.idp;

import com.nowellpoint.aws.model.AbstractRequest;

public class VerifyTokenRequest extends AbstractRequest {

	private static final long serialVersionUID = 4462868491415777411L;
	
	private String accessToken;
	
	public VerifyTokenRequest() {
		
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public VerifyTokenRequest withAccessToken(String accessToken) {
		setAccessToken(accessToken);
		return this;
	}
}