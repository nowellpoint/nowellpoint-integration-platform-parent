package com.nowellpoint.client.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageProvider {
	
	public static String getMessage(Locale locale, String key) {
		return ResourceBundle.getBundle("com.nowellpoint.client.messages", locale).getString(key);
	}
}