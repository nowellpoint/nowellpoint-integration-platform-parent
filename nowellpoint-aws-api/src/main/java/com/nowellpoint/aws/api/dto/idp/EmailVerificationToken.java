package com.nowellpoint.aws.api.dto.idp;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailVerificationToken implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4028402953654987070L;
	
	@JsonProperty(value="href")
	private String href;
	
	public EmailVerificationToken() {
		
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}
}