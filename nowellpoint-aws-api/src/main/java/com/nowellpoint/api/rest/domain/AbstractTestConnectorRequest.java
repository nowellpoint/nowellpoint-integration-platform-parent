package com.nowellpoint.api.rest.domain;

import org.immutables.value.Value;

import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.UsernamePasswordGrantRequest;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractTestConnectorRequest {
	public abstract ConnectorType getConnectorType();
	public abstract String getClientId();
	public abstract String getClientSecret();
	public abstract String getUsername();
	public abstract String getPassword();
	
	public String test() {
		String status = null;
		
		if ("SALESFORCE_SANDBOX".equals(getConnectorType().getName()) || "SALESFORCE_PRODUCTION".equals(getConnectorType().getName())) {
			try {
				login(getConnectorType().getAuthEndpoint(), getClientId(), getClientSecret(), getUsername(), getPassword());
				status = "CONNECTED";
			} catch (OauthException e) {
				status = String.format("%s. %s: %s", "FAILED_TO_CONNECT", e.getError(), e.getErrorDescription());
			}
		}
		
		return status;
	}
	
	private OauthAuthenticationResponse login(String authEndpoint, String clientId, String clientSecret, String username, String password) {
		UsernamePasswordGrantRequest request = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setClientId(clientId)
				.setClientSecret(clientSecret)
				.setUsername(username)
				.setPassword(password)
				.build();
		
		OauthAuthenticationResponse authenticationResponse = Authenticators.PASSWORD_GRANT_AUTHENTICATOR.authenticate(request);
		
		return authenticationResponse;
	}
}