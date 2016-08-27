package com.nowellpoint.www.app.util;

public class Path {
	
	/**
	 *  
	 * @author jherson
	 *
	 */
	
	public static final String APPLICATION_CONTEXT = "/app/%s";
	
	public static class Route {
		public static final String INDEX = "/";
		public static final String LOGIN = "/login";
		public static final String LOGOUT = "/logout"; 
		public static final String SIGN_UP = "/signup";
		public static final String CONTACT_US = "/contact";
		public static final String SERVICES = "/services";
		public static final String HEALTH_CHECK = "/healthcheck";
		public static final String VERIFY_EMAIL = "/verify-email";
		public static final String START = String.format(APPLICATION_CONTEXT, "start");
		public static final String DASHBOARD = String.format(APPLICATION_CONTEXT, "dashboard");
		public static final String NOTIFICATIONS = String.format(APPLICATION_CONTEXT, "notifications");
		public static final String SETUP = String.format(APPLICATION_CONTEXT, "setup");
		public static final String APPLICATIONS = String.format(APPLICATION_CONTEXT, "applications");
		public static final String APPLICATION = String.format(APPLICATION_CONTEXT, "applications/:id");
		public static final String APPLICATIONS_NEW = String.format(APPLICATION_CONTEXT, "applications/new");
		public static final String APPLICATIONS_IMPORT = String.format(APPLICATION_CONTEXT, "applications/import");
		public static final String APPLICATIONS_CREATE = String.format(APPLICATION_CONTEXT, "applications/create");
		public static final String PROJECTS = String.format(APPLICATION_CONTEXT, "projects");
		public static final String PROVIDERS = String.format(APPLICATION_CONTEXT, "providers");
		public static final String CONNECTORS_SALESFORCE_LIST = String.format(APPLICATION_CONTEXT, "connectors/salesforce");
		public static final String CONNECTORS_SALESFORCE = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id");
		public static final String CONNECTORS_SALESFORCE_EDIT = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/edit");
		public static final String CONNECTORS_SALESFORCE_ENVIRONMENTS_ADD = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/environments/add");
		public static final String SALESFORCE_OAUTH = String.format(APPLICATION_CONTEXT, "salesforce/oauth");
		public static final String ACCOUNT_PROFILE = String.format(APPLICATION_CONTEXT, "account-profile/:id");
		public static final String ACCOUNT_PROFILE_ADDRESS = String.format(APPLICATION_CONTEXT, "account-profile/:id/address");
		public static final String ACCOUNT_PROFILE_PAYMENT_METHODS = String.format(APPLICATION_CONTEXT, "account-profile/:id/payment-methods");
		public static final String ADMINISTRATION = String.format(APPLICATION_CONTEXT, "administration");
		
		
	}

	public static class Template {
		public static final String INDEX = "index.html";
		public static final String LOGIN = "login.html";
		public static final String SIGN_UP = "signup.html";
		public static final String CONTACT_US = "contact.html";
		public static final String SERVICES = "services.html";
		public static final String VERIFY_EMAIL = "verify-email.html";
		public static final String START = String.format(APPLICATION_CONTEXT, "start.html");
		public static final String DASHBOARD = String.format(APPLICATION_CONTEXT, "dashboard.html");
		public static final String SETUP = String.format(APPLICATION_CONTEXT, "setup.html");
		public static final String APPLICATION = String.format(APPLICATION_CONTEXT, "application.html");
		public static final String APPLICATION_CONNECTOR_SELECT = String.format(APPLICATION_CONTEXT, "application-connector-select.html");
		public static final String APPLICATION_EDIT = String.format(APPLICATION_CONTEXT, "application-edit.html");
		public static final String APPLICATION_CREATE = String.format(APPLICATION_CONTEXT, "application.html");
		public static final String APPLICATIONS_LIST = String.format(APPLICATION_CONTEXT, "applications-list.html");
		public static final String PROJECT = String.format(APPLICATION_CONTEXT, "project.html");
		public static final String PROJECT_LIST = String.format(APPLICATION_CONTEXT, "project-list.html");
		public static final String SERVICE_CATALOG = String.format(APPLICATION_CONTEXT, "service-catalog.html");
		public static final String REVIEW_SERVICE_PLANS = String.format(APPLICATION_CONTEXT, "review-service-plans.html");
		public static final String QUERY_EDIT = String.format(APPLICATION_CONTEXT, "query-edit.html");
		public static final String SALESFORCE_OUTBOUND_MESSAGE = String.format(APPLICATION_CONTEXT, "salesforce-outbound-messages.html");
		public static final String ENVIRONMENT = String.format(APPLICATION_CONTEXT, "environment.html");
		public static final String ENVIRONMENTS = String.format(APPLICATION_CONTEXT, "environments.html");
		public static final String SALESFORCE_CONNECTOR = String.format(APPLICATION_CONTEXT, "salesforce-connector.html");
		public static final String SALESFORCE_CONNECTOR_EDIT = String.format(APPLICATION_CONTEXT, "salesforce-connector-edit.html");
		public static final String SALESFORCE_CONNECTORS_LIST = String.format(APPLICATION_CONTEXT, "salesforce-connectors-list.html");
		public static final String EVENT_LISTENERS = String.format(APPLICATION_CONTEXT, "event-listeners.html");
		public static final String TARGETS = String.format(APPLICATION_CONTEXT, "targets.html");
		public static final String SALESFORCE_OAUTH = String.format(APPLICATION_CONTEXT, "salesforce-callback.html");
		public static final String ADMINISTRATION_HOME = String.format(APPLICATION_CONTEXT, "administration-home.html");
		public static final String CACHE_MANAGER = String.format(APPLICATION_CONTEXT, "cache.html");
		public static final String PROPERTY_MANAGER = String.format(APPLICATION_CONTEXT, "properties-list.html");
		public static final String ACCOUNT_PROFILE = String.format(APPLICATION_CONTEXT, "account-profile.html");
		public static final String ACCOUNT_PROFILE_EDIT = String.format(APPLICATION_CONTEXT, "account-profile-edit.html");
		public static final String ACCOUNT_PROFILE_ADDRESS_EDIT = String.format(APPLICATION_CONTEXT, "account-profile-address-edit.html");
		public static final String ACCOUNT_PROFILE_PAYMENT_METHOD = String.format(APPLICATION_CONTEXT, "payment-method.html");
		public static final String NOTIFICATIONS = String.format(APPLICATION_CONTEXT, "notification-list.html");
	}
}