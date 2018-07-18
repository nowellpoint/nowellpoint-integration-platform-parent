package com.nowellpoint.console.service;

public class ServiceClient {
	
	private static final ServiceClient INSTANCE = new ServiceClient();
	private AuthenticationService authentication;
	private OrganizationService organization;
	private PlanService plan;
	private UserProfileService userProfile;
	
	private ServiceClient() {
		
	}

	public static ServiceClient getInstance() {
		return INSTANCE;
	}
	
	public AuthenticationService authentication() {
		if (authentication == null) {
			authentication = new AuthenticationService();
		}
		return authentication;
	}
	
	public OrganizationService organization() {
		if (organization == null) {
			organization = new OrganizationService();
		}
		return organization;
	}
	
	public PlanService plan() {
		if (plan == null) {
			plan = new PlanService();
		}
		return plan;
	}
	
	public UserProfileService userProfile() {
		if (userProfile == null) {
			userProfile = new UserProfileService();
		}
		return userProfile;
	}
}