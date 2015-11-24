package com.nowellpoint.aws.model.idp;

import com.nowellpoint.aws.model.AbstractRequest;

public class GetAccountRequest extends AbstractRequest {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 350380797564170812L;

	private String apiKeyId;
	
	private String apiKeySecret;
	
	public GetAccountRequest() {
		
	}

	public String getApiKeyId() {
		return apiKeyId;
	}

	public void setApiKeyId(String apiKeyId) {
		this.apiKeyId = apiKeyId;
	}

	public String getApiKeySecret() {
		return apiKeySecret;
	}

	public void setApiKeySecret(String apiKeySecret) {
		this.apiKeySecret = apiKeySecret;
	}
	
	public GetAccountRequest withApiKeyId(String apiKeyId) {
		setApiKeyId(apiKeyId);
		return this;
	}
	
	public GetAccountRequest withApiKeySecret(String apiKeySecret) {
		setApiKeySecret(apiKeySecret);
		return this;
	}
}