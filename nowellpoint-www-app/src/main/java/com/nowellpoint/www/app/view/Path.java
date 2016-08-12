package com.nowellpoint.www.app.view;

public class Path {
	
	/**
	 *  
	 * @author jherson
	 *
	 */
	
	public static class Route {
		public static final String LOGIN = "/login";
		public static final String LOGOUT = "/logout"; 
		public static final String ACCOUNT_PROFILE = "/app/account-profile/:id";
		public static final String ACCOUNT_PROFILE_ADDRESS = "/app/account-profile/:id/address";
		public static final String ACCOUNT_PROFILE_PAYMENT_METHODS = "/app/account-profile/:id/payment-methods";
		public static final String ADMINISTRATION = "/app/administration";
	}

	public static class Template {
		public static final String LOGIN = "login.html";
		public static final String ACCOUNT_PROFILE = "secure/account-profile.html";
		public static final String ACCOUNT_PROFILE_EDIT = "secure/account-profile-edit.html";
		public static final String ACCOUNT_PROFILE_ADDRESS_EDIT = "secure/account-profile-address-edit.html";
	}
}