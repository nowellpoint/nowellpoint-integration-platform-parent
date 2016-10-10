package com.nowellpoint.client.resource;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.model.AccountProfile;
import com.nowellpoint.client.model.AddResult;
import com.nowellpoint.client.model.AddSubscriptionRequest;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.GetAccountProfileRequest;
import com.nowellpoint.client.model.NowellpointServiceException;
import com.nowellpoint.client.model.Subscription;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.UpdateSubscriptionRequest;
import com.nowellpoint.client.model.idp.Token;

public class AccountProfileResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "account-profile";
	
	public AccountProfileResource(Token token) {
		super(token);
	}
	
	public AccountProfile getMyAccountProfile() {
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.path("me")
				.execute();
		
		AccountProfile accountProfile = null;
    	
    	if (httpResponse.getStatusCode() == 200) {
    		accountProfile = httpResponse.getEntity(AccountProfile.class);
    	} else {
    		throw new NowellpointServiceException(httpResponse.getStatusCode(), httpResponse.getAsString());
    	}
    	
    	return accountProfile;
	} 
	
	public AccountProfile getAccountProfile(GetAccountProfileRequest getAccountProfileRequest) {
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.path(getAccountProfileRequest.getId())
				.execute();
		
		AccountProfile accountProfile = null;
    	
		if (httpResponse.getStatusCode() == Status.OK) {
			accountProfile = httpResponse.getEntity(AccountProfile.class);
		} else {
			throw new NowellpointServiceException(httpResponse.getStatusCode(), httpResponse.getAsString());
		}
    	
    	return accountProfile;
	} 
	
	public SubscriptionResource subscription() {
		return new SubscriptionResource(token);
	}
	
	public class SubscriptionResource extends AbstractResource {
		
		public SubscriptionResource(Token token) {
			super(token);
		}
		
		public UpdateResult<Subscription> update(UpdateSubscriptionRequest updateSubscriptionRequest) {
			HttpResponse httpResponse = RestResource.put(API_ENDPOINT)
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
					.path(RESOURCE_CONTEXT)
					.path(updateSubscriptionRequest.getAccountProfileId())
					.path("subscription")
					.parameter("currencyIsoCode", updateSubscriptionRequest.getCurrencyIsoCode())
					.parameter("planCode", updateSubscriptionRequest.getPlanCode())
					.parameter("unitPrice", String.valueOf(updateSubscriptionRequest.getUnitPrice()))
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
		
		public AddResult<Subscription> add(AddSubscriptionRequest addSubscriptionRequest) {
			HttpResponse httpResponse = RestResource.put(API_ENDPOINT)
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
					.path(RESOURCE_CONTEXT)
					.path(addSubscriptionRequest.getAccountProfileId())
					.path("subscription")
					.parameter("currencyIsoCode", addSubscriptionRequest.getCurrencyIsoCode())
					.parameter("planCode", addSubscriptionRequest.getPlanCode())
					.parameter("unitPrice", String.valueOf(addSubscriptionRequest.getUnitPrice()))
					.execute();
			
			AddResult<Subscription> result = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				Subscription subscription = httpResponse.getEntity(Subscription.class);
				result = new AddResultImpl<Subscription>(subscription);
			} else {
				Error error = httpResponse.getEntity(Error.class);
				result = new AddResultImpl<Subscription>(error);
			}
			
			return result;
		}
	}
}