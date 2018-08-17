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

package com.nowellpoint.www.app.util;

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
		public static final String RESEND = "/resend/";
		public static final String ACTIVATE_ACCOUNT = "/activate/:id/";
		public static final String SECURE_ACCOUNT = "/secure/:id/";
		public static final String CONTACT = "/contact/";
		public static final String HEALTH_CHECK = "/healthcheck/";
		
		public static final String LOGOUT = String.format(APPLICATION_CONTEXT, "logout/"); 
		
		public static final String SALESFORCE_OAUTH = String.format(APPLICATION_CONTEXT, "salesforce-oauth");
		public static final String START = String.format(APPLICATION_CONTEXT, "start/");
		public static final String DASHBOARD = String.format(APPLICATION_CONTEXT, "dashboard/");
		
		public static final String USER_PROFILE = String.format(APPLICATION_CONTEXT, "user-profile/:id/");
		
		public static final String ORGANIZATION_VIEW = String.format(APPLICATION_CONTEXT, "organization/:id");
		public static final String ORGANIZATION_LIST_PLANS = String.format(APPLICATION_CONTEXT, "organization/:id/plans");
		public static final String ORGANIZATION_PLAN = String.format(APPLICATION_CONTEXT, "organization/:id/plans/:planId");
		public static final String ORGANIZATION_CREDIT_CARD = String.format(APPLICATION_CONTEXT, "organization/:id/credit-card/");
		public static final String ORGANIZATION_BILLING_CONTACT = String.format(APPLICATION_CONTEXT, "organization/:id/billing-contact/");
		public static final String ORGANIZATION_BILLING_ADDRESS = String.format(APPLICATION_CONTEXT, "organization/:id/billing-address/");
		public static final String ORGANIZATION_GET_INVOICE = String.format(APPLICATION_CONTEXT, "organization/:id/invoice/:invoiceNumber/");
		
		public static final String NOTIFICATIONS = String.format(APPLICATION_CONTEXT, "notifications");
		public static final String CONNECTORS_SHOW = String.format(APPLICATION_CONTEXT, "connectors/show");
		public static final String CONNECTORS_ADD = String.format(APPLICATION_CONTEXT, "connectors/add");
		public static final String CONNECTORS_EDIT = String.format(APPLICATION_CONTEXT, "connectors/:id/edit");
		public static final String CONNECTORS_CONNECT = String.format(APPLICATION_CONTEXT, "connectors/:id/connect");
		public static final String CONNECTORS_LIST = String.format(APPLICATION_CONTEXT, "connectors");
		public static final String CONNECTORS_VIEW = String.format(APPLICATION_CONTEXT, "connectors/:id/view");
		public static final String CONNECTORS_DISCONNECT = String.format(APPLICATION_CONTEXT, "connectors/:id/disconnect");
		public static final String CONNECTORS_UPDATE = String.format(APPLICATION_CONTEXT, "connectors/:id/update");
		public static final String CONNECTORS_DELETE = String.format(APPLICATION_CONTEXT, "connectors/:id/delete");
		public static final String CONNECTORS_REFRESH = String.format(APPLICATION_CONTEXT, "connectors/:id/refresh");
		

		
		public static final String JOBS_LIST = String.format(APPLICATION_CONTEXT, "jobs");
		public static final String JOBS_VIEW = String.format(APPLICATION_CONTEXT, "jobs/:id");
		public static final String JOBS_UPDATE = String.format(APPLICATION_CONTEXT, "jobs/:id");
		public static final String JOBS_OUTPUTS = String.format(APPLICATION_CONTEXT, "jobs/:id/outputs");
		public static final String JOBS_OUTPUTS_DOWNLOAD = String.format(APPLICATION_CONTEXT, "jobs/:id/download");
		public static final String JOBS_WEBHOOK_URL_TEST = String.format(APPLICATION_CONTEXT, "jobs/:id/webhook-test");
		public static final String JOBS_EDIT = String.format(APPLICATION_CONTEXT, "jobs/:id/edit");
		public static final String JOBS_SUBMIT = String.format(APPLICATION_CONTEXT, "jobs/:id/submit");
		public static final String JOBS_RUN = String.format(APPLICATION_CONTEXT, "jobs/:id/run");
		public static final String JOBS_STOP = String.format(APPLICATION_CONTEXT, "jobs/:id/stop");
		public static final String JOBS_TERMINATE = String.format(APPLICATION_CONTEXT, "jobs/:id/terminate");
		public static final String JOBS_CREATE = String.format(APPLICATION_CONTEXT, "jobs/:connectorId/metadata-backup");
		public static final String ADMINISTRATION = String.format(APPLICATION_CONTEXT, "administration");
	}
}