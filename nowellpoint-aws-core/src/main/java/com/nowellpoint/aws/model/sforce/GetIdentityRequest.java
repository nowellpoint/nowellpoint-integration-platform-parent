package com.nowellpoint.aws.model.sforce;

import java.io.Serializable;

import com.nowellpoint.aws.model.AbstractLambdaRequest;

public class GetIdentityRequest extends AbstractLambdaRequest implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -3510126823687923328L;
	
	private String id;
	
	private String accessToken;

	public GetIdentityRequest() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public GetIdentityRequest withId(String id) {
		setId(id);
		return this;
	}
	
	public GetIdentityRequest withAccessToken(String accessToken) {
		setAccessToken(accessToken);
		return this;
	}
}