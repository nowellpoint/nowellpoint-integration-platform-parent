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
import com.nowellpoint.client.resource.IdentityResource;
import com.nowellpoint.client.resource.PlanResource;
import com.nowellpoint.client.resource.SalesforceConnectorResource;
import com.nowellpoint.client.resource.SalesforceResource;
import com.nowellpoint.client.resource.ScheduledJobResource;
import com.nowellpoint.client.resource.ScheduledJobTypeResource;
import com.nowellpoint.client.resource.UserResource;

public class NowellpointClient {
	
	private Environment environment;
	
	private Token token;
	
	public NowellpointClient() {
		setEnvironment(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")));
	}
	
	public NowellpointClient(Environment environment) {
		setEnvironment(environment);
	}
	
	public NowellpointClient(ClientCredentials credentials) {
		ClientCredentialsGrantRequest grantRequest = OauthRequests.CLIENT_CREDENTIALS_GRANT_REQUEST.builder()
				.setEnvironment(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")))
				.setApiKeyId(credentials.getApiKeyId())
				.setApiKeySecret(credentials.getApiKeySecret()).build();
		
		OauthAuthenticationResponse oauthAuthenticationResponse = Authenticators.CLIENT_CREDENTIALS_GRANT_AUTHENTICATOR
					.authenticate(grantRequest);
		
		setToken(oauthAuthenticationResponse.getToken());
	}
	
	public NowellpointClient(PasswordCredentials credentials) {
		PasswordGrantRequest grantRequest = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setEnvironment(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")))
				.setUsername(credentials.getUsername())
				.setPassword(credentials.getPassword())
				.build();
	
		OauthAuthenticationResponse oauthAuthenticationResponse = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(grantRequest);
		
		setToken(oauthAuthenticationResponse.getToken());
	}
	
	public NowellpointClient(Environment environment, ClientCredentials credentials) {
		ClientCredentialsGrantRequest grantRequest = OauthRequests.CLIENT_CREDENTIALS_GRANT_REQUEST.builder()
				.setEnvironment(environment)
				.setApiKeyId(credentials.getApiKeyId())
				.setApiKeySecret(credentials.getApiKeySecret()).build();
		
		OauthAuthenticationResponse oauthAuthenticationResponse = Authenticators.CLIENT_CREDENTIALS_GRANT_AUTHENTICATOR
					.authenticate(grantRequest);
		
		setToken(oauthAuthenticationResponse.getToken());
	}
	
	public NowellpointClient(Environment environment, PasswordCredentials credentials) {
		PasswordGrantRequest grantRequest = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setEnvironment(environment)
				.setUsername(credentials.getUsername())
				.setPassword(credentials.getPassword())
				.build();
	
		OauthAuthenticationResponse oauthAuthenticationResponse = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(grantRequest);
		
		setToken(oauthAuthenticationResponse.getToken());
	}
	
	public NowellpointClient(Token token) {
		setToken(token);
	}
	
	private void setToken(Token token) {
		this.token = token;
	}
	
	private void setEnvironment(Environment environment) {
		this.environment = environment;
	}
	
	public void logout() {
		RevokeTokenRequest revokeTokenRequest = OauthRequests.REVOKE_TOKEN_REQUEST.builder()
				.setToken(token)
				.build();
		
		Authenticators.REVOKE_TOKEN_INVALIDATOR.revoke(revokeTokenRequest);
	}
	
	public IdentityResource identity() {
		return new IdentityResource(token);
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
		return new UserResource(environment.getEnvironmentUrl());
	}
	
	public SalesforceResource salesforce() {
		return new SalesforceResource(token);
	}
}