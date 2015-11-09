package com.nowellpoint.aws.lambda.idp.model;

import java.io.Serializable;

public class GetTokenResponse implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -317791542723037367L;
	
    private Integer statusCode;
	
	private Token token;
	
	private String errorCode;
	
	private String errorMessage;

	public GetTokenResponse() {
		
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}