package com.nowellpoint.signup.service;

import com.nowellpoint.api.model.RegistrationRequest;
import com.nowellpoint.signup.model.Registration;

public interface RegistrationService {
	
	public Registration findById(String id);
	
	public Registration register(RegistrationRequest request);
	
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