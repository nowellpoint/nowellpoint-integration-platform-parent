package com.nowellpoint.aws.lambda.idp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.Configuration;
import com.nowellpoint.aws.model.idp.GetAccountRequest;
import com.nowellpoint.aws.model.idp.GetAccountResponse;

public class ClientCredentialsAuthentication implements RequestHandler<GetAccountRequest, GetAccountResponse> {
	
	
	@Override
	public GetAccountResponse handleRequest(GetAccountRequest request, Context context) { 
		HttpResponse httpResponse = null;
		try {
			httpResponse = RestResource.post(Configuration.getStormpathApiEndpoint())
					.path(Configuration.getStormpathApplicationId())
					.path("oauth/token")
					.basicAuthorization(Configuration.getStormpathApiKeyId(), Configuration.getStormpathApiKeySecret())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.parameter("grant_type", "client_credentials")
					.execute();
		} catch (Exception e) {
			
		}
		return null;
		
	}	
}