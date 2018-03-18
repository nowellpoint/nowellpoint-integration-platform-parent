package com.nowellpoint.signup.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageProvider {
	
	public static final String REGISTRATION_ID_NOT_FOUND = "registration.id.not.found"; 
	
	public static String getMessage(Locale locale, String key) {
		return ResourceBundle.getBundle("messages", locale).getString(key);
	}
}