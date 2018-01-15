package com.nowellpoint.api.rest.domain;

import static com.nowellpoint.util.Assert.assertNotNull;
import static com.nowellpoint.util.Assert.isNotNullOrEmpty;
import static com.nowellpoint.util.Assert.isNull;
import static com.nowellpoint.util.Assert.isNotNull;

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
	public abstract @Nullable String getName();
	public abstract @Nullable String getUsername();
	public abstract @Nullable String getPassword();
	public abstract @Nullable String getClientId();
	public abstract @Nullable String getClientSecret();
	public abstract @Nullable String getStatus();
	
	private static final String OAUTH_TOKEN_URI = "%s/services/oauth2/token";
	
	public Connector toConnector() {
		
		SalesforceLoginResult loginResult = SalesforceLoginResult.builder().build();
		
		if (isNull(getConnector())) {
			
			assertNotNull(getName(), MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_NAME));
			
			if (Connector.CONNECTED.equals(getStatus())) {
				
				assertNotNull(getClientId(), MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_CLIENT_ID));
				assertNotNull(getClientSecret(), MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_CLIENT_SECRET));
				assertNotNull(getUsername(), MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_USERNAME));
				assertNotNull(getPassword(), MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_PASSWORD));
				
				loginResult = login(getConnectorType().getAuthEndpoint(), getClientId(), getClientSecret(), getUsername(), getPassword());	
			}
			
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
			
			String name = isNotNullOrEmpty(getName()) ? getName() : getConnector().getName();
			String username = isNotNullOrEmpty(getUsername()) ? getUsername() : getConnector().getUsername();
			String password = isNotNullOrEmpty(getPassword()) ? getPassword() : KeyManager.decrypt(getConnector().getPassword());
			String clientId = isNotNullOrEmpty(getClientId()) ? getClientId() : getConnector().getClientId();
			String clientSecret = isNotNullOrEmpty(getClientSecret()) ? getClientSecret() : KeyManager.decrypt(getConnector().getClientSecret());
			String status = isNotNullOrEmpty(getStatus()) ? getStatus() : getConnector().getStatus();
			
			if (! Connector.CONNECTED.equals(status) 
					&& ! Connector.DISCONNECTED.equals(status) 
					&& ! Connector.NOT_CONNECTED.equals(status)
					&& status.indexOf(Connector.FAILED_TO_CONNECT) == -1) {
				
				throw new IllegalArgumentException(String.format(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_INVALID_STATUS), status));
			}
			
			assertNotNull(name, MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_NAME));
			
			if (Connector.CONNECTED.equals(status)) {
				
				assertNotNull(clientId, MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_CLIENT_ID));
				assertNotNull(clientSecret, MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_CLIENT_SECRET));
				assertNotNull(username, MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_USERNAME));
				assertNotNull(password, MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_PASSWORD));
				
				loginResult = login(getConnector().getConnectorType().getAuthEndpoint(), clientId, clientSecret, username, password);	
				
				status = loginResult.getStatus();
			}
			
			if (! getConnector().getConnectorType().getScheme().equals(getConnector().getConnectorType().getScheme())) {
				throw new IllegalArgumentException(String.format(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_TYPE_SCHEME_MISMATCH), 
						getConnector().getConnectorType().getScheme(), 
						getConnector().getConnectorType().getScheme()));
			}
			
			Connector connector = Connector.builder()
					.from(getConnector())
					.name(name)
					.connectorType(isNotNull(getConnectorType()) ? getConnectorType() : getConnector().getConnectorType())
					.lastUpdatedBy(UserInfo.of(ClaimsContext.getClaims()))
					.lastUpdatedOn(Date.from(Instant.now()))
					.username(username)
					.password(password)
					.clientId(clientId)
					.clientSecret(clientSecret)
					.connectedAs(loginResult.getIsConnected() ? loginResult.getToken().getId() : null)
					.connectedOn(loginResult.getIsConnected() ? new Date(Long.valueOf(loginResult.getToken().getIssuedAt())) : null)
					.status(status)
					.isConnected(loginResult.getIsConnected())
					.build();
			
			return connector;
		}
	}
	
	private SalesforceLoginResult login(String authEndpoint, String clientId, String clientSecret, String username, String password) {
		HttpResponse httpResponse = RestResource.post(String.format(OAUTH_TOKEN_URI, authEndpoint))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.acceptCharset(StandardCharsets.UTF_8)
				.parameter(OauthConstants.GRANT_TYPE_PARAMETER, OauthConstants.PASSWORD_GRANT_TYPE)
				.parameter(OauthConstants.CLIENT_ID_PARAMETER, clientId)
				.parameter(OauthConstants.CLIENT_SECRET_PARAMETER, clientSecret)
				.parameter(OauthConstants.USERNAME_PARAMETER, username)
				.parameter(OauthConstants.PASSWORD_PARAMETER, password)
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