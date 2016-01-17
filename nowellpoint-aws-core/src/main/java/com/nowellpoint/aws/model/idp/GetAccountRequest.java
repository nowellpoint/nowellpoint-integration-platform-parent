package com.nowellpoint.aws.model.idp;

import org.hibernate.validator.constraints.NotEmpty;

public class GetAccountRequest extends AbstractIdpRequest {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 350380797564170812L;
	
	private String href;
	
	public GetAccountRequest() {
		
	}
	
	@NotEmpty
	public String getHref() {
		return href;
	}
	
	public void setHref(String href) {
		this.href = href;
	}
	
	public GetAccountRequest withApiKeyId(String apiKeyId) {
		setApiKeyId(apiKeyId);
		return this;
	}
	
	public GetAccountRequest withApiKeySecret(String apiKeySecret) {
		setApiKeySecret(apiKeySecret);
		return this;
	}
	
	public GetAccountRequest withHref(String href) {
		setHref(href);
		return this;
	}
}