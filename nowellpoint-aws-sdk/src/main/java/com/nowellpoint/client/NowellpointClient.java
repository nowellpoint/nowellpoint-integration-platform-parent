package com.nowellpoint.client;

import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.OauthAuthenticationResponse;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.auth.PasswordGrantRequest;
import com.nowellpoint.client.auth.RevokeTokenRequest;
import com.nowellpoint.client.auth.TokenCredentials;
import com.nowellpoint.client.auth.UsernamePasswordCredentials;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.resource.AccountProfileResource;
import com.nowellpoint.client.resource.ApplicationResource;
import com.nowellpoint.client.resource.PlanResource;
import com.nowellpoint.client.resource.SalesforceConnectorResource;
import com.nowellpoint.client.resource.SalesforceResource;
import com.nowellpoint.client.resource.ScheduledJobResource;
import com.nowellpoint.client.resource.ScheduledJobTypeResource;
import com.nowellpoint.client.resource.UserResource;

public class NowellpointClient {
	
	private static Environment environment;
	
	private static Token token;
	
	public NowellpointClient() {
		if (System.getenv("NOWELLPOINT_API_ENDPOINT")  != null) {
			environment = Environment.parseEnvironment("sandbox");
		} else {
			environment = Environment.parseEnvironment("production");
		}
	}
	
	public NowellpointClient(UsernamePasswordCredentials credentials) {
		this();
		PasswordGrantRequest passwordGrantRequest = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setUsername(credentials.getUsername())
				.setPassword(credentials.getPassword())
				.build();
	
		OauthAuthenticationResponse oauthAuthenticationResponse = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(passwordGrantRequest);
		
		token = oauthAuthenticationResponse.getToken();
	}
	
	public NowellpointClient(TokenCredentials credentials) {
		this();
		token = credentials.getToken();
	}
	
	public void logout() {
		RevokeTokenRequest revokeTokenRequest = OauthRequests.REVOKE_TOKEN_REQUEST.builder()
				.setAccessToken(token.getAccessToken())
				.build();
		
		Authenticators.REVOKE_TOKEN_INVALIDATOR.revoke(revokeTokenRequest);
	}
	
	public ApplicationResource application() {
		return new ApplicationResource(token);
	}
	
	public PlanResource plan() {
		return new PlanResource(token);
	}
	
	public ScheduledJobResource scheduledJob() {
		return new ScheduledJobResource(token);
	}
	
	public SalesforceConnectorResource salesforceConnector() {
		return new SalesforceConnectorResource(token);
	}
	
	public ScheduledJobTypeResource scheduledJobType() {
		return new ScheduledJobTypeResource(token);
	}
	
	public AccountProfileResource accountProfile() {
		return new AccountProfileResource(token);
	}
	
	public UserResource user() {
		return new UserResource();
	}
	
	public SalesforceResource salesforce() {
		return new SalesforceResource(token);
	}
}