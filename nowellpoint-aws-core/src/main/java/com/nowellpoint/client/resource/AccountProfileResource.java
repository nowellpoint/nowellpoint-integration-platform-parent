package com.nowellpoint.client.resource;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.model.AccountProfile;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.GetResult;
import com.nowellpoint.client.model.NotFoundException;
import com.nowellpoint.client.model.Subscription;
import com.nowellpoint.client.model.SetResult;
import com.nowellpoint.client.model.SetSubscriptionRequest;
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
	
	public SubscriptionResource subscription() {
		return new SubscriptionResource(token);
	}
	
	public class SubscriptionResource extends AbstractResource {
		
		public SubscriptionResource(Token token) {
			super(token);
		}
		
		public SetResult<Subscription> set(SetSubscriptionRequest setSubscriptionRequest) {
			HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
					.path(RESOURCE_CONTEXT)
					.path(setSubscriptionRequest.getAccountProfileId())
					.path("subscription")
					.parameter("currencyIsoCode", setSubscriptionRequest.getCurrencyIsoCode())
					.parameter("planCode", setSubscriptionRequest.getPlanCode())
					.parameter("unitPrice", String.valueOf(setSubscriptionRequest.getUnitPrice()))
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
}