package com.nowellpoint.www.app.service;

import java.util.logging.Logger;

import javax.ws.rs.BadRequestException;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.www.app.model.AccountProfile;

public class AccountProfileService {
	
	private static final Logger LOGGER = Logger.getLogger(AccountProfileService.class.getName());

	public AccountProfile getMyAccountProfile(GetMyAccountProfileRequest request) {
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(request.getAccessToken())
				.path("account-profile")
				.path("me")
				.execute();
		
		int statusCode = httpResponse.getStatusCode();
    	
    	LOGGER.info("Status Code: " + statusCode + " Method: GET : " + httpResponse.getURL());
    	
    	if (statusCode != 200) {
    		throw new BadRequestException(httpResponse.getAsString());
    	}
    	
    	return httpResponse.getEntity(AccountProfile.class);
	}
}