package com.nowellpoint.api.rest.impl;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.nowellpoint.api.rest.SignUpService;
import com.nowellpoint.api.rest.domain.Registration;
import com.nowellpoint.api.service.RegistrationService;

public class SignUpServiceImpl implements SignUpService {
	
	@Inject
	private RegistrationService registrationService;
	
	@Override
	public Response getRegistration(String id) {
		
		Registration registration = registrationService.findById(id);
		
		return Response.ok(registration)
				.build();
	}
    
	@Override
    public Response createRegistration(
    		String firstName,
    		String lastName,
    		String email,
    		String phone,
    		String countryCode,
    		String domain,
    		String planId) {
    	
		Registration registration = registrationService.register(
				firstName, 
				lastName, 
				email, 
				phone,
				countryCode, 
				domain,
				planId);
		
		URI uri = UriBuilder.fromPath(registration.getMeta().getHref()).build();
		
		return Response.created(uri)
				.entity(registration)
				.build();
    }
	
	@Override
	public Response resendVerificationEmail(String id) {
		registrationService.resentVerificationEmail(id);
		return Response.noContent()
				.build();
	}
	
	@Override
	public Response updateRegistration(String id, String domain, String planId) {
		
		Registration registration = registrationService.updateRegistration(
				id, 
				domain,
				planId);
		
		return Response.ok(registration)
				.build();
	}
	
	@Override
	public Response provision(
			String id, 
			String cardholderName, 
			String expirationMonth, 
			String expirationYear,
			String number, 
			String cvv) {
		
		Registration registration = registrationService.provision(
				id, 
				cardholderName, 
				expirationMonth, 
				expirationYear, 
				number, 
				cvv);
		
		return Response.ok(registration)
				.build();
	}
    
	@Override
    public Response setPassword(String password, String confirmPassword) {
    	return Response.ok().build();
    }
	
	@Override
	public Response verifyEmail(String emailVerificationToken) {	
		Registration registration = registrationService.verifyEmail(emailVerificationToken);
		return Response.ok(registration)
				.build();
	}

	@Override
	public Response deleteRegistration(String id) {
		registrationService.deleteRegistration(id);
		return Response.ok().build();
	}
}