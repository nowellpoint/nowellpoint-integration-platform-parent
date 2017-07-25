package com.nowellpoint.api.service;

import com.nowellpoint.api.rest.domain.Registration;

public interface RegistrationService {
	public Registration register(
			String firstName,
    		String lastName,
    		String email,
    		String countryCode,
    		String domain,
    		String planId);
	
	public Registration updateRegistration(String id, String domain);
	
	public Registration verifyEmail(String emailVerificationToken);
}