package com.nowellpoint.api.rest.service;

import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.text.RandomStringGenerator;
import org.jboss.logging.Logger;

import com.nowellpoint.api.rest.domain.Address;
import com.nowellpoint.api.rest.domain.Photos;
import com.nowellpoint.api.rest.domain.ReferenceLink;
import com.nowellpoint.api.rest.domain.ReferenceLinkTypes;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectIdBuilder;
import com.amazonaws.util.IOUtils;
import com.nowellpoint.api.rest.domain.UserProfile;
import com.nowellpoint.api.service.EmailService;
import com.nowellpoint.api.service.IdentityProviderService;
import com.nowellpoint.api.service.UserProfileService;
import com.nowellpoint.api.util.CountryProvider;
import com.nowellpoint.api.util.UserContext;
import com.okta.sdk.resource.user.User;

public class UserProfileServiceImpl extends AbstractUserProfileService implements UserProfileService {
	
	private static final Logger LOGGER = Logger.getLogger(AbstractUserProfileService.class);
	
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
		
		UserInfo userInfo = UserInfo.of(UserContext.getPrincipal().getName());
		
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
				.createdBy(userInfo)
				.createdOn(now)
				.lastUpdatedBy(userInfo)
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
	
	public byte[] getInvoice(String id, String invoiceNumber) {
		if (UserContext.getPrincipal().getName().equals(id)) {
			S3ObjectIdBuilder builder = new S3ObjectIdBuilder();
			builder.setBucket("nowellpoint-invoices");
			builder.setKey(invoiceNumber);
			
			GetObjectRequest request = new GetObjectRequest(builder.build());
			AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
			
			S3Object object = s3client.getObject(request);
			InputStream inputStream = object.getObjectContent();
			
			try {
				byte[] bytes = IOUtils.toByteArray(inputStream);
				inputStream.close();
				return bytes;
			} catch (IOException e) {
				LOGGER.error(e);
			}
		}
		
		return null;
	}
	
	public void addSalesforceProfilePicture(String userId, String profileHref) {
		
		AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
		
		try {
			URL url = new URL( profileHref );
			
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			String contentType = connection.getHeaderField("Content-Type");
			
			ObjectMetadata objectMetadata = new ObjectMetadata();
	    	objectMetadata.setContentLength(connection.getContentLength());
	    	objectMetadata.setContentType(contentType);
			
	    	PutObjectRequest putObjectRequest = new PutObjectRequest("aws-microservices", userId, connection.getInputStream(), objectMetadata);
	    	
	    	s3Client.putObject(putObjectRequest);
			
		} catch (IOException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
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