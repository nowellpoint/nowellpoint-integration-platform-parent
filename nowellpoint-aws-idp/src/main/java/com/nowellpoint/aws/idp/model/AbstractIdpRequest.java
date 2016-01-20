package com.nowellpoint.aws.idp.model;

import org.hibernate.validator.constraints.NotEmpty;

import com.nowellpoint.aws.model.AbstractLambdaRequest;

public abstract class AbstractIdpRequest extends AbstractLambdaRequest {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 308976666801125491L;
	
	private String apiEndpoint;
	
	private String apiKeyId;
	
	private String apiKeySecret;
	
	private String applicationId;
	
	private String directoryId;

	public AbstractIdpRequest() {
		
	}

	protected String getApiEndpoint() {
		return apiEndpoint;
	}

	protected void setApiEndpoint(String apiEndpoint) {
		this.apiEndpoint = apiEndpoint;
	}

	@NotEmpty
	public String getApiKeyId() {
		return apiKeyId;
	}

	public void setApiKeyId(String apiKeyId) {
		this.apiKeyId = apiKeyId;
	}

	@NotEmpty
	public String getApiKeySecret() {
		return apiKeySecret;
	}

	public void setApiKeySecret(String apiKeySecret) {
		this.apiKeySecret = apiKeySecret;
	}
	
	protected String getApplicationId() {
		return applicationId;
	}
	
	protected void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	
	protected String getDirectoryId() {
		return directoryId;
	}
	
	protected void setDirectoryId(String directoryId) {
		this.directoryId = directoryId;
	}
}