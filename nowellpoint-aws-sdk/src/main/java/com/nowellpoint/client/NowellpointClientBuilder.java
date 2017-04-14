package com.nowellpoint.client;

import com.nowellpoint.client.auth.Authenticators;
import com.nowellpoint.client.auth.ClientCredentials;
import com.nowellpoint.client.auth.ClientCredentialsGrantRequest;
import com.nowellpoint.client.auth.OauthAuthenticationResponse;
import com.nowellpoint.client.auth.OauthRequests;
import com.nowellpoint.client.auth.PasswordCredentials;
import com.nowellpoint.client.auth.PasswordGrantRequest;
import com.nowellpoint.client.auth.RevokeTokenRequest;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.resource.AccountProfileResource;
import com.nowellpoint.client.resource.IdentityResource;
import com.nowellpoint.client.resource.JobResource;
import com.nowellpoint.client.resource.JobTypeResource;
import com.nowellpoint.client.resource.PlanResource;
import com.nowellpoint.client.resource.SalesforceConnectorResource;
import com.nowellpoint.client.resource.SalesforceResource;
import com.nowellpoint.util.Assert;

public final class NowellpointClientBuilder {
	
	private Token token;
	
	private NowellpointClientBuilder() {
		
	}
	
	private void setToken(Token token) {
		this.token = token;
	}
	
	public static NowellpointClientOrig defaultClient(Token token) {
		return new NowellpointClientOrig(token);
	}
	
	public static NowellpointClientBuilder newInstance() {
		return new NowellpointClientBuilder();
	}
	
	public NowellpointClient build() {
		return new NowellpointClientImpl(token);
	}
	
	public NowellpointClientBuilder withToken(Token token) {
		setToken(token);
		return this;
	}
	
	public NowellpointClientBuilder withPasswordCredentials(Environment environment, PasswordCredentials credentials) {
		PasswordGrantRequest grantRequest = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setEnvironment(environment)
				.setUsername(credentials.getUsername())
				.setPassword(credentials.getPassword())
				.build();
	
		OauthAuthenticationResponse oauthAuthenticationResponse = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(grantRequest);
		
		setToken(oauthAuthenticationResponse.getToken());
		
		return this;
	}
	
	public NowellpointClientBuilder withClientCredentials(Environment environment, ClientCredentials credentials) {
		ClientCredentialsGrantRequest grantRequest = OauthRequests.CLIENT_CREDENTIALS_GRANT_REQUEST.builder()
				.setEnvironment(environment)
				.setApiKeyId(credentials.getApiKeyId())
				.setApiKeySecret(credentials.getApiKeySecret()).build();
		
		OauthAuthenticationResponse oauthAuthenticationResponse = Authenticators.CLIENT_CREDENTIALS_GRANT_AUTHENTICATOR
					.authenticate(grantRequest);
		
		setToken(oauthAuthenticationResponse.getToken());
		
		return this;
	}
	
	public class NowellpointClientImpl implements NowellpointClient {
		
		private Token token;
		
		private NowellpointClientImpl(Token token) {
			this.token = token;
		}
		
		@Override
		public Token getToken() {
			Assert.assertNotNull(token, "Missing token for request. Please provide a valid token through the NowellpointClientBuilder");
			return token;
		}

		@Override
		public void logout() {
			RevokeTokenRequest revokeTokenRequest = OauthRequests.REVOKE_TOKEN_REQUEST.builder()
					.setToken(getToken())
					.build();
			
			Authenticators.REVOKE_TOKEN_INVALIDATOR.revoke(revokeTokenRequest);
		}
		
		@Override
		public IdentityResource identity() {
			return new IdentityResource(getToken());
		}
		
		@Override
		public PlanResource plan() {
			return new PlanResource(getToken());
		}
		
		@Override
		public JobResource job() {
			return new JobResource(getToken());
		}
		
		@Override
		public SalesforceConnectorResource salesforceConnector() {
			return new SalesforceConnectorResource(getToken());
		}
		
		@Override
		public JobTypeResource scheduledJobType() {
			return new JobTypeResource(getToken());
		}
		
		@Override
		public AccountProfileResource accountProfile() {
			return new AccountProfileResource(getToken());
		}
		
		@Override
		public SalesforceResource salesforce() {
			return new SalesforceResource(getToken());
		}
	}
}