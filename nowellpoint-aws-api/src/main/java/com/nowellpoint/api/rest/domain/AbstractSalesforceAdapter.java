package com.nowellpoint.api.rest.domain;

import static com.nowellpoint.util.Assert.assertNotNull;
import static com.nowellpoint.util.Assert.isNull;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.nowellpoint.api.util.ClaimsContext;
import com.nowellpoint.api.util.KeyManager;
import com.nowellpoint.api.util.MessageConstants;
import com.nowellpoint.api.util.MessageProvider;
import com.nowellpoint.client.sforce.OauthConstants;
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
	public abstract String getStatus();
	
	private static final String OAUTH_TOKEN_URI = "%s/services/oauth2/token";
	
	public Connector toConnector() {
		
		if (Connector.CONNECTED.equals(getStatus())) {
			
			assertNotNull(getClientId(), MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_CLIENT_ID));
			assertNotNull(getClientSecret(), MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_CLIENT_SECRET));
			assertNotNull(getUsername(), MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_USERNAME));
			assertNotNull(getPassword(), MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_PASSWORD));
			
			SalesforceLoginResult loginResult = null;
			
			if (isNull(getConnector())) {
				
				loginResult = login(getConnectorType().getAuthEndpoint());		
				
				Connector connector = Connector.builder()
						.name(getName())
						.connectorType(getConnectorType())
						.username(getUsername())
						.password(KeyManager.encrypt(getPassword()))
						.clientId(getClientId())
						.clientSecret(KeyManager.encrypt(getClientSecret()))
						.connectedAs(loginResult.getIsConnected() ? getUsername() : null)
						.connectedOn(loginResult.getIsConnected() ? new Date(Long.valueOf(loginResult.getToken().getIssuedAt())) : null)
						.status(loginResult.getStatus())
						.isConnected(loginResult.getIsConnected())
						.build();
				
				return connector;
				
			} else {
				
				loginResult = login(getConnectorType().getAuthEndpoint());	
				
				Connector connector = Connector.builder()
						.from(getConnector())
						.name(getName())
						.lastUpdatedBy(UserInfo.of(ClaimsContext.getClaims()))
						.lastUpdatedOn(Date.from(Instant.now()))
						.username(getUsername())
						.password(KeyManager.encrypt(getPassword()))
						.clientId(getClientId())
						.clientSecret(KeyManager.encrypt(getClientSecret()))
						.connectedAs(loginResult.getIsConnected() ? getUsername() : null)
						.connectedOn(loginResult.getIsConnected() ? new Date(Long.valueOf(loginResult.getToken().getIssuedAt())) : null)
						.status(loginResult.getStatus())
						.isConnected(loginResult.getIsConnected())
						.build();
				
				return connector;
			}		
		} else {
			
			Connector connector = Connector.builder()
					.name(getName())
					.connectorType(getConnectorType())
					.username(getUsername())
					.password(KeyManager.encrypt(getPassword()))
					.clientId(getClientId())
					.clientSecret(KeyManager.encrypt(getClientSecret()))
					.status(Connector.NOT_CONNECTED)
					.isConnected(Boolean.FALSE)
					.build();
			
			return connector;
		}
	}
	
	private SalesforceLoginResult login(String authEndpoint) {
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
		
		if (httpResponse.getStatusCode() == Status.OK) {
			return SalesforceLoginResult.builder()
					.isConnected(Boolean.TRUE)
					.status(Connector.CONNECTED)
					.token(httpResponse.getEntity(Token.class))
					.build();
		} else {
			Error error = httpResponse.getEntity(Error.class);
			return SalesforceLoginResult.builder()
					.isConnected(Boolean.FALSE)
					.status(String.format("%s. Error: %s - %s )", Connector.FAILED_TO_CONNECT, error.getError(), error.getErrorDescription()))
					.build();
		}
	}
}