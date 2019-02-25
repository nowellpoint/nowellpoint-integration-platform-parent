package com.nowellpoint.client.sforce;

import lombok.Getter;

public class OauthException extends RuntimeException {
	
	private static final long serialVersionUID = 2726007023971974319L;
	
	@Getter private int statusCode;
	@Getter private String error;
	@Getter private String errorDescription;
	
	public OauthException(int statusCode, ApiError error) {
		super(error.getErrorDescription());
		this.statusCode = statusCode;
		this.error = error.getError();
		this.errorDescription = error.getErrorDescription();
	}
}