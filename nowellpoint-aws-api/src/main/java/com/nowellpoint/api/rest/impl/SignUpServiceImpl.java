package com.nowellpoint.api.rest.impl;

import java.net.URI;
import java.util.Locale;

import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.jboss.logging.Logger;

import com.nowellpoint.api.rest.SignUpService;
import com.nowellpoint.api.rest.domain.Registration;
import com.nowellpoint.api.service.RegistrationService;
import com.nowellpoint.api.util.MessageConstants;
import com.nowellpoint.api.util.MessageProvider;

public class SignUpServiceImpl implements SignUpService {
	
	private static final Logger LOGGER = Logger.getLogger(SignUpServiceImpl.class);
	
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
			String cardNumber, 
			String cvv) {
		
		try {
			
			Registration registration = registrationService.provision(
					id, 
					cardholderName,
					expirationMonth, 
					expirationYear, 
					cardNumber, 
					cvv);
			
			return Response.ok(registration)
					.build();
		
		} catch (Exception e) {
			
			LOGGER.error(e);
			
			throw new InternalServerErrorException(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.UNEXPECTED_EXCEPTION));
		}
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