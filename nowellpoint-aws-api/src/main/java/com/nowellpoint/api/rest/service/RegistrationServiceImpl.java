package com.nowellpoint.api.rest.service;

import java.net.URI;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.text.RandomStringGenerator;
import org.jboss.logging.Logger;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.nowellpoint.api.rest.domain.Registration;
import com.nowellpoint.api.rest.domain.ValidationException;
import com.nowellpoint.api.service.AccountProfileService;
import com.nowellpoint.api.service.EmailService;
import com.nowellpoint.api.service.RegistrationService;
import com.nowellpoint.api.util.MessageConstants;
import com.nowellpoint.api.util.MessageProvider;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.nowellpoint.util.Assert;
import com.nowellpoint.util.Properties;

public class RegistrationServiceImpl extends AbstractRegistrationService implements RegistrationService {
	
	private static final Logger LOGGER = Logger.getLogger(RegistrationServiceImpl.class);
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@Inject
	private EmailService emailService;

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
			LOGGER.info(String.format(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_RECEIVED), email));
		}
		
		String emailVerificationToken = new RandomStringGenerator.Builder()
				.withinRange('a', 'z')
				.usingRandom(new SecureRandom()::nextInt)
				.build()
				.generate(24);
		
		URI emailVerificationHref = buildEmailVerificationHref(emailVerificationToken);
		
		LOGGER.info(emailVerificationHref);
		
		Registration registration = Registration.builder()
				.countryCode(countryCode)
				.email(email)
				.emailVerificationToken(emailVerificationToken)
				.firstName(firstName)
				.lastName(lastName)
				.emailVerificationHref(emailVerificationHref)
				.createdOn(Date.from(Instant.now()))
				.lastUpdatedOn(Date.from(Instant.now()))
				.build();
		
		create(registration);
		
		sendEmail(
				email, 
				firstName, 
				lastName, 
				emailVerificationHref);
		
		return registration;
	}
	
	private URI buildEmailVerificationHref(String emailVerificationToken) {
		return UriBuilder.fromUri(System.getProperty(Properties.VERIFY_EMAIL_REDIRECT))
				.queryParam("emailVerificationToken", "{emailVerificationToken}")
				.build(emailVerificationToken);
	}
	
	private void publish(String email) {
		AmazonSNS snsClient = AmazonSNSClient.builder().build(); 
		PublishRequest publishRequest = new PublishRequest("arn:aws:sns:us-east-1:600862814314:REGISTRATION", MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_RECEIVED), email);
		snsClient.publish(publishRequest);
	}
	
	private void sendEmail(String email, String firstName, String lastName, URI emailVerificationHref) {		
		String name = Assert.isNotNullOrEmpty(firstName) ? firstName.concat(" ").concat(lastName) : lastName; 
		emailService.sendEmailVerificationMessage(email, name, emailVerificationHref);
	}
}