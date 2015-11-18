package com.nowellpoint.aws.sforce.model;

import java.io.Serializable;

import com.nowellpoint.aws.model.AbstractRequest;

public class GetAuthorizationRequest extends AbstractRequest implements Serializable {

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