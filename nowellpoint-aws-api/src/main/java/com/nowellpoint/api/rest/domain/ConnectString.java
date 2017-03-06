package com.nowellpoint.api.rest.domain;

public class ConnectString {
	
	//salesforce://refreshToken@https://login.salesforce.com?grant_type=refresh_token
	
	private String uri;
	
	public ConnectString() {
		
	}
	
	private ConnectString(String uri) {
		this.uri = uri;
	}
	
	public static ConnectString salesforce(String refreshToken, String identityId) {
		String uri = "salesforce://".concat(refreshToken)
				.concat("@")
				.concat(identityId)
				.concat("?grant_type=refresh_token");
		
		return new ConnectString(uri);
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}