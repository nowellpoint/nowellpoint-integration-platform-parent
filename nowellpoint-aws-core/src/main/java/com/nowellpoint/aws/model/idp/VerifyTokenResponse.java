package com.nowellpoint.aws.model.idp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.aws.model.AbstractLambdaResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyTokenResponse extends AbstractLambdaResponse {

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