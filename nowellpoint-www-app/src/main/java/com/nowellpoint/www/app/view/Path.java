package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;

import spark.template.freemarker.FreeMarkerEngine;

public class Path {
	
	/**
	 *  
	 * @author jherson
	 *
	 */
	
	public static class Routes {
		public static final String LOGIN = "/login";
		public static final String LOGOUT = "/logout"; 
		public static final String ACCOUNT_PROFILE = "/app/account-profile/:id";
		public static final String ACCOUNT_PROFILE_ME = "/app/account-profile";
		public static final String ACCOUNT_PROFILE_EDIT = "/app/account-profile/:id/edit";
		public static final String ACCOUNT_PROFILE_DISABLE = "/app/account-profile/:id/disable";
		public static final String ACCOUNT_PROFILE_ADDRESS = "/app/account-profile/:id/address";
	}

	public static class Templates {
		
	}
}