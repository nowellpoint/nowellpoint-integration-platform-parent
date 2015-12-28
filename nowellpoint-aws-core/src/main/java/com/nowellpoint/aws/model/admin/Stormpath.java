package com.nowellpoint.aws.model.admin;

import java.io.Serializable;

public class Stormpath implements Serializable {
	
	private static final long serialVersionUID = -7077573035663609126L;

	private String apiKeyId;
	
	private String apiKeySecret;

	public Stormpath() {
		
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
}