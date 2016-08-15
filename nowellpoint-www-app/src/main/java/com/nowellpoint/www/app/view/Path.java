package com.nowellpoint.www.app.view;

public class Path {
	
	/**
	 *  
	 * @author jherson
	 *
	 */
	
	public static class Route {
		public static final String INDEX = "/";
		public static final String LOGIN = "/login";
		public static final String LOGOUT = "/logout"; 
		public static final String SIGN_UP = "/signup";
		public static final String CONTACT_US = "/contact";
		public static final String SERVICES = "/services";
		public static final String HEALTH_CHECK = "/healthcheck";
		public static final String VERIFY_EMAIL = "/verify-email";
		public static final String START = "/app/start";
		public static final String DASHBOARD = "/app/dashboard";
		public static final String NOTIFICATIONS = "/app/notifications";
		public static final String SETUP = "/app/setup";
		public static final String APPLICATIONS = "/app/applications";
		public static final String PROJECTS = "/app/projects";
		public static final String PROVIDERS = "/app/providers";
		public static final String CONNECTORS_SALESFORCE = "/app/connectors/salesforce";
		public static final String SALESFORCE_OAUTH = "/app/salesforce/oauth";
		public static final String ACCOUNT_PROFILE = "/app/account-profile/:id";
		public static final String ACCOUNT_PROFILE_ADDRESS = "/app/account-profile/:id/address";
		public static final String ACCOUNT_PROFILE_PAYMENT_METHODS = "/app/account-profile/:id/payment-methods";
		public static final String ADMINISTRATION = "/app/administration";
	}

	public static class Template {
		public static final String INDEX = "index.html";
		public static final String LOGIN = "login.html";
		public static final String SIGN_UP = "signup.html";
		public static final String CONTACT_US = "contact.html";
		public static final String SERVICES = "services.html";
		public static final String VERIFY_EMAIL = "verify-email.html";
		public static final String START = "secure/start.html";
		public static final String DASHBOARD = "secure/dashboard.html";
		public static final String SETUP = "secure/setup.html";
		public static final String APPLICATION = "secure/application.html";
		public static final String APPLICATION_EDIT = "secure/application-edit.html";
		public static final String APPLICATIONS_LIST = "secure/application-list.html";
		public static final String PROJECT = "secure/project.html";
		public static final String PROJECT_LIST = "secure/project-list.html";
		public static final String SERVICE_CATALOG = "secure/service-catalog.html";
		public static final String REVIEW_ORDER = "secure/review-order.html";
		public static final String QUERY_EDIT = "secure/query-edit.html";
		public static final String SALESFORCE_OUTBOUND_MESSAGE = "secure/salesforce-outbound-messages.html";
		public static final String ENVIRONMENT = "secure/environment.html";
		public static final String ENVIRONMENTS = "secure/environments.html";
		public static final String SALESFORCE_CONNECTOR = "secure/salesforce-connector.html";
		public static final String SALESFORCE_CONNECTOR_EDIT = "secure/salesforce-connector-edit.html";
		public static final String SALESFORCE_CONNECTORS_LIST = "secure/salesforce-connectors-list.html";
		public static final String EVENT_LISTENERS = "secure/event-listeners.html";
		public static final String TARGETS = "secure/targets.html";
		public static final String SALESFORCE_OAUTH = "secure/salesforce-callback.html";
		public static final String ADMINISTRATION_HOME = "secure/administration-home.html";
		public static final String CACHE_MANAGER = "secure/cache.html";
		public static final String PROPERTY_MANAGER = "secure/properties-list.html";
		public static final String ACCOUNT_PROFILE = "secure/account-profile.html";
		public static final String ACCOUNT_PROFILE_EDIT = "secure/account-profile-edit.html";
		public static final String ACCOUNT_PROFILE_ADDRESS_EDIT = "secure/account-profile-address-edit.html";
		public static final String ACCOUNT_PROFILE_PAYMENT_METHOD = "secure/payment-method.html";
		public static final String NOTIFICATIONS = "secure/notification-list.html";
	}
}