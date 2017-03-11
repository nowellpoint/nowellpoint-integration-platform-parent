package com.nowellpoint.api.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageProvider {
	
	public static String getMessage(Locale locale, String key) {
		return ResourceBundle.getBundle("messages", locale).getString(key);
	}
}