package com.nowellpoint.api.rest.domain;

import static com.nowellpoint.util.Assert.isNull;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.nowellpoint.api.util.ClaimsContext;
import com.nowellpoint.api.util.KeyManager;
import com.nowellpoint.client.sforce.OauthConstants;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.model.Error;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractSalesforceAdapter {
	public abstract @Nullable Connector getConnector();
	public abstract @Nullable ConnectorType getConnectorType();
	public abstract String getName();
	public abstract String getUsername();
	public abstract String getPassword();
	public abstract String getClientId();
	public abstract String getClientSecret();
	
	private static final String OAUTH_TOKEN_URI = "%s/services/oauth2/token";
	
	public static Connector refresh(Connector connector) {
		SalesforceAdapter adapter = SalesforceAdapter.builder()
				.clientId(connector.getClientId())
				.clientSecret(KeyManager.decrypt(connector.getClientSecret()))
				.connector(connector)
				.name(connector.getName())
				.password(KeyManager.decrypt(connector.getPassword()))
				.username(connector.getUsername())
				.build();
			
		return adapter.toConnector();	
	}
	
	public Connector toConnector() {
		
		String status = "Connected";
		Boolean isConnected = Boolean.TRUE;
		
		Token token = null;
		
		if (isNull(getConnector())) {
			
			try {
				token = login(getConnectorType().getAuthEndpoint());			
			} catch (OauthException e) {
				isConnected = Boolean.FALSE;
				status = String.format("Failed to Connect. Error: %s [ %s ]", e.getError(), e.getErrorDescription());
			}
			
			Connector connector = Connector.builder()
					.name(getName())
					.connectorType(getConnectorType())
					.username(isConnected ? getUsername() : null)
					.password(isConnected ? KeyManager.encrypt(getPassword()) : null)
					.clientId(isConnected ? getClientId() : null)
					.clientSecret(isConnected ? KeyManager.encrypt(getClientSecret()) : null)
					.connectedAs(isConnected ? getUsername() : null)
					.connectedOn(isConnected ? new Date(Long.valueOf(token.getIssuedAt())) : null)
					.status(status)
					.isConnected(isConnected)
					.build();
			
			return connector;
			
		} else {
			
			try {
				token = login(getConnector().getConnectorType().getAuthEndpoint());			
			} catch (OauthException e) {
				isConnected = Boolean.FALSE;
				status = String.format("Failed to Connect. Error: %s - %s )", e.getError(), e.getErrorDescription());
			}
			
			Connector connector = Connector.builder()
					.from(getConnector())
					.name(getName())
					.lastUpdatedBy(UserInfo.of(ClaimsContext.getClaims()))
					.lastUpdatedOn(Date.from(Instant.now()))
					.username(isConnected ? getUsername() : null)
					.password(isConnected ? KeyManager.encrypt(getPassword()) : null)
					.clientId(isConnected ? getClientId() : null)
					.clientSecret(isConnected ? KeyManager.encrypt(getClientSecret()) : null)
					.connectedAs(isConnected ? getUsername() : null)
					.connectedOn(isConnected ? new Date(Long.valueOf(token.getIssuedAt())) : null)
					.status(status)
					.isConnected(isConnected)
					.build();
			
			return connector;
		}		
	}
	
	private Token login(String authEndpoint) {
		HttpResponse httpResponse = RestResource.post(String.format(OAUTH_TOKEN_URI, authEndpoint))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.acceptCharset(StandardCharsets.UTF_8)
				.parameter(OauthConstants.GRANT_TYPE_PARAMETER, OauthConstants.PASSWORD_GRANT_TYPE)
				.parameter(OauthConstants.CLIENT_ID_PARAMETER, getClientId())
				.parameter(OauthConstants.CLIENT_SECRET_PARAMETER, getClientSecret())
				.parameter(OauthConstants.USERNAME_PARAMETER, getUsername())
				.parameter(OauthConstants.PASSWORD_PARAMETER, getPassword())
				.execute();
		
		Token token = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			token = httpResponse.getEntity(Token.class);
		} else {
			throw new OauthException(httpResponse.getStatusCode(), httpResponse.getEntity(Error.class));
		}
		
		return token;
	}
}