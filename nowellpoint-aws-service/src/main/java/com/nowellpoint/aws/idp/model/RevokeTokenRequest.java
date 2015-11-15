package com.nowellpoint.aws.idp.model;

import java.io.Serializable;

import com.nowellpoint.aws.model.AbstractRequest;

public class RevokeTokenRequest extends AbstractRequest implements Serializable {
	
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