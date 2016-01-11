package com.nowellpoint.aws.model.sforce;

import java.io.Serializable;

import com.nowellpoint.aws.model.AbstractLambdaRequest;

public class RevokeTokenRequest extends AbstractLambdaRequest implements Serializable {
	
	private static final long serialVersionUID = -1542764419372202544L;
	
    private String revokeTokenUri;
	
	private String accessToken;
	
	public RevokeTokenRequest() {
		
	}
	
	public String getRevokeTokenUri() {
		return revokeTokenUri;
	}

	public void setRevokeTokenUri(String revokeTokenUri) {
		this.revokeTokenUri = revokeTokenUri;
	}

	public String getAccessToken() {
		return accessToken;
	}
	
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public RevokeTokenRequest withRevokeTokenUri(String revokeTokenUri) {
		setRevokeTokenUri(revokeTokenUri);
		return this;
	}
	
	public RevokeTokenRequest withAccessToken(String accessToken) {
		setAccessToken(accessToken);
		return this;
	}
}