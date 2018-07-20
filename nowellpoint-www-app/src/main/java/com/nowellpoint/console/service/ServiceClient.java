package com.nowellpoint.console.service;

import com.nowellpoint.console.service.impl.AuthenticationServiceImpl;
import com.nowellpoint.console.service.impl.IdentityServiceImpl;
import com.nowellpoint.console.service.impl.LeadServiceImpl;
import com.nowellpoint.console.service.impl.OrganizationServiceImpl;
import com.nowellpoint.console.service.impl.PlanServiceImpl;
import com.nowellpoint.console.service.impl.UserProfileServiceImpl;

public class ServiceClient {
	
	private static final ServiceClient INSTANCE = new ServiceClient();
	
	private AuthenticationService authentication;
	private IdentityService identity;
	private LeadService lead;
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
			authentication = new AuthenticationServiceImpl();
		}
		return authentication;
	}
	
	public IdentityService identity() {
		if (identity == null) {
			identity = new IdentityServiceImpl();
		}
		return identity;
	}
	
	public LeadService lead() {
		if (lead == null) {
			lead = new LeadServiceImpl();
		} 
		return lead;
	}
	
	public OrganizationService organization() {
		if (organization == null) {
			organization = new OrganizationServiceImpl();
		}
		return organization;
	}
	
	public PlanService plan() {
		if (plan == null) {
			plan = new PlanServiceImpl();
		}
		return plan;
	}
	
	public UserProfileService userProfile() {
		if (userProfile == null) {
			userProfile = new UserProfileServiceImpl();
		}
		return userProfile;
	}
}