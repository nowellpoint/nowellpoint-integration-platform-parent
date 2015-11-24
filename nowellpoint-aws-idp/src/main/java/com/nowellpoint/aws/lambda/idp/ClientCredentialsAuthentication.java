package com.nowellpoint.aws.lambda.idp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.nowellpoint.aws.model.idp.GetAccountRequest;
import com.nowellpoint.aws.model.idp.GetAccountResponse;
import com.stormpath.sdk.api.ApiAuthenticationResult;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.Clients;
import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.http.HttpRequests;
import com.stormpath.sdk.impl.util.Base64;

public class ClientCredentialsAuthentication implements RequestHandler<GetAccountRequest, GetAccountResponse> {
	
	private static final Client client;
	private static final Application application;

	static {
		client = Clients.builder()
				.setApiKey(ApiKeys.builder()
						.setId(System.getenv("STORMPATH_API_KEY_ID"))
						.setSecret(System.getenv("STORMPATH_API_KEY_SECRET"))
						.build())
					.build();

		application = client.getResource(System.getenv("STORMPATH_APPLICATION"), Application.class);
	}
	
	@Override
	public GetAccountResponse handleRequest(GetAccountRequest request, Context context) { 
		String clientCredentials = System.getenv("NOWELLPOINT_API_KEY_ID").concat(":").concat(System.getenv("NOWELLPOINT_API_KEY_SECRET"));
		String basicToken = new String(Base64.encodeBase64(clientCredentials.getBytes()));

		HttpRequest httpRequest = HttpRequests.method(HttpMethod.POST)
				.addHeader("grant_type", new String[] {"client_credentials"})
				.addHeader("Authorization", new String[] {"Basic " + basicToken})
				.build();
		
		 ApiAuthenticationResult result = Applications.apiRequestAuthenticator(application).authenticate(httpRequest);
		 
		 GetAccountResponse response = new GetAccountResponse();
		 //response.setAccount(result.getAccount());
		 
		 return response;
	}	
}