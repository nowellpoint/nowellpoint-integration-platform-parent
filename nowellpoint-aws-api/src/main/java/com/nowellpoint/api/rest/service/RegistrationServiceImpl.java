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

import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.jboss.logging.Logger;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.mongodb.client.model.Filters;
import com.nowellpoint.api.rest.SignUpService;
import com.nowellpoint.api.rest.domain.Registration;
import com.nowellpoint.api.rest.domain.ValidationException;
import com.nowellpoint.api.service.AccountProfileService;
import com.nowellpoint.api.service.EmailService;
import com.nowellpoint.api.service.RegistrationService;
import com.nowellpoint.api.util.MessageConstants;
import com.nowellpoint.api.util.MessageProvider;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.nowellpoint.util.Assert;

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
				.withinRange('0', 'z')
				.filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
				.usingRandom(new SecureRandom()::nextInt)
				.build()
				.generate(24);
		
		URI emailVerificationTokenUri = UriBuilder.fromUri(System.getProperty("api.hostname"))
				.path(SignUpService.class)
				.path("verify-email")
				.path("{emailVerificationToken}")
				.build(emailVerificationToken);
		
		LOGGER.info(emailVerificationTokenUri);
		
		Registration registration = Registration.builder()
				.countryCode(countryCode)
				.email(email)
				.emailVerificationToken(emailVerificationToken)
				.firstName(firstName)
				.lastName(lastName)
				.emailVerificationHref(emailVerificationTokenUri)
				.createdOn(Date.from(Instant.now()))
				.lastUpdatedOn(Date.from(Instant.now()))
				.build();
		
		create(registration);
		
		sendEmail(registration.getEmail(), registration.getName(), registration.getEmailVerificationToken());
		
		return registration;
	}
	
	public Registration findByEmailVerificationToken(String emailVerificationToken) {
		Registration registration = null;
		try {
			registration = findOne( Filters.eq ( "emailVerificationToken", emailVerificationToken ) );
		} catch (DocumentNotFoundException e) {
			throw new ValidationException(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_INVALID_OR_EXPIRED));
		}
		return registration;
	}
	
	private void publish(String email) {
		if (Boolean.valueOf(System.getProperty("registration.send.notification"))) {
			AmazonSNS snsClient = AmazonSNSClient.builder().build(); 
			PublishRequest publishRequest = new PublishRequest(
					"arn:aws:sns:us-east-1:600862814314:REGISTRATION", 
					MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_RECEIVED), email);
			
			snsClient.publish(publishRequest);
		}
	}
	
	private void sendEmail(String email, String name, String emailVerificationToken) {		
		emailService.sendEmailVerificationMessage(email, name, emailVerificationToken);
	}
}