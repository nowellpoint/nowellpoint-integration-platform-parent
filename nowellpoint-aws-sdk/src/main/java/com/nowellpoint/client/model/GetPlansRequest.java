package com.nowellpoint.client.model;

public class GetPlansRequest {
	
	private String localeSidKey;
	
	private String languageSidKey;
	
	public GetPlansRequest() {
		
	}

	public String getLocaleSidKey() {
		return localeSidKey;
	}

	public void setLocaleSidKey(String localeSidKey) {
		this.localeSidKey = localeSidKey;
	}

	public String getLanguageSidKey() {
		return languageSidKey;
	}

	public void setLanguageSidKey(String languageSidKey) {
		this.languageSidKey = languageSidKey;
	}
	
	public GetPlansRequest withLocaleSidKey(String localeSidKey) {
		setLocaleSidKey(localeSidKey);
		return this;
	}
	
	public GetPlansRequest withLanguageSidKey(String languageSidKey) {
		setLanguageSidKey(languageSidKey);
		return this;
	}
}