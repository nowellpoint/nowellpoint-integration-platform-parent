package com.nowellpoint.client.resource;

import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.util.IOUtils;
import com.nowellpoint.client.model.Address;
import com.nowellpoint.client.model.AddressRequest;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Error;
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
	
	private static final String RESOURCE_CONTEXT = "user-profile";
	
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
	 * @param id
	 * @param invoiceNumber
	 * @return
	 * @throws IOException 
	 */
	
	public byte[] downloadInvoice(String id, String invoiceNumber) throws IOException {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_OCTET_STREAM)
				.path(RESOURCE_CONTEXT)
				.path(id)
				.path("invoice")
				.path(invoiceNumber)
				.execute();
		
		InputStream resource = null;
    	
    	if (httpResponse.getStatusCode() == Status.OK) {
    		resource = httpResponse.getEntity();
    	} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
    	}
    	
    	return IOUtils.toByteArray(resource);
	} 
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	public DeleteResult deactivate(String id) {
		HttpResponse httpResponse = RestResource.delete(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.path(id)
				.execute();
		
		DeleteResult result = null;
			
		if (httpResponse.getStatusCode() == Status.OK) {
			result = new DeleteResultImpl();
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new DeleteResultImpl(error);
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param accountProfileId
	 * @param userProfileRequest
	 * @return
	 */
	
	public UpdateResult<UserProfile> update(String accountProfileId, UserProfileRequest userProfileRequest) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path(RESOURCE_CONTEXT)
				.path(accountProfileId)
				.parameter("firstName", userProfileRequest.getFirstName())
				.parameter("lastName", userProfileRequest.getLastName())
				.parameter("company", userProfileRequest.getCompany())
				.parameter("division", userProfileRequest.getDivision())
				.parameter("department", userProfileRequest.getDepartment())
				.parameter("title", userProfileRequest.getTitle())
				.parameter("email", userProfileRequest.getEmail())
				.parameter("mobilePhone", userProfileRequest.getMobilePhone())
				.parameter("phone", userProfileRequest.getPhone())
				.parameter("extension", userProfileRequest.getExtension())
				.parameter("locale", userProfileRequest.getLocale())
				.parameter("timeZone", userProfileRequest.getTimeZone())
				.execute();
		
		UpdateResult<UserProfile> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			UserProfile resource = httpResponse.getEntity(UserProfile.class);
			result = new UpdateResultImpl<UserProfile>(resource);
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new UpdateResultImpl<UserProfile>(error);
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param accountProfileId
	 * @return
	 */
	
	public UpdateResult<UserProfile> removeProfilePicture(String userProfileId) {
		HttpResponse httpResponse = RestResource.delete(token.getEnvironmentUrl())
    			.bearerAuthorization(token.getAccessToken())
        		.path(RESOURCE_CONTEXT)
        		.path(userProfileId)
        		.path("photo")
        		.execute();
		
		UpdateResult<UserProfile> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			UserProfile resource = httpResponse.getEntity(UserProfile.class);
			result = new UpdateResultImpl<UserProfile>(resource);
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new UpdateResultImpl<UserProfile>(error);
		}
		
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
					.parameter("state", addressRequest.getStateCode())
					.parameter("postalCode", addressRequest.getPostalCode())
					.parameter("street", addressRequest.getStreet())
					.execute();
			
			UpdateResult<Address> result = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				Address resource = httpResponse.getEntity(Address.class);
				result = new UpdateResultImpl<Address>(resource);
			} else {
				Error error = httpResponse.getEntity(Error.class);
				result = new UpdateResultImpl<Address>(error);
			}
			
			return result;
		}
	}
}