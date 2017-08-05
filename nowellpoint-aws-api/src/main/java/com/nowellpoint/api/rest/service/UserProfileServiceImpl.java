package com.nowellpoint.api.rest.service;

import static com.mongodb.client.model.Filters.eq;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

import org.apache.commons.text.RandomStringGenerator;

import com.nowellpoint.api.rest.domain.Address;
import com.nowellpoint.api.rest.domain.Photos;
import com.nowellpoint.api.rest.domain.ReferenceLink;
import com.nowellpoint.api.rest.domain.ReferenceLinkTypes;
import com.nowellpoint.api.rest.domain.AbstractUserInfo;
import com.nowellpoint.api.rest.domain.UserProfile;
import com.nowellpoint.api.service.EmailService;
import com.nowellpoint.api.service.IdentityProviderService;
import com.nowellpoint.api.service.UserProfileService;
import com.nowellpoint.api.util.CountryProvider;
import com.nowellpoint.api.util.UserContext;
import com.okta.sdk.resource.user.User;

public class UserProfileServiceImpl extends AbstractUserProfileService implements UserProfileService {
	
	@Inject
	private IdentityProviderService identityProviderService;
	
	@Inject
	private EmailService emailService;
	
	@Override
	public UserProfile findById(String id) {
		return super.findById(id);
	}

	@Override
	public UserProfile findByUsername(String username) {
		return findOne(eq ( "username", username ));
	}

	@Override
	public UserProfile createUserProfile(String firstName, String lastName, String email, String countryCode) {
		return createUserProfile(firstName, lastName, email, countryCode, Locale.getDefault(), TimeZone.getDefault());
	}
	
	@Override
	public UserProfile createUserProfile(String firstName, String lastName, String email, String countryCode, Locale locale, TimeZone timeZone) {
		String country = getCountry(countryCode);
		
		AbstractUserInfo abstractUserInfo = AbstractUserInfo.of(UserContext.getPrincipal().getName());
		
		String temporaryPassword = generateTemporaryPassword(24);
		
		User user = createUser(email, firstName, lastName, temporaryPassword);
		
		ReferenceLink referenceLink = ReferenceLink.of(ReferenceLinkTypes.USER_ID, user.getId());
		
		Date now = Date.from(Instant.now());
		
		Address address = Address.builder()
				.countryCode(countryCode)
				.country(country)
				.build();
		
		Photos photos = Photos.builder()
				.profilePicture("/images/person-generic.jpg")
				.build();
		
		UserProfile userProfile = UserProfile.builder()
				.firstName(firstName)
				.lastName(lastName)
				.email(email)
				.username(email)
				.address(address)
				.locale(Locale.getDefault())
				.timeZone(TimeZone.getDefault())
				.isActive(Boolean.FALSE)
				.createdBy(abstractUserInfo)
				.createdOn(now)
				.lastUpdatedBy(abstractUserInfo)
				.lastUpdatedOn(now)
				.photos(photos)
				.addReferenceLink(referenceLink)
				.build();
		
		create(userProfile);
		
		sendWelcomeMessage(userProfile.getEmail(), userProfile.getEmail(), userProfile.getName(), temporaryPassword);
		
		return userProfile;
	}
	
	@Override
	public UserProfile updateAddress(String id, String street, String city, String state, String postalCode, String countryCode) {
		UserProfile original = findById(id);
		
		String country = getCountry(countryCode);
		
		Address address = Address.builder()
				.from(original.getAddress())
				.city(city)
				.countryCode(countryCode)
				.country(country)
				.stateCode(state)
				.street(street)
				.build();
		
		UserProfile userProfile = UserProfile.builder()
				.from(original)
				.address(address)
				.build();
		
		update(userProfile);
		
		return userProfile;
	}
	
	@Override
	public UserProfile deactivateUserProfile(String id) {
		UserProfile original = findById(id);
		
		UserProfile userProfile = UserProfile.builder()
				.from(original)
				.isActive(Boolean.FALSE)
				.build();
		
		update(userProfile);
		
		return userProfile;
	}
	
	private User createUser(String email, String firstName, String lastName, String temporaryPassword) {
		return identityProviderService.createUser(email, firstName, lastName, temporaryPassword);
	}
	
	private String getCountry(String countryCode) {
		return CountryProvider.getCountry(Locale.getDefault(), countryCode);
	}
	
	private static String generateTemporaryPassword(int length) {
		return new RandomStringGenerator.Builder()
				.withinRange('0', 'z')
				.usingRandom(new SecureRandom()::nextInt)
				.build()
				.generate(length);
	}
	
	private void sendWelcomeMessage(String email, String username, String name, String temporaryPassword) {
		emailService.sendWelcomeMessage(email, username, name, temporaryPassword);
	}
}