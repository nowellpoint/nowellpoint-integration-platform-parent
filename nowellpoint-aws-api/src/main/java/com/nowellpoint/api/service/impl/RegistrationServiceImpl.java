package com.nowellpoint.api.service.impl;

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
import com.nowellpoint.api.rest.domain.PlanOrig;
import com.nowellpoint.api.rest.domain.Registration;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.rest.domain.UserProfile;
import com.nowellpoint.api.rest.domain.ValidationException;
import com.nowellpoint.api.service.EmailService;
import com.nowellpoint.api.service.OrganizationService;
import com.nowellpoint.api.service.PlanService;
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
	private PlanService planService;
	
	@Inject
	private EmailService emailService;
	
	@Inject
	private UserProfileService userProfileService;
	
	@Inject
	private OrganizationService organizationService;
	
	@Override
	public Registration findById(String id) {
		return super.findById(id);
	}

	@Override
	public Registration register(String firstName, String lastName, String email, String phone, String countryCode, String domain, String planId) {
		
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
    	
    	PlanOrig planOrig = findPlanById(planId);
    	
    	isRegistred(email, domain);
		
		UserInfo userInfo = UserInfo.of(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
		String emailVerificationToken = new RandomStringGenerator.Builder()
				.withinRange('0', 'z')
				.filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
				.usingRandom(new SecureRandom()::nextInt)
				.build()
				.generate(24);
		
		Registration registration = Registration.builder()
				.countryCode(countryCode)
				.email(email)
				.phone(phone)
				.emailVerificationToken(emailVerificationToken)
				.firstName(firstName)
				.lastName(lastName)
				.verified(Boolean.FALSE)
				.planId(planOrig.getId())
				.createdBy(userInfo)
				.createdOn(now)
				.lastUpdatedBy(userInfo)
				.lastUpdatedOn(now)
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
	public Registration updateRegistration(String id, String domain, String planId) {
		
		if (Assert.isNullOrEmpty(domain)) {
			throw new ValidationException(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_MISSING_DOMAIN));
		}
		
		Registration registration = findById(id);
		
		isExpired(registration.getExpiresAt());
		
		URI emailVerificationTokenUri = UriBuilder.fromUri(System.getProperty(Properties.API_HOSTNAME))
				.path(SignUpService.class)
				.path("verify-email")
				.path("{emailVerificationToken}")
				.build(registration.getEmailVerificationToken());
		
		LOGGER.info(emailVerificationTokenUri);
		
		UserInfo userInfo = UserInfo.of(UserContext.getPrincipal().getName()); 
		
		Date now = Date.from(Instant.now());
		
		PlanOrig planOrig = findPlanById(Assert.isNotNullOrEmpty(planId) ? planId : registration.getPlanId());
		
		Registration instance = Registration.builder()
				.from(registration)
				.domain(domain)
				.emailVerificationHref(emailVerificationTokenUri)
				.lastUpdatedOn(now)
				.lastUpdatedBy(userInfo)
				.planId(planOrig.getId())
				.build();
		
		update(instance);
		
		sendVerificationEmail(
				instance.getEmail(), 
				instance.getName(), 
				instance.getEmailVerificationToken());
		
		return instance;
	}
	
	@Override
	public Registration verifyEmail(String emailVerificationToken) {
		Registration registration = findByEmailVerificationToken(emailVerificationToken);
		
		Registration instance = Registration.builder()
				.from(registration)
				.expiresAt(System.currentTimeMillis())
				.verified(Boolean.TRUE)
				.build();
		
		update(instance);
		
		return instance;
	}
	
	@Override
	public void resentVerificationEmail(String id) {
		Registration registration = findById(id);
		
		sendVerificationEmail(
				registration.getEmail(), 
				registration.getName(), 
				registration.getEmailVerificationToken());
	}
	
	@Override
	public Registration provision(
			String id, 
			String cardholderName, 
			String expirationMonth, 
			String expirationYear,
			String number, 
			String cvv) {
		
		Registration registration = findById(id);
		
		PlanOrig planOrig = findPlanById(registration.getPlanId());
		
		Organization organization = null;
		
		if (planOrig.getPrice().getUnitPrice() > 0) {
			organization = createOrganization(
					planOrig,
					registration.getDomain(), 
					registration.getFirstName(),
					registration.getLastName(),
					registration.getEmail(),
					registration.getPhone(),
					registration.getCountryCode(),
					cardholderName, 
					expirationMonth, 
					expirationYear, 
					number, 
					cvv);
		} else {
			organization = createOrganization(
					planOrig,
					registration.getDomain(), 
					registration.getFirstName(),
					registration.getLastName(),
					registration.getEmail(),
					registration.getPhone(),
					registration.getCountryCode());
		}
		
		UserProfile userProfile = createUserProfile(
				registration.getFirstName(), 
				registration.getLastName(), 
				registration.getEmail(), 
				registration.getPhone(),
				registration.getCountryCode(),
				organization);
		
		URI uri = UriBuilder.fromUri(System.getProperty(Properties.API_HOSTNAME))
				.path(IdentityResource.class)
				.path("/{organizationId}/{userId}")
				.build(organization.getId(), userProfile.getId());
		
		Registration instance = Registration.builder()
				.from(registration)
				.identityHref(uri.toString())
				.build();
		
		update(instance);
		
		return instance;
		
	}
	
	@Override
	public void deleteRegistration(String id) {
		Registration registration = findById(id);
		super.delete(registration);
	}
	
	private Registration findByEmailVerificationToken(String emailVerificationToken) {
		Registration registration = null;
		try {
			registration = findOne( Filters.eq ( "emailVerificationToken", emailVerificationToken ) );
			isExpired(registration.getExpiresAt());
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
	
	private UserProfile createUserProfile(String firstName, String lastName, String email, String phone, String countryCode, Organization organization) {
		return userProfileService.createUserProfile(firstName, lastName, email, phone, countryCode, organization);
	}
	
	private Organization createOrganization(PlanOrig planOrig, String domain, String firstName, String lastName, String email, String phone, String countryCode) {
		return organizationService.createOrganization(
				planOrig, 
				domain, 
				firstName, 
				lastName, 
				email, 
				phone, 
				countryCode);
	}
	
	private Organization createOrganization(PlanOrig planOrig, String domain, String firstName, String lastName, String email, String phone, String countryCode, String cardholderName, String expirationMonth, String expirationYear, String number, String cvv) {
		return organizationService.createOrganization(
				planOrig, 
				domain, 
				firstName, 
				lastName, 
				email, 
				phone, 
				countryCode, 
				cardholderName, 
				expirationMonth, 
				expirationYear, 
				number, 
				cvv);
	}
	
	private PlanOrig findPlanById(String planId) {
		try {
    		return planService.findById(planId);
    	} catch (DocumentNotFoundException ignore) {
    		throw new ValidationException(String.format(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_INVALID_PLAN), planId));
		}
	}
	
	private void isRegistred(String username, String domain) {
		try {
			userProfileService.findByUsername(username);
			throw new ValidationException(String.format(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_ACCOUNT_CONFLICT), username));
		} catch (DocumentNotFoundException e) {
			try {
				organizationService.findByDomain(domain);
				throw new ValidationException(String.format(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_DOMAIN_CONFLICT), domain));
			} catch (DocumentNotFoundException ignore) {
				publish(username);
			}
		}
	}
}