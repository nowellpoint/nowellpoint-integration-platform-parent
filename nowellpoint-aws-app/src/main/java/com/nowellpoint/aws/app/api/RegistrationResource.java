package com.nowellpoint.aws.app.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.nowellpoint.aws.client.IdentityProviderClient;
import com.nowellpoint.aws.model.idp.GetTokenRequest;
import com.nowellpoint.aws.model.idp.GetTokenResponse;

@Path("/registration")
public class RegistrationResource {
	
	private static IdentityProviderClient identityProviderClient = new IdentityProviderClient();
	
	public RegistrationResource() {
		
	}

	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public String findAll() {
		
		long startTime = System.currentTimeMillis();
		
		GetTokenRequest tokenRequest = new GetTokenRequest().withUsername(System.getenv("STORMPATH_USERNAME"))
				.withPassword(System.getenv("STORMPATH_PASSWORD"));
		
		GetTokenResponse tokenResponse = identityProviderClient.authenticate(tokenRequest);
			
		String accessToken = tokenResponse.getToken().getAccessToken();
			
		System.out.println("Authenticating...success: " + tokenResponse.getToken().getStormpathAccessTokenHref());
		
		System.out.println("Authenticate: " + (System.currentTimeMillis() - startTime));
		
        return "Authenticating...success: " + tokenResponse.getToken().getStormpathAccessTokenHref();
    }
}