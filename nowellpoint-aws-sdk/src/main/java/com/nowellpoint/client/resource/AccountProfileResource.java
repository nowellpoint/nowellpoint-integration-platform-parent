package com.nowellpoint.client.resource;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.model.AccountProfile;
import com.nowellpoint.client.model.AccountProfileRequest;
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
import com.nowellpoint.client.model.SubscriptionRequest;
import com.nowellpoint.client.model.Token;

/**
 * @author jherson
 *
 */
public class AccountProfileResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "account-profile";
	
	/**
	 * 
	 * @param environmentUrl
	 * @param accessToken
	 */
	
	public AccountProfileResource(Token token) {
		super(token);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	public AccountProfile get(String id) {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(id)
				.execute();
		
		AccountProfile resource = null;
    	
    	if (httpResponse.getStatusCode() == Status.OK) {
    		resource = httpResponse.getEntity(AccountProfile.class);
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
	 * @return
	 */
	
	public DeleteResult deactivate(String id) {
		HttpResponse httpResponse = RestResource.delete(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
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
	 * @param accountProfileRequest
	 * @return
	 */
	
	public UpdateResult<AccountProfile> update(String accountProfileId, AccountProfileRequest accountProfileRequest) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path("account-profile")
				.path(accountProfileId)
				.parameter("firstName", accountProfileRequest.getFirstName())
				.parameter("lastName", accountProfileRequest.getLastName())
				.parameter("company", accountProfileRequest.getCompany())
				.parameter("division", accountProfileRequest.getDivision())
				.parameter("department", accountProfileRequest.getDepartment())
				.parameter("title", accountProfileRequest.getTitle())
				.parameter("email", accountProfileRequest.getEmail())
				.parameter("fax", accountProfileRequest.getFax())
				.parameter("mobilePhone", accountProfileRequest.getMobilePhone())
				.parameter("phone", accountProfileRequest.getPhone())
				.parameter("extension", accountProfileRequest.getExtension())
				.parameter("languageSidKey", accountProfileRequest.getLanguageSidKey())
				.parameter("localeSidKey", accountProfileRequest.getLocaleSidKey())
				.parameter("timeZoneSidKey", accountProfileRequest.getTimeZoneSidKey())
				.parameter("enableSalesforceLogin", accountProfileRequest.getEnableSalesforceLogin())
				.execute();
		
		UpdateResult<AccountProfile> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			AccountProfile resource = httpResponse.getEntity(AccountProfile.class);
			result = new UpdateResultImpl<AccountProfile>(resource);
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new UpdateResultImpl<AccountProfile>(error);
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param accountProfileId
	 * @return
	 */
	
	public UpdateResult<AccountProfile> removeProfilePicture(String accountProfileId) {
		HttpResponse httpResponse = RestResource.delete(token.getEnvironmentUrl())
    			.bearerAuthorization(token.getAccessToken())
        		.path("account-profile")
        		.path(accountProfileId)
        		.path("photo")
        		.execute();
		
		UpdateResult<AccountProfile> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			AccountProfile resource = httpResponse.getEntity(AccountProfile.class);
			result = new UpdateResultImpl<AccountProfile>(resource);
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new UpdateResultImpl<AccountProfile>(error);
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
	 * @return
	 */
	
	public SubscriptionResource subscription() {
		return new SubscriptionResource(token);
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
		
		public Address get(String accountProfileId) {
			HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
					.bearerAuthorization(token.getAccessToken())
					.path("account-profile")
					.path(accountProfileId)
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
		
		public UpdateResult<Address> update(String accountProfileId, AddressRequest addressRequest) {
			HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
					.bearerAuthorization(token.getAccessToken())
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
	    			.acceptCharset("UTF-8")
					.path("account-profile")
					.path(accountProfileId)
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
		 * @param subscriptionRequest
		 * @return
		 */
		
		public UpdateResult<Subscription> set(SubscriptionRequest subscriptionRequest) {
			HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.path(RESOURCE_CONTEXT)
					.path(subscriptionRequest.getAccountProfileId())
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
					.path(creditCardRequest.getAccountProfileId())
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
					.path(creditCardRequest.getAccountProfileId())
					.path("credit-card")
					.path(creditCardRequest.getToken())
					.parameter("cardholderName", creditCardRequest.getCardholderName())
					.parameter("cvv", creditCardRequest.getCvv())
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
					.parameter("primary", "true")
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