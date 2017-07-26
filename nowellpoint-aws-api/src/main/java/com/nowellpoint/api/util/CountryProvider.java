package com.nowellpoint.api.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class CountryProvider {
	
	public static String getCountry(Locale locale, String key) {
		return ResourceBundle.getBundle("countries", locale).getString(key);
	}
}