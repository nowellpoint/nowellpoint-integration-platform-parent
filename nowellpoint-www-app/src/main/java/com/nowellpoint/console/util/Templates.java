package com.nowellpoint.console.util;

public class Templates {
	private static final String APPLICATION_CONTEXT = "/app/%s";
	public static final String INDEX = "index.html";
	public static final String LOGIN = "login.html";
	public static final String CONTACT = "contact.html";
	public static final String NOT_FOUND = "not-found.html";
	public static final String CONSOLE = String.format(APPLICATION_CONTEXT, "console.html");
	public static final String START = String.format(APPLICATION_CONTEXT, "start.html");
	public static final String USER_PROFILE = String.format(APPLICATION_CONTEXT, "user-profile.html");
	public static final String USER_PROFILE_INFORMATION = String.format(APPLICATION_CONTEXT, "user-profile-information.html");
	public static final String USER_PROFILE_ADDRESS = String.format(APPLICATION_CONTEXT, "user-profile-address.html");
}