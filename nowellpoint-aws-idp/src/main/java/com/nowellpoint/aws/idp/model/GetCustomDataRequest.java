package com.nowellpoint.aws.idp.model;

import org.hibernate.validator.constraints.NotEmpty;

public class GetCustomDataRequest extends AbstractIdpRequest {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 5375223906096061481L;
	
	private String accessToken;
	
	public GetCustomDataRequest() {
		
	}
	
	@NotEmpty
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	@NotEmpty
	public String getApiEndpoint() {
		return super.getApiEndpoint();
	}
	
	public void setApiEndpoint(String apiEndpoint) {
		super.setApiEndpoint(apiEndpoint);
	}

	public GetCustomDataRequest withAccessToken(String accessToken) {
		setAccessToken(accessToken);
		return this;
	}
	
	public GetCustomDataRequest withApiEndpoint(String apiEndpoint) {
		setApiEndpoint(apiEndpoint);
		return this;
	}

	public GetCustomDataRequest withApiKeyId(String apiKeyId) {
		setApiKeyId(apiKeyId);
		return this;
	}
	
	public GetCustomDataRequest withApiKeySecret(String apiKeySecret) {
		setApiKeySecret(apiKeySecret);
		return this;
	}
}