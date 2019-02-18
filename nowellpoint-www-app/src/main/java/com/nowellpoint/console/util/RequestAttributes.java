package com.nowellpoint.console.util;

import java.util.Locale;
import java.util.TimeZone;

import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.model.Token;

import spark.Request;

public class RequestAttributes {
	public static final String AUTH_TOKEN = "com.nowellpoint.auth.token";
	public static final String IDENTITY = "com.nowellpoint.auth.identity";
	public static final String LOCALE = "com.nowellpoint.default.locale";
	public static final String TIME_ZONE = "com.nowellpoint.default.timezone";
	
	public static Token getToken(Request request) {
		return request.attribute(RequestAttributes.AUTH_TOKEN);
	}
	
	public static Identity getIdentity(Request request) {
		return request.attribute(RequestAttributes.IDENTITY);
	}
	
	public static Locale getLocale(Request request) {
		return request.attribute(RequestAttributes.LOCALE);
	}
	
	public static TimeZone getTimeZone(Request request) {		
		return request.attribute(RequestAttributes.TIME_ZONE);
	}
}