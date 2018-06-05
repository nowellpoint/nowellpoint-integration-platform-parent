package com.nowellpoint.console.entity;

import java.io.Serializable;
import java.util.Locale;

public class Preferences implements Serializable {
	
	private static final long serialVersionUID = -379308611788575877L;

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