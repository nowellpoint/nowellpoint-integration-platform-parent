package com.nowellpoint.console.entity;

import java.util.Locale;

public class Preferences {
	
	private String timeZone;
	
	private Locale locale;

	public Preferences() {
		
	}
	
	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}