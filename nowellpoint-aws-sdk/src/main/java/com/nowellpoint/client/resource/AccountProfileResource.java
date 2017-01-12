package com.nowellpoint.client.resource;

import org.infinispan.Cache;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.Environment;
import com.nowellpoint.client.model.AccountProfile;
import com.nowellpoint.client.model.AccountProfileRequest;
import com.nowellpoint.client.model.CreditCardRequest;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.AddResult;
import com.nowellpoint.client.model.Address;
import com.nowellpoint.client.model.AddressRequest;
import com.nowellpoint.client.model.Contact;
import com.nowellpoint.client.model.CreditCard;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.Subscription;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.exception.NotFoundException;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.client.model.SetResult;
import com.nowellpoint.client.model.SubscriptionRequest;
import com.nowellpoint.client.model.Token;

/**
 * @author jherson
 *
 */
public class AccountProfileResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "account-profile";
	
	/**
	 * @param token
	 */
	
	public AccountProfileResource(Environment environment, Token token) {
		super(environment, token);
	}
	
	/**
	 * @return
	 */
	
	public AccountProfile get() {
		Cache<String, AccountProfile> cache = cacheManager.getCache();
		AccountProfile resource = cache.get(token.getAccessToken());
		if (resource == null) {
			
			HttpResponse httpResponse = RestResource.get(environment.getEnvironmentUrl())
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
					.path(RESOURCE_CONTEXT)
					.path("me")
					.execute();
	    	
	    	if (httpResponse.getStatusCode() == Status.OK) {
	    		resource = httpResponse.getEntity(AccountProfile.class);
	    	} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
				throw new NotFoundException(httpResponse.getAsString());
			} else {
				throw new ServiceUnavailableException(httpResponse.getAsString());
	    	}
	    	
	    	cache.put(token.getAccessToken(), resource);
		}
		
		else {
			System.out.println("found account profile in cache: " + resource.getId());
		}
    	
    	return resource;
	} 
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	public AccountProfile get(String id) {
		HttpResponse httpResponse = RestResource.get(environment.getEnvironmentUrl())
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
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
		HttpResponse httpResponse = RestResource.delete(environment.getEnvironmentUrl())
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
		HttpResponse httpResponse = RestResource.post(environment.getEnvironmentUrl())
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
		HttpResponse httpResponse = RestResource.delete(environment.getEnvironmentUrl())
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
		return new AddressResource(environment, token);
	}
	
	/**
	 * 
	 * @return
	 */
	
	public SubscriptionResource subscription() {
		return new SubscriptionResource(environment, token);
	}
	
	/**
	 * 
	 * @return
	 */
	
	public CreditCardResource creditCard() {
		return new CreditCardResource(environment, token);
	}
	
	/**
	 * 
	 * @author jherson
	 *
	 */
	
	public class AddressResource extends AbstractResource {
		
		/**
		 * 
		 * @param token
		 */
		
		public AddressResource(Environment environment, Token token) {
			super(environment, token);
		}
		
		/**
		 * 
		 * @param accountProfileId
		 * @return
		 */
		
		public Address get(String accountProfileId) {
			HttpResponse httpResponse = RestResource.get(environment.getEnvironmentUrl())
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
			HttpResponse httpResponse = RestResource.post(environment.getEnvironmentUrl())
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
		
		public SubscriptionResource(Environment environment, Token token) {
			super(environment, token);
		}
		
		/**
		 * 
		 * @param subscriptionRequest
		 * @return
		 */
		
		public SetResult<Subscription> set(SubscriptionRequest subscriptionRequest) {
			HttpResponse httpResponse = RestResource.post(environment.getEnvironmentUrl())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
					.path(RESOURCE_CONTEXT)
					.path(subscriptionRequest.getAccountProfileId())
					.path("subscription")
					.parameter("planId", subscriptionRequest.getPlanId())
					.parameter("paymentMethodToken", subscriptionRequest.getPaymentMethodToken())
					.execute();
			
			SetResult<Subscription> result = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				Subscription subscription = httpResponse.getEntity(Subscription.class);
				result = new SetResultImpl<Subscription>(subscription);
			} else {
				Error error = httpResponse.getEntity(Error.class);
				result = new SetResultImpl<Subscription>(error);
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
		
		public CreditCardResource(Environment environment, Token token) {
			super(environment, token);
		}
		
		/**
		 * 
		 * @param creditCardRequest
		 * @return
		 */
		
		public AddResult<CreditCard> add(CreditCardRequest creditCardRequest) {
			
			CreditCard creditCard = new CreditCard()
					.withBillingAddress(new Address()
							.withCity(creditCardRequest.getCity())
							.withCountryCode(creditCardRequest.getCountryCode())
							.withPostalCode(creditCardRequest.getPostalCode())
							.withState(creditCardRequest.getState())
							.withStreet(creditCardRequest.getStreet()))
					.withBillingContact(new Contact()
							.withFirstName(creditCardRequest.getFirstName())
							.withLastName(creditCardRequest.getLastName()))
					.withCardholderName(creditCardRequest.getCardholderName())
					.withExpirationMonth(creditCardRequest.getExpirationMonth())
					.withExpirationYear(creditCardRequest.getExpirationYear())
					.withNumber(creditCardRequest.getNumber())
					.withCvv(creditCardRequest.getCvv())
					.withPrimary(creditCardRequest.getPrimary());
			
			HttpResponse httpResponse = RestResource.post(environment.getEnvironmentUrl())
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
					.path("account-profile")
					.path(creditCardRequest.getAccountProfileId())
					.path("credit-card")
					.body(creditCard)
					.execute();
			
			AddResult<CreditCard> result = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				creditCard = httpResponse.getEntity(CreditCard.class);
				result = new AddResultImpl<CreditCard>(creditCard);
			} else {
				Error error = httpResponse.getEntity(Error.class);
				result = new AddResultImpl<CreditCard>(error);
			}
			
			return result;
		}
		
		/**
		 * 
		 * @param creditCardRequest
		 * @return
		 */
		
		public UpdateResult<CreditCard> update(CreditCardRequest creditCardRequest) {
			
			CreditCard creditCard = new CreditCard()
					.withBillingAddress(new Address()
							.withCity(creditCardRequest.getCity())
							.withCountryCode(creditCardRequest.getCountryCode())
							.withPostalCode(creditCardRequest.getPostalCode())
							.withState(creditCardRequest.getState())
							.withStreet(creditCardRequest.getStreet()))
					.withBillingContact(new Contact()
							.withFirstName(creditCardRequest.getFirstName())
							.withLastName(creditCardRequest.getLastName()))
					.withCardholderName(creditCardRequest.getCardholderName())
					.withExpirationMonth(creditCardRequest.getExpirationMonth())
					.withExpirationYear(creditCardRequest.getExpirationYear())
					.withNumber(creditCardRequest.getNumber())
					.withCvv(creditCardRequest.getCvv())
					.withPrimary(creditCardRequest.getPrimary());
			
			HttpResponse httpResponse = RestResource.put(environment.getEnvironmentUrl())
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
					.path("account-profile")
					.path(creditCardRequest.getAccountProfileId())
					.path("credit-card")
					.path(creditCardRequest.getToken())
					.body(creditCard)
					.execute();
			
			UpdateResult<CreditCard> result = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				creditCard = httpResponse.getEntity(CreditCard.class);
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
			HttpResponse httpResponse = RestResource.post(environment.getEnvironmentUrl())
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
			HttpResponse httpResponse = RestResource.get(environment.getEnvironmentUrl())
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
			HttpResponse httpResponse = RestResource.delete(environment.getEnvironmentUrl())
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