package com.nowellpoint.aws.idp.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.aws.model.AbstractResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyTokenResponse extends AbstractResponse implements Serializable {

	private static final long serialVersionUID = -2566366296041045154L;
	
	private AuthToken authToken;

	public VerifyTokenResponse() {
		
	}

	public AuthToken getAuthToken() {
		return authToken;
	}

	public void setAuthToken(AuthToken authToken) {
		this.authToken = authToken;
	}
}