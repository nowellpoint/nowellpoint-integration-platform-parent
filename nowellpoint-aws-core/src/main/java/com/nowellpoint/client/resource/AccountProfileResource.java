package com.nowellpoint.client.resource;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.model.AccountProfile;
import com.nowellpoint.client.model.GetAccountProfileRequest;
import com.nowellpoint.client.model.NowellpointServiceException;
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
}