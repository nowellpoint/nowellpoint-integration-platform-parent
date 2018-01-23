package com.nowellpoint.client.resource;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.client.model.Address;
import com.nowellpoint.client.model.AddressRequest;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.UserProfile;
import com.nowellpoint.client.model.UserProfileRequest;
import com.nowellpoint.client.model.exception.NotFoundException;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

/**
 * @author jherson
 *
 */
public class UserProfileResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "user-profiles";
	
	/**
	 * 
	 * @param environmentUrl
	 * @param accessToken
	 */
	
	public UserProfileResource(Token token) {
		super(token);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	public UserProfile get(String id) {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(id)
				.execute();
		
		UserProfile resource = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			resource = httpResponse.getEntity(UserProfile.class);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
		}
		
		return resource;
	} 
	
	/**
	 * 
	 * @param accountProfileId
	 * @param abstractUserProfileRequest
	 * @return
	 */
	
	public UpdateResult<UserProfile> update(String accountProfileId, UserProfileRequest userProfileRequest) {
		ObjectNode payload = objectMapper.createObjectNode()
				.put("firstName", userProfileRequest.getFirstName())
				.put("lastName", userProfileRequest.getLastName())
				.put("title", userProfileRequest.getTitle())
				.put("email", userProfileRequest.getEmail())
				.put("phone", userProfileRequest.getPhone())
				.put("locale", userProfileRequest.getLocale())
				.put("timeZone", userProfileRequest.getTimeZone());
		
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(accountProfileId)
				.body(payload)
				.execute();
		
		UpdateResult<UserProfile> result = new UpdateResultImpl<UserProfile>(UserProfile.class, httpResponse);
		
		return result;
	}
	
	/**
	 * 
	 * @return
	 */
	
	public AddressResource address() {
		return new AddressResource(token);
	}
	
	/**
	 * 
	 * @author jherson
	 *
	 */
	
	public class AddressResource extends AbstractResource {
		
		/**
		 * 
		 * @param environmentUrl
		 * @param accessToken
		 */
		
		public AddressResource(Token token) {
			super(token);
		}
		
		/**
		 * 
		 * @param accountProfileId
		 * @param addressRequest
		 * @return
		 */
		
		public UpdateResult<Address> update(String userProfileId, AddressRequest addressRequest) {
			HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
					.bearerAuthorization(token.getAccessToken())
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.acceptCharset("UTF-8")
					.path(RESOURCE_CONTEXT)
					.path(userProfileId)
					.path("address")
					.parameter("city", addressRequest.getCity())
					.parameter("countryCode", addressRequest.getCountryCode())
					.parameter("state", addressRequest.getState())
					.parameter("postalCode", addressRequest.getPostalCode())
					.parameter("street", addressRequest.getStreet())
					.execute();
			
			UpdateResult<Address> result = new UpdateResultImpl<Address>(Address.class, httpResponse);
			
			return result;
		}
	}
}