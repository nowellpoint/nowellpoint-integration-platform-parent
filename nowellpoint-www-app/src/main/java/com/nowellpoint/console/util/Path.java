/**
 * 
 * Copyright 2015-2016 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.nowellpoint.console.util;

public class Path {
	
	/**
	 *  
	 * @author jherson
	 *
	 */
	
	public static final String APPLICATION_CONTEXT = "/app/%s/";
	
	public static class Route {
		public static final String INDEX = "/";
		public static final String LOGIN = "/login/";
		public static final String FREE_ACCOUNT = "/free/";
		public static final String SIGN_UP = ("/signup/");
		public static final String ACCOUNT_ACTIVATE = "/signup/:id/activate/";
		public static final String ACCOUNT_SECURE = "/signup/:id/secure/";
		public static final String ACCOUNT_CONNECT_USER = "/signup/:id/connected-user/";
		public static final String ACCOUNT_CONNECT_USER_ADD = "/signup/:id/connected-user/add/";
		public static final String ACCOUNT_ACTIVATION_RESEND = "/signup/:id/resend/";
		public static final String SALESFORCE_OAUTH_CALLBACK = "/salesforce/oauth/callback/";
		public static final String CONTACT = "/contact/";
		public static final String HEALTH_CHECK = "/healthcheck/";
		
		public static final String LOGOUT = String.format(APPLICATION_CONTEXT, "logout"); 
		public static final String START = String.format(APPLICATION_CONTEXT, "start");
		public static final String DASHBOARD = String.format(APPLICATION_CONTEXT, "dashboard");
		public static final String ADMINISTRATION = String.format(APPLICATION_CONTEXT, "administration");
		public static final String USER_PROFILE = String.format(APPLICATION_CONTEXT, "user-profile");
		public static final String ORGANIZATION = String.format(APPLICATION_CONTEXT, "organization");
		public static final String ORGANIZATION_CONNECTED_USER = String.format(APPLICATION_CONTEXT, "organization/connected-user");
		public static final String ORGANIZATION_EVENTS = String.format(APPLICATION_CONTEXT, "organization/events");
		public static final String ORGANIZATION_EVENT_LISTENER_SETUP = String.format(APPLICATION_CONTEXT, "organization/event-listeners/setup/:sobject");
		
		public static final String ORGANIZATION_LIST_PLANS = String.format(APPLICATION_CONTEXT, "organization/:id/plans");
		public static final String ORGANIZATION_PLAN = String.format(APPLICATION_CONTEXT, "organization/:id/plans/:planId");
		public static final String ORGANIZATION_CREDIT_CARD = String.format(APPLICATION_CONTEXT, "organization/:id/credit-card");
		public static final String ORGANIZATION_BILLING_CONTACT = String.format(APPLICATION_CONTEXT, "organization/:id/billing-contact");
		public static final String ORGANIZATION_BILLING_ADDRESS = String.format(APPLICATION_CONTEXT, "organization/:id/billing-address");
		public static final String ORGANIZATION_GET_INVOICE = String.format(APPLICATION_CONTEXT, "organization/:id/invoice/:invoiceNumber");
	}
	
	public static class Resource {
		public static final String IDENTITIES = "/resource/identities/:id/";
		public static final String ORANIZATIONS = "/resource/organizations/:id/";
	}
}