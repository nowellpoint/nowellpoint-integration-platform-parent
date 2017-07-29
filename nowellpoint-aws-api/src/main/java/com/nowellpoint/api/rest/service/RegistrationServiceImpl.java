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
import com.nowellpoint.api.rest.IdentityResource;
import com.nowellpoint.api.rest.SignUpService;
import com.nowellpoint.api.rest.domain.Organization;
import com.nowellpoint.api.rest.domain.Registration;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.rest.domain.UserProfile;
import com.nowellpoint.api.rest.domain.ValidationException;
import com.nowellpoint.api.service.EmailService;
import com.nowellpoint.api.service.OrganizationService;
import com.nowellpoint.api.service.RegistrationService;
import com.nowellpoint.api.service.UserProfileService;
import com.nowellpoint.api.util.MessageConstants;
import com.nowellpoint.api.util.MessageProvider;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.nowellpoint.util.Assert;
import com.nowellpoint.util.Properties;

public class RegistrationServiceImpl extends AbstractRegistrationService implements RegistrationService {
	
	private static final Logger LOGGER = Logger.getLogger(RegistrationServiceImpl.class);
	
	@Inject
	private EmailService emailService;
	
	@Inject
	private UserProfileService userProfileService;
	
	@Inject
	private OrganizationService organizationService;

	@Override
	public Registration register(String firstName, String lastName, String email, String countryCode, String domain, String planId) {
		
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
			
			userProfileService.findByUsername(email);
			
			throw new ValidationException(String.format(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_ACCOUNT_CONFLICT), email));
			
		} catch (DocumentNotFoundException ignore) {
			publish(email);
		}
		
		UserInfo userInfo = UserInfo.of(UserContext.getPrincipal().getName());
		
		String emailVerificationToken = new RandomStringGenerator.Builder()
				.withinRange('0', 'z')
				.filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
				.usingRandom(new SecureRandom()::nextInt)
				.build()
				.generate(24);
		
		Registration registration = Registration.builder()
				.countryCode(countryCode)
				.email(email)
				.emailVerificationToken(emailVerificationToken)
				.firstName(firstName)
				.lastName(lastName)
				.createdBy(userInfo)
				.createdOn(Date.from(Instant.now()))
				.lastUpdatedBy(userInfo)
				.lastUpdatedOn(Date.from(Instant.now()))
				.domain(Assert.isNotNullOrEmpty(domain) ? domain : emailVerificationToken)
				.expiresAt(Instant.now().plusSeconds(1209600).toEpochMilli())
				.build();
		
		create(registration);
		
		if (Assert.isNotNullOrEmpty(domain)) {
			sendVerificationEmail(
					registration.getEmail(), 
					registration.getName(), 
					registration.getEmailVerificationToken());
		}
		
		return registration;
	}
	
	@Override
	public Registration updateRegistration(String id, String domain) {
		
		if (Assert.isNullOrEmpty(domain)) {
			throw new ValidationException(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_MISSING_DOMAIN));
		}
		
		Registration original = findById(id);
		
		isExpired(original.getExpiresAt());
		
		URI emailVerificationTokenUri = UriBuilder.fromUri(System.getProperty(Properties.API_HOSTNAME))
				.path(SignUpService.class)
				.path("verify-email")
				.path("{emailVerificationToken}")
				.build(original.getEmailVerificationToken());
		
		LOGGER.info(emailVerificationTokenUri);
		
		UserInfo userInfo = UserInfo.of(UserContext.getPrincipal().getName());
		
		Registration registration = Registration.builder()
				.from(original)
				.domain(domain)
				.emailVerificationHref(emailVerificationTokenUri)
				.lastUpdatedOn(Date.from(Instant.now()))
				.lastUpdatedBy(userInfo)
				.build();
		
		update(registration);
		
		sendVerificationEmail(
				registration.getEmail(), 
				registration.getName(), 
				registration.getEmailVerificationToken());
		
		return registration;
	}
	
	@Override
	public Registration verifyEmail(String emailVerificationToken) {
		Registration original = findByEmailVerificationToken(emailVerificationToken);
		
		isExpired(original.getExpiresAt());
		
		UserProfile userProfile = createUserProfile(
				original.getFirstName(), 
				original.getLastName(), 
				original.getEmail(), 
				original.getCountryCode());
		
		Organization organization = createOrganization(original.getDomain());
		
		URI uri = UriBuilder.fromUri(System.getProperty(Properties.API_HOSTNAME))
				.path(IdentityResource.class)
				.path("/{organizationId}/{userId}")
				.build(organization.getId(), userProfile.getId());
		
		Registration registration = Registration.builder()
				.from(original)
				.expiresAt(System.currentTimeMillis())
				.identityHref(uri.toString())
				.build();
		
		update(registration);
		
		return registration;
	}
	
	private Registration findByEmailVerificationToken(String emailVerificationToken) {
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
	
	private void sendVerificationEmail(String email, String name, String emailVerificationToken) {		
		emailService.sendEmailVerificationMessage(email, name, emailVerificationToken);
	}
	
	private void isExpired(Long expiresAt) {
		if (Instant.ofEpochMilli(expiresAt).isBefore(Instant.now())) {
			throw new ValidationException(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_INVALID_OR_EXPIRED));
		}
	}
	
	private UserProfile createUserProfile(String firstName, String lastName, String email, String countryCode) {
		return userProfileService.createUserProfile(firstName, lastName, email, countryCode);
	}
	
	private Organization createOrganization(String domain) {
		return organizationService.createOrganization(domain);
	}
}