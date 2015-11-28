package com.nowellpoint.aws.model.sforce;

import java.io.Serializable;

import com.nowellpoint.aws.model.AbstractLambdaRequest;

public class RevokeTokenRequest extends AbstractLambdaRequest implements Serializable {
	
	private static final long serialVersionUID = -1542764419372202544L;
	
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