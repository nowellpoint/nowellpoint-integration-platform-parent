package com.nowellpoint.api.service;

import com.nowellpoint.api.rest.domain.Registration;

public interface RegistrationService {
	
	public Registration findById(String id);
	
	public Registration register(
			String firstName,
    		String lastName,
    		String email,
    		String phone,
    		String countryCode,
    		String domain,
    		String planId);
	
	public Registration updateRegistration(
			String id, 
			String domain, 
			String planId);
	
	public Registration verifyEmail(String emailVerificationToken);
	
	public void resentVerificationEmail(String id);
	
	public Registration provision(
			String id, 
			String cardholderName, 
			String expirationMonth, 
			String expirationYear,
			String number, 
			String cvv);
	
	public void deleteRegistration(String id);
}