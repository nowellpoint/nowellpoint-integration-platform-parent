package com.nowellpoint.client.resource;

import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.util.IOUtils;
import com.nowellpoint.client.model.Organization;
import com.nowellpoint.client.model.UserProfileRequest;
import com.nowellpoint.client.model.CreditCardRequest;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.AddressRequest;
import com.nowellpoint.client.model.ContactRequest;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.Subscription;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.exception.NotFoundException;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;
import com.nowellpoint.client.model.SubscriptionRequest;
import com.nowellpoint.client.model.Token;

/**
 * @author jherson
 *
 */
public class OrganizationResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "organizations";
	
	/**
	 * 
	 * @param environmentUrl
	 * @param accessToken
	 */
	
	public OrganizationResource(Token token) {
		super(token);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	public Organization get(String id) {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken()).accept(MediaType.APPLICATION_JSON).path(RESOURCE_CONTEXT)
				.path(id).execute();

		Organization resource = null;

		if (httpResponse.getStatusCode() == Status.OK) {
			resource = httpResponse.getEntity(Organization.class);
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
	 * @param accountProfileId
	 * @param userProfileRequest
	 * @return
	 */
	
	public UpdateResult<Organization> update(String organizationId, UserProfileRequest userProfileRequest) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path(RESOURCE_CONTEXT)
				.path(organizationId)
				.execute();
		
		UpdateResult<Organization> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			Organization resource = httpResponse.getEntity(Organization.class);
			result = new UpdateResultImpl<Organization>(resource);
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new UpdateResultImpl<Organization>(error);
		}
		
		return result;
	}
	
	public DeleteResult delete(String id) {
		HttpResponse httpResponse = RestResource.delete(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
    			.path(id)
    			.execute();
		
		DeleteResult deleteResult = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			deleteResult = new DeleteResultImpl();
		} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
			Error error = httpResponse.getEntity(Error.class);
			deleteResult = new DeleteResultImpl(error);
		}
		
		return deleteResult;
	}
	
	/**
	 * 
	 * @param accountProfileId
	 * @return
	 */
	
	public UpdateResult<Organization> removeProfilePicture(String userProfileId) {
		HttpResponse httpResponse = RestResource.delete(token.getEnvironmentUrl())
    			.bearerAuthorization(token.getAccessToken())
        		.path(RESOURCE_CONTEXT)
        		.path(userProfileId)
        		.path("photo")
        		.execute();
		
		UpdateResult<Organization> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			Organization resource = httpResponse.getEntity(Organization.class);
			result = new UpdateResultImpl<Organization>(resource);
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new UpdateResultImpl<Organization>(error);
		}
		
		return result;
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	
	public SubscriptionResource subscription() {
		return new SubscriptionResource(token);
	}
	
	/**
	 * 
	 * @author jherson
	 *
	 */
	
	public class SubscriptionResource extends AbstractResource {
		
		/**
		 * 
		 * @param token
		 */
		
		public SubscriptionResource(Token token) {
			super(token);
		}
		
		/**
		 * 
		 * @return
		 */
		
		public CreditCardResource creditCard() {
			return new CreditCardResource(token);
		}
		
		/**
		 * 
		 * @return
		 */
		
		public ContactResource billingContact() {
			return new ContactResource(token);
		}
		
		/**
		 * 
		 * @return
		 */
		
		public AddressResource billingAddress() {
			return new AddressResource(token);
		}
		
		/**
		 * 
		 * @param subscriptionRequest
		 * @return
		 */
		
		public UpdateResult<Subscription> set(SubscriptionRequest subscriptionRequest) {
			HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.path(RESOURCE_CONTEXT)
					.path(subscriptionRequest.getOrganizationId())
					.path("subscription")
					.parameter("planId", subscriptionRequest.getPlanId())
					.parameter("cardholderName", subscriptionRequest.getCardholderName())
					.parameter("number", subscriptionRequest.getNumber())
					.parameter("expirationMonth", subscriptionRequest.getExpirationMonth())
					.parameter("expirationYear", subscriptionRequest.getExpirationYear())
					.parameter("cvv", subscriptionRequest.getCvv())
					.execute();
			
			UpdateResult<Subscription> result = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				Subscription subscription = httpResponse.getEntity(Subscription.class);
				result = new UpdateResultImpl<Subscription>(subscription);
			} else {
				Error error = httpResponse.getEntity(Error.class);
				result = new UpdateResultImpl<Subscription>(error);
			}
			
			return result;
		}
	}
	
	
	
	public class ContactResource extends AbstractResource {

		public ContactResource(Token token) {
			super(token);
		}
		
		public UpdateResult<Organization> update(ContactRequest contactRequest) {
			HttpResponse httpResponse = RestResource.post(contactRequest.getToken().getEnvironmentUrl())
					.bearerAuthorization(contactRequest.getToken().getAccessToken())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.path(RESOURCE_CONTEXT)
					.path(contactRequest.getOrganizationId())
					.path("subscription")
					.path("billing-contact")
					.parameter("firstName", contactRequest.getFirstName())
					.parameter("lastName", contactRequest.getLastName())
					.parameter("email", contactRequest.getEmail())
					.parameter("phone", contactRequest.getPhone())
					.execute();
			
			UpdateResult<Organization> result = new UpdateResultImpl<Organization>(Organization.class, httpResponse);
			
			return result;
		}
		
	}
	
	public class AddressResource extends AbstractResource {

		public AddressResource(Token token) {
			super(token);
		}
		
		public UpdateResult<Organization> update(AddressRequest addressRequest) {
			HttpResponse httpResponse = RestResource.post(addressRequest.getToken().getEnvironmentUrl())
					.bearerAuthorization(addressRequest.getToken().getAccessToken())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.path(RESOURCE_CONTEXT)
					.path(addressRequest.getOrganizationId())
					.path("subscription")
					.path("billing-address")
					.parameter("street", addressRequest.getStreet())
					.parameter("city", addressRequest.getCity())
					.parameter("stateCode", addressRequest.getStateCode())
					.parameter("postalCode", addressRequest.getPostalCode())
					.parameter("countryCode", addressRequest.getCountryCode())
					.execute();
			
			UpdateResult<Organization> result = new UpdateResultImpl<Organization>(Organization.class, httpResponse);
			
			return result;
		}
		
	}
	
	/**
	 * 
	 * @author jherson
	 *
	 */
	
	public class CreditCardResource extends AbstractResource {
		
		/**
		 * 
		 * @param token
		 */
		
		public CreditCardResource(Token token) {
			super(token);
		}
		
		/**
		 * 
		 * @param creditCardRequest
		 * @return
		 */
		
		public UpdateResult<Organization> update(CreditCardRequest creditCardRequest) {
			HttpResponse httpResponse = RestResource.post(creditCardRequest.getToken().getEnvironmentUrl())
					.bearerAuthorization(creditCardRequest.getToken().getAccessToken())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.path(RESOURCE_CONTEXT)
					.path(creditCardRequest.getOrganizationId())
					.path("subscription")
					.path("credit-card")
					.parameter("cardholderName", creditCardRequest.getCardholderName())
					.parameter("expirationMonth", creditCardRequest.getExpirationMonth())
					.parameter("expirationYear", creditCardRequest.getExpirationYear())
					.parameter("number", creditCardRequest.getNumber())
					.parameter("cvv", creditCardRequest.getCvv())
					.execute();
			
			UpdateResult<Organization> result = new UpdateResultImpl<Organization>(Organization.class, httpResponse);
			
			return result;
		}
		
		/**
		 * 
		 * @param accountProfileId
		 * @param paymentMethodToken
		 * @return
		 */
		
		public DeleteResult delete(String accountProfileId, String paymentMethodToken) {
			HttpResponse httpResponse = RestResource.delete(token.getEnvironmentUrl())
					.bearerAuthorization(token.getAccessToken())
					.path("account-profile")
					.path(accountProfileId)
					.path("credit-card")
					.path(paymentMethodToken)
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
	}
}