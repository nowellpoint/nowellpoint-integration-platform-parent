package com.nowellpoint.console.util;

public class Templates {
	private static final String APPLICATION_CONTEXT = "/app/%s";
	public static final String INDEX = "index.html";
	public static final String LOGIN = "login.html";
	public static final String CONTACT = "contact.html";
	public static final String SIGN_UP = "signup.html";
	public static final String NOT_FOUND = "not-found.html";
	public static final String ACTIVATE_ACCOUNT = "activate.html";
	public static final String SECURE_ACCOUNT = "secure.html";
	public static final String SALESFORCE_OAUTH = String.format(APPLICATION_CONTEXT, "salesforce-oauth.html");
	public static final String SALESFORCE_OAUTH_CALLBACK = String.format(APPLICATION_CONTEXT, "salesforce-oauth-callback.html");
	public static final String CONSOLE = String.format(APPLICATION_CONTEXT, "console.html");
	public static final String START = String.format(APPLICATION_CONTEXT, "start.html");
	public static final String USER_PROFILE = String.format(APPLICATION_CONTEXT, "user-profile.html");
	public static final String USER_PROFILE_INFORMATION = String.format(APPLICATION_CONTEXT, "user-profile-information.html");
	public static final String ORGANIZATION = String.format(APPLICATION_CONTEXT, "organization.html");
	public static final String ORGANIZATION_CHANGE_PLAN = String.format(APPLICATION_CONTEXT, "organization-change-plan.html");
	public static final String ORGANIZATION_PAYMENT_METHOD = String.format(APPLICATION_CONTEXT, "organization-payment-method.html");
	public static final String ORGANIZATION_BILLING_ADDRESS = String.format(APPLICATION_CONTEXT, "organization-billing-address.html");
	public static final String ORGANIZATION_BILLING_CONTACT = String.format(APPLICATION_CONTEXT, "organization-billing-contact.html");
}