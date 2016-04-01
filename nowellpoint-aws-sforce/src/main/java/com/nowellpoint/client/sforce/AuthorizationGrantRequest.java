package com.nowellpoint.client.sforce;

public class AuthorizationGrantRequest {

	
	private String code;
	
	public AuthorizationGrantRequest(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
}
