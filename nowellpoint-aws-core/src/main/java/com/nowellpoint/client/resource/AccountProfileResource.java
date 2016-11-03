package com.nowellpoint.client.resource;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.model.AccountProfile;
import com.nowellpoint.client.model.CreditCardRequest;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.AddResult;
import com.nowellpoint.client.model.Address;
import com.nowellpoint.client.model.Contact;
import com.nowellpoint.client.model.CreditCard;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.GetResult;
import com.nowellpoint.client.model.NotFoundException;
import com.nowellpoint.client.model.Subscription;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.SetResult;
import com.nowellpoint.client.model.SubscriptionRequest;
import com.nowellpoint.client.model.idp.Token;

public class AccountProfileResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "account-profile";
	
	public AccountProfileResource(Token token) {
		super(token);
	}
	
	public GetResult<AccountProfile> get() {
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.path("me")
				.execute();
		
		GetResult<AccountProfile> result = null;
    	
    	if (httpResponse.getStatusCode() == Status.OK) {
    		AccountProfile accountProfile = httpResponse.getEntity(AccountProfile.class);
    		result = new GetResultImpl<AccountProfile>(accountProfile); 
    	} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
    		Error error = httpResponse.getEntity(Error.class);
			result = new GetResultImpl<AccountProfile>(error);
    	}
    	
    	return result;
	} 
	
	public GetResult<AccountProfile> get(String id) {
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.path(id)
				.execute();
		
		GetResult<AccountProfile> result = null;
    	
    	if (httpResponse.getStatusCode() == Status.OK) {
    		AccountProfile accountProfile = httpResponse.getEntity(AccountProfile.class);
    		result = new GetResultImpl<AccountProfile>(accountProfile); 
    	} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
    		Error error = httpResponse.getEntity(Error.class);
			result = new GetResultImpl<AccountProfile>(error);
    	}
    	
    	return result;
	} 
	
	public DeleteResult deactivate(String id) {
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
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
	
	public SubscriptionResource subscription() {
		return new SubscriptionResource(token);
	}
	
	public CreditCardResource creditCard() {
		return new CreditCardResource(token);
	}
	
	public class SubscriptionResource extends AbstractResource {
		
		public SubscriptionResource(Token token) {
			super(token);
		}
		
		public SetResult<Subscription> set(SubscriptionRequest subscriptionRequest) {
			HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
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
	
	public class CreditCardResource extends AbstractResource {
		
		public CreditCardResource(Token token) {
			super(token);
		}
		
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
			
			HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
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
			
			HttpResponse httpResponse = RestResource.put(API_ENDPOINT)
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
	}
}