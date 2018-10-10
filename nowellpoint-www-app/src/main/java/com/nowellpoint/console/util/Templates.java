package com.nowellpoint.console.util;

public class Templates {
	private static final String APPLICATION_CONTEXT = "/app/%s";
	public static final String INDEX = "index.ftl";
	public static final String LOGIN = "login.ftl";
	public static final String CONTACT = "contact.ftl";
	public static final String SIGN_UP = "signup.ftl";
	public static final String ERROR = "error.ftl";
	public static final String NOT_FOUND = "not-found.ftl";
	public static final String ACTIVATE_ACCOUNT = "activate.ftl";
	public static final String SECURE_ACCOUNT = "secure.ftl";
	public static final String SALESFORCE_OAUTH = "salesforce-oauth.ftl";
	public static final String SALESFORCE_OAUTH_CALLBACK = "salesforce-oauth-callback.ftl";
	public static final String SALESFORCE_OAUTH_SUCCESS = "salesforce-oauth-success.html";
	public static final String CONSOLE = String.format(APPLICATION_CONTEXT, "console.html");
	public static final String START = String.format(APPLICATION_CONTEXT, "start.ftl");
	public static final String DASHBOARD = String.format(APPLICATION_CONTEXT, "dashboard.html");
	public static final String IDENTITY = String.format(APPLICATION_CONTEXT, "user-profile.ftl");
	public static final String IDENTITY_INFORMATION = String.format(APPLICATION_CONTEXT, "identity-information.ftl");
	public static final String ORGANIZATION = String.format(APPLICATION_CONTEXT, "organization.ftl");
	public static final String ORGANIZATION_CHANGE_PLAN = String.format(APPLICATION_CONTEXT, "organization-change-plan.html");
	public static final String ORGANIZATION_PAYMENT_METHOD = String.format(APPLICATION_CONTEXT, "organization-payment-method.html");
	public static final String ORGANIZATION_BILLING_ADDRESS = String.format(APPLICATION_CONTEXT, "organization-billing-address.html");
	public static final String ORGANIZATION_BILLING_CONTACT = String.format(APPLICATION_CONTEXT, "organization-billing-contact.html");
}