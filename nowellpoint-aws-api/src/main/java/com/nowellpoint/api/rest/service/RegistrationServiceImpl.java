package com.nowellpoint.api.rest.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.apache.commons.text.RandomStringGenerator;
import org.jboss.logging.Logger;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.nowellpoint.api.rest.domain.Registration;
import com.nowellpoint.api.rest.domain.ValidationException;
import com.nowellpoint.api.service.AccountProfileService;
import com.nowellpoint.api.service.RegistrationService;
import com.nowellpoint.api.util.MessageConstants;
import com.nowellpoint.api.util.MessageProvider;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

public class RegistrationServiceImpl implements RegistrationService {
	
	private static final Logger LOGGER = Logger.getLogger(RegistrationServiceImpl.class);
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@Inject
	protected DocumentManagerFactory documentManagerFactory;

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
			publish(email);
			LOGGER.info(String.format("registration received for email address: %s", email));
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
				.createdOn(Date.from(Instant.now()))
				.lastUpdatedOn(Date.from(Instant.now()))
				.build();
		
		MongoDocument document = registration.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( document );
		
		registration.fromDocument( document );
		
		return registration;
	}
	
	private void publish(String email) {
		AmazonSNS snsClient = AmazonSNSClient.builder().build(); 
		PublishRequest publishRequest = new PublishRequest("arn:aws:sns:us-east-1:600862814314:REGISTRATION", String.format("Registration received for email address: %s", email));
		snsClient.publish(publishRequest);
	}
}