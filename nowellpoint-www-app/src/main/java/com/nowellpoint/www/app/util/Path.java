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
		public static final String APPLICATION_LIST = String.format(APPLICATION_CONTEXT, "applications");
		public static final String APPLICATION_VIEW = String.format(APPLICATION_CONTEXT, "applications/:id");
		public static final String APPLICATION_CONNECTOR_SELECT = String.format(APPLICATION_CONTEXT, "applications/select");
		public static final String APPLICATION_EDIT = String.format(APPLICATION_CONTEXT, "applications/:id/edit");
		public static final String APPLICATION_DELETE = String.format(APPLICATION_CONTEXT, "applications/:id/delete");
		public static final String APPLICATION_NEW = String.format(APPLICATION_CONTEXT, "applications/new");
		public static final String APPLICATION_CREATE = String.format(APPLICATION_CONTEXT, "applications");
		public static final String APPLICATION_UPDATE = String.format(APPLICATION_CONTEXT, "applications/:id");
		public static final String APPLICATION_ENVIRONMENT_VIEW = String.format(APPLICATION_CONTEXT, "applications/:id/environments/:key/view");
		public static final String APPLICATION_ENVIRONMENT_NEW = String.format(APPLICATION_CONTEXT, "applications/:id/environments/new");
		public static final String APPLICATION_ENVIRONMENT_ADD = String.format(APPLICATION_CONTEXT, "applications/:id/environments");
		public static final String APPLICATION_ENVIRONMENT_EDIT = String.format(APPLICATION_CONTEXT, "applications/:id/environments/:key/edit");
		public static final String APPLICATION_ENVIRONMENT_UPDATE = String.format(APPLICATION_CONTEXT, "applications/:id/environments/:key");
		public static final String APPLICATION_ENVIRONMENT_TEST = String.format(APPLICATION_CONTEXT, "applications/:id/environments/:key/test");
		public static final String APPLICATION_ENVIRONMENT_REMOVE = String.format(APPLICATION_CONTEXT, "applications/:id/environments/:key");
		public static final String APPLICATION_SERVICE_VIEW = String.format(APPLICATION_CONTEXT, "applications/:id/services/:key/view");
		public static final String APPLICATION_SERVICE_EDIT = String.format(APPLICATION_CONTEXT, "applications/:id/services/:key/edit");
		public static final String APPLICATION_SERVICE_NEW = String.format(APPLICATION_CONTEXT, "applications/:id/services/catalog");
		public static final String APPLICATION_SERVICE_ADD = String.format(APPLICATION_CONTEXT, "applications/:id/services");
		public static final String APPLICATION_SERVICE_UPDATE = String.format(APPLICATION_CONTEXT, "applications/:id/services/:key");
		public static final String PROJECTS = String.format(APPLICATION_CONTEXT, "projects");
		public static final String PROVIDERS = String.format(APPLICATION_CONTEXT, "providers");
		public static final String CONNECTORS_SALESFORCE_LIST = String.format(APPLICATION_CONTEXT, "connectors/salesforce");
		public static final String CONNECTORS_SALESFORCE_VIEW = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id");
		public static final String CONNECTORS_SALESFORCE_EDIT = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/edit");
		public static final String CONNECTORS_SALESFORCE_UPDATE = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id");
		public static final String CONNECTORS_SALESFORCE_DELETE = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/delete");
		public static final String CONNECTORS_SALESFORCE_ENVIRONMENT_NEW = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/environments/new");
		public static final String CONNECTORS_SALESFORCE_ENVIRONMENT_VIEW = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/environments/:key/view");
		public static final String CONNECTORS_SALESFORCE_ENVIRONMENT_ADD = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/environments");
		public static final String CONNECTORS_SALESFORCE_ENVIRONMENT_EDIT = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/environments/:key/edit");
		public static final String CONNECTORS_SALESFORCE_ENVIRONMENT_UPDATE = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/environments/:key");
		public static final String CONNECTORS_SALESFORCE_ENVIRONMENT_TEST = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/environments/:key/test");
		public static final String CONNECTORS_SALESFORCE_ENVIRONMENT_REMOVE = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/environments/:key");
		public static final String CONNECTORS_SALESFORCE_SERVICE_VIEW = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/services/:key/view");
		public static final String CONNECTORS_SALESFORCE_SERVICE_EDIT = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/services/:key/edit");
		public static final String CONNECTORS_SALESFORCE_SERVICE_NEW = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/services/catalog");
		public static final String CONNECTORS_SALESFORCE_SERVICE_ADD = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/services");
		public static final String CONNECTORS_SALESFORCE_SERVICE_UPDATE = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/services/:key");
		public static final String SALESFORCE_OAUTH = String.format(APPLICATION_CONTEXT, "salesforce/oauth");
		public static final String ACCOUNT_PROFILE = String.format(APPLICATION_CONTEXT, "account-profile/:id");
		public static final String ACCOUNT_PROFILE_ADDRESS = String.format(APPLICATION_CONTEXT, "account-profile/:id/address");
		public static final String ACCOUNT_PROFILE_PAYMENT_METHODS = String.format(APPLICATION_CONTEXT, "account-profile/:id/payment-methods");
		public static final String ACCOUNT_PROFILE_DEACTIVATE = String.format(APPLICATION_CONTEXT, "account-profile/:id/deactivate");
		public static final String ADMINISTRATION = String.format(APPLICATION_CONTEXT, "administration");
		public static final String SCHEDULED_JOBS_LIST = String.format(APPLICATION_CONTEXT, "scheduled-jobs");
		public static final String SCHEDULED_JOB_SELECT_TYPE = String.format(APPLICATION_CONTEXT, "scheduled-jobs/new/select-type");
		public static final String SCHEDULED_JOB_SELECT_CONNECTOR = String.format(APPLICATION_CONTEXT, "scheduled-jobs/new/select-connector");
		public static final String SCHEDULED_JOB_SELECT_ENVIRONMENT = String.format(APPLICATION_CONTEXT, "scheduled-jobs/new/select-environment");
		public static final String SCHEDULED_JOB_SET_SCHEDULE = String.format(APPLICATION_CONTEXT, "scheduled-jobs/new/set-schedule");
		public static final String SCHEDULED_JOB_VIEW = String.format(APPLICATION_CONTEXT, "scheduled-jobs/:id");
		public static final String SCHEDULED_JOB_CREATE = String.format(APPLICATION_CONTEXT, "scheduled-jobs");
		public static final String SCHEDULED_JOB_EDIT = String.format(APPLICATION_CONTEXT, "scheduled-jobs/:id/edit");
		public static final String SCHEDULED_JOB_UPDATE = String.format(APPLICATION_CONTEXT, "scheduled-jobs/:id");
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
		public static final String TARGETS = String.format(APPLICATION_CONTEXT, "targets.html");
		public static final String SALESFORCE_OAUTH = String.format(APPLICATION_CONTEXT, "salesforce-callback.html");
		public static final String ADMINISTRATION_HOME = String.format(APPLICATION_CONTEXT, "administration-home.html");
		public static final String CACHE_MANAGER = String.format(APPLICATION_CONTEXT, "cache.html");
		public static final String PROPERTY_MANAGER = String.format(APPLICATION_CONTEXT, "properties-list.html");
		public static final String ACCOUNT_PROFILE = String.format(APPLICATION_CONTEXT, "account-profile.html");
		public static final String ACCOUNT_PROFILE_EDIT = String.format(APPLICATION_CONTEXT, "account-profile-edit.html");
		public static final String ACCOUNT_PROFILE_ADDRESS_EDIT = String.format(APPLICATION_CONTEXT, "account-profile-address-edit.html");
		public static final String ACCOUNT_PROFILE_DEACTIVATE = String.format(APPLICATION_CONTEXT, "account-profile-deactivate.html");
		public static final String ACCOUNT_PROFILE_PAYMENT_METHOD = String.format(APPLICATION_CONTEXT, "payment-method.html");
		public static final String NOTIFICATIONS = String.format(APPLICATION_CONTEXT, "notification-list.html");
		public static final String SCHEDULED_JOBS_LIST = String.format(APPLICATION_CONTEXT, "scheduled-jobs-list.html");
		public static final String SCHEDULED_JOB_SELECT = String.format(APPLICATION_CONTEXT, "scheduled-job-create.html");
		public static final String SCHEDULED_JOB_EDIT = String.format(APPLICATION_CONTEXT, "scheduled-job-edit.html");
		public static final String SCHEDULED_JOB = String.format(APPLICATION_CONTEXT, "scheduled-job.html");
		public static final String SCHEDULE = String.format(APPLICATION_CONTEXT, "schedule.html");
	}
}