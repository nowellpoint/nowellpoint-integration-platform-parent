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
	public static final String CONNECT_USER = "connect-user.ftl";
	public static final String START = String.format(APPLICATION_CONTEXT, "start.ftl");
	public static final String DASHBOARD = String.format(APPLICATION_CONTEXT, "dashboard.html");
	public static final String ADMINISTRATION = String.format(APPLICATION_CONTEXT, "administration.html");
	public static final String USER_PROFILE = String.format(APPLICATION_CONTEXT, "user-profile.ftl");
	public static final String USER_PROFILE_INFORMATION = String.format(APPLICATION_CONTEXT, "identity-information.ftl");
	public static final String ORGANIZATION = String.format(APPLICATION_CONTEXT, "organization.ftl");
	public static final String ORGANIZATION_CHANGE_PLAN = String.format(APPLICATION_CONTEXT, "organization-change-plan.html");
	public static final String ORGANIZATION_PAYMENT_METHOD = String.format(APPLICATION_CONTEXT, "organization-payment-method.html");
	public static final String ORGANIZATION_BILLING_ADDRESS = String.format(APPLICATION_CONTEXT, "organization-billing-address.html");
	public static final String ORGANIZATION_BILLING_CONTACT = String.format(APPLICATION_CONTEXT, "organization-billing-contact.html");
}