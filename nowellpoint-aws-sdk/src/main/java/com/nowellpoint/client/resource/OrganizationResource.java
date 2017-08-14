package com.nowellpoint.client.resource;

import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.util.IOUtils;
import com.nowellpoint.client.model.Organization;
import com.nowellpoint.client.model.UserProfileRequest;
import com.nowellpoint.client.model.CreditCardRequest;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Address;
import com.nowellpoint.client.model.AddressRequest;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.CreditCard;
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
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(id)
				.execute();
		
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
		 * @return
		 */
		
		public Address get(String userProfileId) {
			HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
					.bearerAuthorization(token.getAccessToken())
					.path(RESOURCE_CONTEXT)
					.path(userProfileId)
					.path("address")
					.execute();
			
			Address resource = null;
	    	
	    	if (httpResponse.getStatusCode() == Status.OK) {
	    		resource = httpResponse.getEntity(Address.class);
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
					.path(subscriptionRequest.getUserProfileId())
					.path("subscription")
					.parameter("planId", subscriptionRequest.getPlanId())
					.parameter("paymentMethodToken", subscriptionRequest.getPaymentMethodToken())
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
		
		public CreateResult<CreditCard> add(CreditCardRequest creditCardRequest) {
			HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.path("account-profile")
					.path(creditCardRequest.getOrganizationId())
					.path("credit-card")
					.parameter("cardholderName", creditCardRequest.getCardholderName())
					.parameter("cvv", creditCardRequest.getCvv())
					.parameter("number", creditCardRequest.getNumber())
					.parameter("expirationMonth", creditCardRequest.getExpirationMonth())
					.parameter("expirationYear", creditCardRequest.getExpirationYear())
					.parameter("primary", creditCardRequest.getPrimary())
					.parameter("street", creditCardRequest.getStreet())
					.parameter("city", creditCardRequest.getCity())
					.parameter("state", creditCardRequest.getState())
					.parameter("postalCode", creditCardRequest.getPostalCode())
					.parameter("countryCode", creditCardRequest.getCountryCode())
					.parameter("firstName", creditCardRequest.getFirstName())
					.parameter("lastName", creditCardRequest.getLastName())
					.execute();
			
			CreateResult<CreditCard> result = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				CreditCard creditCard = httpResponse.getEntity(CreditCard.class);
				result = new CreateResultImpl<CreditCard>(creditCard);
			} else {
				Error error = httpResponse.getEntity(Error.class);
				result = new CreateResultImpl<CreditCard>(error);
			}
			
			return result;
		}
		
		/**
		 * 
		 * @param creditCardRequest
		 * @return
		 */
		
		public UpdateResult<CreditCard> update(CreditCardRequest creditCardRequest) {
			HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.path("account-profile")
					.path(creditCardRequest.getOrganizationId())
					.path("credit-card")
					.path(creditCardRequest.getToken())
					.parameter("cardholderName", creditCardRequest.getCardholderName())
					.parameter("expirationMonth", creditCardRequest.getExpirationMonth())
					.parameter("expirationYear", creditCardRequest.getExpirationYear())
					.parameter("primary", creditCardRequest.getPrimary())
					.parameter("street", creditCardRequest.getStreet())
					.parameter("city", creditCardRequest.getCity())
					.parameter("state", creditCardRequest.getState())
					.parameter("postalCode", creditCardRequest.getPostalCode())
					.parameter("countryCode", creditCardRequest.getCountryCode())
					.parameter("firstName", creditCardRequest.getFirstName())
					.parameter("lastName", creditCardRequest.getLastName())
					.execute();
			
			UpdateResult<CreditCard> result = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				CreditCard creditCard = httpResponse.getEntity(CreditCard.class);
				result = new UpdateResultImpl<CreditCard>(creditCard);
			} else {
				Error error = httpResponse.getEntity(Error.class);
				result = new UpdateResultImpl<CreditCard>(error);
			}
			
			return result;
		}
		
		/**
		 * 
		 * @param accountProfileId
		 * @param paymentMethodToken
		 * @return
		 */
		
		public UpdateResult<CreditCard> setPrimary(String accountProfileId, String paymentMethodToken) {
			HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
					.bearerAuthorization(token.getAccessToken())
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.path("account-profile")
					.path(accountProfileId)
					.path("credit-card")
					.path(paymentMethodToken)
					.path("primary")
					.execute();
			
			UpdateResult<CreditCard> result = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				CreditCard creditCard = httpResponse.getEntity(CreditCard.class);
				result = new UpdateResultImpl<CreditCard>(creditCard);
			} else {
				Error error = httpResponse.getEntity(Error.class);
				result = new UpdateResultImpl<CreditCard>(error);
			}
			
			return result;
		}
		
		/**
		 * @param accountProfileId
		 * @param paymentMethodToken
		 * @return
		 */
		
		public CreditCard get(String accountProfileId, String paymentMethodToken) {
			HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
					.bearerAuthorization(token.getAccessToken())
					.path("account-profile")
					.path(accountProfileId)
					.path("credit-card")
					.path(paymentMethodToken)
					.execute();
			
			CreditCard resource = null;
	    	
	    	if (httpResponse.getStatusCode() == Status.OK) {
	    		resource = httpResponse.getEntity(CreditCard.class);
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