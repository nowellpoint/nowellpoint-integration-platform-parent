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
import com.nowellpoint.client.resource.ScheduledJobResource;
import com.nowellpoint.client.resource.ScheduledJobTypeResource;

public class NowellpointClient {
	
	private static Token token;
	
	public NowellpointClient(UsernamePasswordCredentials credentials) {
		PasswordGrantRequest passwordGrantRequest = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setUsername(credentials.getUsername())
				.setPassword(credentials.getPassword())
				.build();
	
		OauthAuthenticationResponse oauthAuthenticationResponse = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(passwordGrantRequest);
		
		token = oauthAuthenticationResponse.getToken();
	}
	
	public NowellpointClient(TokenCredentials credentials) {
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
}