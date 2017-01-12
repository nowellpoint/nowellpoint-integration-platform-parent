package com.nowellpoint.client;

import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.ClientCredentials;
import com.nowellpoint.client.auth.ClientCredentialsGrantRequest;
import com.nowellpoint.client.auth.OauthAuthenticationResponse;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.auth.PasswordGrantRequest;
import com.nowellpoint.client.auth.RevokeTokenRequest;
import com.nowellpoint.client.auth.PasswordCredentials;
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
	
	private Token token;
	
	public NowellpointClient() {
		if (System.getenv("NOWELLPOINT_API_ENDPOINT")  != null) {
			environment = Environment.parseEnvironment("sandbox");
		} else {
			environment = Environment.parseEnvironment("production");
		}
	}
	
	public NowellpointClient(ClientCredentials credentials) {
		this();
		ClientCredentialsGrantRequest grantRequest = OauthRequests.CLIENT_CREDENTIALS_GRANT_REQUEST.builder()
				.setEnvionrment(environment)
				.setApiKeyId(credentials.getApiKeyId())
				.setApiKeySecret(credentials.getApiKeySecret()).build();
		
		OauthAuthenticationResponse oauthAuthenticationResponse = Authenticators.CLIENT_CREDENTIALS_GRANT_AUTHENTICATOR
					.authenticate(grantRequest);
		
		token = oauthAuthenticationResponse.getToken();
	}
	
	public NowellpointClient(PasswordCredentials credentials) {
		this();
		PasswordGrantRequest grantRequest = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setUsername(credentials.getUsername())
				.setPassword(credentials.getPassword())
				.build();
	
		OauthAuthenticationResponse oauthAuthenticationResponse = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(grantRequest);
		
		token = oauthAuthenticationResponse.getToken();
	}
	
	public NowellpointClient(Token token) {
		this();
		this.token = token;
	}
	
	public void logout() {
		RevokeTokenRequest revokeTokenRequest = OauthRequests.REVOKE_TOKEN_REQUEST.builder()
				.setAccessToken(token.getAccessToken())
				.build();
		
		Authenticators.REVOKE_TOKEN_INVALIDATOR.revoke(revokeTokenRequest);
	}
	
	public ApplicationResource application() {
		return new ApplicationResource(environment, token);
	}
	
	public PlanResource plan() {
		return new PlanResource(environment, token);
	}
	
	public ScheduledJobResource scheduledJob() {
		return new ScheduledJobResource(environment, token);
	}
	
	public SalesforceConnectorResource salesforceConnector() {
		return new SalesforceConnectorResource(environment, token);
	}
	
	public ScheduledJobTypeResource scheduledJobType() {
		return new ScheduledJobTypeResource(environment, token);
	}
	
	public AccountProfileResource accountProfile() {
		return new AccountProfileResource(environment, token);
	}
	
	public UserResource user() {
		return new UserResource(environment);
	}
	
	public SalesforceResource salesforce() {
		return new SalesforceResource(environment, token);
	}
}