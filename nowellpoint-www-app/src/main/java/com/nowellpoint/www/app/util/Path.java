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
		public static final String FREE_ACCOUNT = "/free";
		public static final String PLANS = "/plans";
		public static final String CONTACT = "/contact";
		public static final String HEALTH_CHECK = "/healthcheck";
		public static final String VERIFY_EMAIL = "/verify-email";
		public static final String START = String.format(APPLICATION_CONTEXT, "start");
		public static final String DASHBOARD = String.format(APPLICATION_CONTEXT, "dashboard");
		public static final String NOTIFICATIONS = String.format(APPLICATION_CONTEXT, "notifications");
		public static final String CONNECTORS_SALESFORCE_NEW = String.format(APPLICATION_CONTEXT, "connectors/salesforce/new");
		public static final String CONNECTORS_SALESFORCE_LIST = String.format(APPLICATION_CONTEXT, "connectors/salesforce");
		public static final String CONNECTORS_SALESFORCE_VIEW = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id");
		public static final String CONNECTORS_SALESFORCE_EDIT = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/edit");
		public static final String CONNECTORS_SALESFORCE_UPDATE = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id");
		public static final String CONNECTORS_SALESFORCE_DELETE = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/delete");
		public static final String CONNECTORS_SALESFORCE_TEST = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/test");
		public static final String CONNECTORS_SALESFORCE_BUILD = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/build");
		public static final String CONNECTORS_SALESFORCE_SOBJECT_LIST = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/sobjects");
		public static final String CONNECTORS_SALESFORCE_SOBJECT_VIEW = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/sobjects/:sobjectName");
		public static final String CONNECTORS_SALESFORCE_SERVICE_LIST = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/services");
		public static final String CONNECTORS_SALESFORCE_SERVICE_ADD = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/services/:serviceId");
		public static final String CONNECTORS_SALESFORCE_SERVICE_SETUP = String.format(APPLICATION_CONTEXT, "connectors/salesforce/:id/services/:serviceId/setup");
		public static final String SALESFORCE_OAUTH = String.format(APPLICATION_CONTEXT, "salesforce/oauth");
		public static final String ACCOUNT_PROFILE = String.format(APPLICATION_CONTEXT, "account-profile/:id");
		public static final String ACCOUNT_PROFILE_PICTURE = String.format(APPLICATION_CONTEXT, "account-profile/:id/picture");
		public static final String ACCOUNT_PROFILE_LIST_PLANS = String.format(APPLICATION_CONTEXT, "account-profile/:id/plans");
		public static final String ACCOUNT_PROFILE_PLAN = String.format(APPLICATION_CONTEXT, "account-profile/:id/plans/:planId");
		public static final String ACCOUNT_PROFILE_CURRENT_PLAN = String.format(APPLICATION_CONTEXT, "account-profile/:id/current-plan");
		public static final String ACCOUNT_PROFILE_ADDRESS = String.format(APPLICATION_CONTEXT, "account-profile/:id/address");
		public static final String ACCOUNT_PROFILE_PAYMENT_METHODS = String.format(APPLICATION_CONTEXT, "account-profile/:id/payment-methods");
		public static final String ACCOUNT_PROFILE_DEACTIVATE = String.format(APPLICATION_CONTEXT, "account-profile/:id/deactivate");
		public static final String JOBS_LIST = String.format(APPLICATION_CONTEXT, "jobs");
		public static final String JOBS_VIEW = String.format(APPLICATION_CONTEXT, "jobs/:id");
		public static final String JOBS_OUTPUTS = String.format(APPLICATION_CONTEXT, "jobs/:id/outputs");
		public static final String JOBS_OUTPUTS_DOWNLOAD = String.format(APPLICATION_CONTEXT, "jobs/:id/download");
		public static final String JOBS_EDIT = String.format(APPLICATION_CONTEXT, "jobs/:id/edit");
		public static final String JOBS_RUN = String.format(APPLICATION_CONTEXT, "jobs/:id/run");
		public static final String ADMINISTRATION = String.format(APPLICATION_CONTEXT, "administration");
	}
}