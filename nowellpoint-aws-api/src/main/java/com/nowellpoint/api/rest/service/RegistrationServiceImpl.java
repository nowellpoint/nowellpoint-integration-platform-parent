package com.nowellpoint.api.rest.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.apache.commons.text.RandomStringGenerator;
import org.jboss.logging.Logger;

import com.nowellpoint.api.rest.domain.Registration;
import com.nowellpoint.api.rest.domain.ValidationException;
import com.nowellpoint.api.service.AccountProfileService;
import com.nowellpoint.api.service.RegistrationService;
import com.nowellpoint.api.util.MessageConstants;
import com.nowellpoint.api.util.MessageProvider;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.nowellpoint.util.Assert;

public class RegistrationServiceImpl implements RegistrationService {
	
	private static final Logger LOGGER = Logger.getLogger(RegistrationServiceImpl.class);
	
	@Inject
	private AccountProfileService accountProfileService;

	@Override
	public Registration register(String firstName, String lastName, String email, String countryCode, String planId) {
		List<String> errors = new ArrayList<>();
    	
    	if (Assert.isNullOrEmpty(lastName)) {
    		errors.add(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_MISSING_LAST_NAME));
    	}
    	
    	if (Assert.isNullOrEmpty(email)) {
    		errors.add(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_MISSING_EMAIL));
    	}
    	
    	if (Assert.isNullOrEmpty(countryCode)) {
    		errors.add(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_MISSING_COUNTRY_CODE));
    	}
    	
    	if (Assert.isNullOrEmpty(planId)) {
    		errors.add(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_MISSING_PLAN_ID));
    	}
    	
    	if (! errors.isEmpty()) {
    		throw new ValidationException(errors);
    	}
		
		try {
			
			accountProfileService.findByUsername(email);
			
			throw new ValidationException(String.format(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_ACCOUNT_CONFLICT), email));
			
		} catch (DocumentNotFoundException ignore) {
			LOGGER.info(String.format("registering received for email address: %s", email));
		}
		
		String emailVerificationToken = new RandomStringGenerator.Builder()
				.withinRange('a', 'z')
				.usingRandom(new SecureRandom()::nextInt)
				.build()
				.generate(24);
		
		Registration registration = Registration.builder()
				.countryCode(countryCode)
				.email(email)
				.emailVerificationToken(emailVerificationToken)
				.firstName(firstName)
				.lastName(lastName)
				.build();
		
		return registration;
	}
}