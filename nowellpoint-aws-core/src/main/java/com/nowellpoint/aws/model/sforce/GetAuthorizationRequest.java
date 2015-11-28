package com.nowellpoint.aws.model.sforce;

import java.io.Serializable;

import com.nowellpoint.aws.model.AbstractLambdaRequest;

public class GetAuthorizationRequest extends AbstractLambdaRequest implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 8458926047232428590L;
	
	private String code;

	public GetAuthorizationRequest() {
		
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public GetAuthorizationRequest withCode(String code) {
		setCode(code);
		return this;
	}
}