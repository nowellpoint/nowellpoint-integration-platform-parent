package com.nowellpoint.client.model;

public class GetPlansRequest {
	
	private String locale;
	
	private String language;
	
	public GetPlansRequest() {
		
	}
	
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public GetPlansRequest withLocale(String locale) {
		setLocale(locale);
		return this;
	}
	
	public GetPlansRequest withLanguage(String language) {
		setLanguage(language);
		return this;
	}
}