package com.nowellpoint.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {
	
	protected final static SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
	
	public static Date iso8601(String dateValue) throws ParseException {
		if (dateValue == null || dateValue.trim().isEmpty()) {
			return null;
		}
		return iso8601Format.parse(dateValue);
	}
}