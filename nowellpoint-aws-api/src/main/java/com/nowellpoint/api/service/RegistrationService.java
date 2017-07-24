package com.nowellpoint.api.service;

import com.nowellpoint.api.rest.domain.Registration;

public interface RegistrationService {
	public Registration register(
			String firstName,
    		String lastName,
    		String email,
    		String countryCode,
    		String planId);
	
	public Registration addSite(String id, String siteName);
	
	public Registration verifyEmail(String emailVerificationToken);
}