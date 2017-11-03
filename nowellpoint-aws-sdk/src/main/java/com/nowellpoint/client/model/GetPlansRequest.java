package com.nowellpoint.client.model;

import java.util.Locale;

public class GetPlansRequest {
	
	private Locale locale;
	
	private String language;
	
	public GetPlansRequest() {
		
	}
	
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public GetPlansRequest withLocale(Locale locale) {
		setLocale(locale);
		return this;
	}
	
	public GetPlansRequest withLanguage(String language) {
		setLanguage(language);
		return this;
	}
}