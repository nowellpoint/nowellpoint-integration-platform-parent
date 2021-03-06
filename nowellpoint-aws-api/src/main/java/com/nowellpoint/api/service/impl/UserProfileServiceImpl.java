package com.nowellpoint.api.service.impl;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.nowellpoint.api.rest.domain.Address;
import com.nowellpoint.api.rest.domain.AddressRequest;
import com.nowellpoint.api.rest.domain.Organization;
import com.nowellpoint.api.rest.domain.OrganizationInfo;
import com.nowellpoint.api.rest.domain.Photos;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.rest.domain.UserProfile;
import com.nowellpoint.api.rest.domain.UserProfileRequest;
import com.nowellpoint.api.service.EmailService;
import com.nowellpoint.api.service.IdentityProviderService;
import com.nowellpoint.api.service.UserProfileService;
import com.nowellpoint.api.util.ClaimsContext;
import com.nowellpoint.api.util.UserContext;
import com.okta.sdk.resource.user.User;

public class UserProfileServiceImpl extends AbstractUserProfileService implements UserProfileService {
	
	private static char[] chars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'q', 'w', 'e', 'r', 't', 'z', 'u', 'i', 'o', 'p', 'a', 's',
	        'd', 'f', 'g', 'h', 'j', 'k', 'l', 'y', 'x', 'c', 'v', 'b', 'n', 'm', 'Q', 'W', 'E', 'R', 'T', 'Z', 'U', 'I', 'O', 'P', 'A',
	        'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Y', 'X', 'C', 'V', 'B', 'N', 'M', '<', '=', '>', '?', '@' };
	
	@Inject
	private IdentityProviderService identityProviderService;
	
	@Inject
	private EmailService emailService;
	
	@Override
	public UserProfile findById(String id) {
		return super.findById(id);
	}
	
	@Override
	public UserProfile findByReferenceId(String referenceId) {
		return findOne( eq ( "referenceId", referenceId ) );
	}

	@Override
	public UserProfile findByUsername(String username) {
		return findOne( eq ( "username", username ) );
	}
	
	@Override
	public Set<UserProfile> queryByOrganizationId(String organizationId) {
		return query( eq ( "organization", organizationId ) );
	}

	@Override
	public UserProfile createUserProfile(String firstName, String lastName, String email, String phone, String countryCode, Organization organization) {
		return createUserProfile(firstName, lastName, email, phone, countryCode, organization, Locale.getDefault(), TimeZone.getDefault());
	}
	
	@Override
	public UserProfile createUserProfile(String firstName, String lastName, String email, String phone, String countryCode, Organization organization, Locale locale, TimeZone timeZone) {
		
		UserInfo userInfo = UserInfo.of(UserContext.getPrincipal().getName());
		
		String temporaryPassword = generateTemporaryPassword(24);
		
		User user = createUser(email, firstName, lastName, temporaryPassword);
		
		Date now = Date.from(Instant.now());
		
		Address address = Address.builder()
				.countryCode(countryCode)
				.addedOn(now)
				.updatedOn(now)
				.build();
		
		Photos photos = Photos.builder()
				.profilePicture("/images/person-generic.jpg")
				.build();
		
		OrganizationInfo organizationInfo = OrganizationInfo.of(organization.getId());
		
		UserProfile userProfile = UserProfile.builder()
				.firstName(firstName)
				.lastName(lastName)
				.email(email)
				.address(address)
				.locale(Locale.getDefault())
				.timeZone(TimeZone.getDefault())
				.isActive(Boolean.FALSE)
				.createdBy(userInfo)
				.createdOn(now)
				.lastUpdatedBy(userInfo)
				.lastUpdatedOn(now)
				.photos(photos)
				.referenceId(user.getId())
				.organization(organizationInfo)
				.build();
		
		create(userProfile);
		
		sendWelcomeMessage(userProfile.getEmail(), userProfile.getEmail(), userProfile.getName(), temporaryPassword);
		
		return userProfile;
	}
	
	@Override
	public UserProfile updateUserProfile(String id, UserProfileRequest request) {
		UserProfile original = findById(id);
		
		UserInfo userInfo = UserInfo.of(ClaimsContext.getClaims().getBody().getSubject());
		
		Date now = Date.from(Instant.now());
		
		identityProviderService.updateUser(
				original.getReferenceId(), 
				request.getEmail(), 
				request.getFirstName(), 
				request.getLastName());
		
		UserProfile userProfile = UserProfile.builder()
				.from(original)
				.email(request.getEmail())
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.lastUpdatedBy(userInfo)
				.lastUpdatedOn(now)
				.locale(new Locale(request.getLocale()))
				.phone(request.getPhone())
				.timeZone(TimeZone.getTimeZone(request.getTimeZone()))
				.title(request.getTitle())
				.build();
		
		update(userProfile);
		
		return userProfile;
	}
	
	@Override
	public UserProfile updateAddress(String id, AddressRequest request) {
		UserProfile original = findById(id);
		
		Address address = Address.builder()
				.from(original.getAddress())
				.city(request.getCity())
				.countryCode(request.getCountryCode())
				.postalCode(request.getPostalCode())
				.state(request.getState())
				.street(request.getStreet())
				.updatedOn(Date.from(Instant.now()))
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
		UserProfile userProfile = findById(id);
		
		UserProfile instance = UserProfile.builder()
				.from(userProfile)
				.isActive(Boolean.FALSE)
				.build();
		
		update(instance);
		
		identityProviderService.deactivateUser(userProfile.getReferenceId());
		
		return instance;
	}
	
	@Override
	public void setPassword(String id, String password) {
		UserProfile userProfile = findById(id);
		identityProviderService.setPassword(userProfile.getReferenceId(), password);
	}
	
	@Override
	public void changePassword(String id, String oldPassword, String newPassword) {
		UserProfile userProfile = findById(id);
		identityProviderService.changePassword(userProfile.getReferenceId(), oldPassword, newPassword);
	}
	
	@Override
	public void deleteUserProfile(String id) {
		UserProfile userProfile = findById(id);
		identityProviderService.deleteUser(userProfile.getReferenceId());
		super.delete(userProfile);
	}
	
	public void addSalesforceProfilePicture(String userId, String profileHref) {

		AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

		try {
			URL url = new URL(profileHref);

			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			String contentType = connection.getHeaderField("Content-Type");

			ObjectMetadata objectMetadata = new ObjectMetadata();
			objectMetadata.setContentLength(connection.getContentLength());
			objectMetadata.setContentType(contentType);

			PutObjectRequest putObjectRequest = new PutObjectRequest("aws-microservices", userId,
					connection.getInputStream(), objectMetadata);

			s3Client.putObject(putObjectRequest);

		} catch (IOException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
	}
	
	public void loggedInEvent(@Observes UserProfile userProfile) {		
		UserProfile instance = UserProfile.builder()
				.from(userProfile)
				.lastLoginDate(Date.from(Instant.now()))
				.build();
		
		this.update(instance);
	}
	
	private User createUser(String email, String firstName, String lastName, String temporaryPassword) {
		return identityProviderService.createUser(email, firstName, lastName, temporaryPassword);
	}
	
	private static String generateTemporaryPassword(int length) {
		
		StringBuilder stringBuilder = new StringBuilder();

	    for (int i = 0; i < length; i++) {
	        stringBuilder.append(chars[new Random().nextInt(chars.length)]);
	    }
	    return stringBuilder.toString();
	}
	
	private void sendWelcomeMessage(String email, String username, String name, String temporaryPassword) {
		emailService.sendWelcomeMessage(email, username, name, temporaryPassword);
	}
}