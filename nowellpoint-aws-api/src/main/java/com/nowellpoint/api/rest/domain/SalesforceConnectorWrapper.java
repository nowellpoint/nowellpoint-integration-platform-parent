package com.nowellpoint.api.rest.domain;

import static com.nowellpoint.util.Assert.isNull;
import static com.nowellpoint.util.Assert.isNotNull;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.nowellpoint.api.model.dynamodb.VaultEntry;
import com.nowellpoint.api.util.ClaimsContext;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;
import com.nowellpoint.client.sforce.OauthConstants;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.model.Error;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

public class SalesforceConnectorWrapper {
	
	private static final String OAUTH_TOKEN_URI = "%s/services/oauth2/token";
	private static final DynamoDBMapper dynamoDBMapper = DynamoDBMapperProvider.getDynamoDBMapper();
	
	private ConnectorType type;
	private ConnectorRequest request;
	private Connector connector;
	
	private ConnectorType getType() {
		return type;
	}
	
	private ConnectorRequest getRequest() {
		return request;
	}
	
	private Connector getConnector() {
		return connector;
	}
	
	public static SalesforceConnectorWrapper of(Connector connector) {
		ConnectorRequest request = ConnectorRequest.builder()
				.name(connector.getName())
				.build();
		
		return of(connector, request);
	}
	
	public static SalesforceConnectorWrapper of(Connector connector, ConnectorRequest request) {
		ConnectorType type = ConnectorType.builder()
				.authEndpoint(connector.getAuthEndpoint())
				.displayName(connector.getTypeName())
				.grantType(connector.getGrantType())
				.iconHref(connector.getIconHref())
				.name(connector.getType())
				.build();
		
		return new SalesforceConnectorWrapper(connector, type, request);
	}
	
	public static SalesforceConnectorWrapper of(ConnectorType type, ConnectorRequest request) {
		return new SalesforceConnectorWrapper(type, request);
	}
	
	private SalesforceConnectorWrapper (Connector connector, ConnectorType type, ConnectorRequest request) {
		this.connector = connector;
		this.type = type;
		this.request = request;
	}
	
	private SalesforceConnectorWrapper (ConnectorType type, ConnectorRequest request) {
		this.type = type;
		this.request = request;
	}
	
	private Token login(String clientId, String clientSecret, String username, String password) {
		HttpResponse httpResponse = RestResource.post(String.format(OAUTH_TOKEN_URI, getType().getAuthEndpoint()))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.acceptCharset(StandardCharsets.UTF_8)
				.parameter(OauthConstants.GRANT_TYPE_PARAMETER, OauthConstants.PASSWORD_GRANT_TYPE)
				.parameter(OauthConstants.CLIENT_ID_PARAMETER, clientId)
				.parameter(OauthConstants.CLIENT_SECRET_PARAMETER, clientSecret)
				.parameter(OauthConstants.USERNAME_PARAMETER, username)
				.parameter(OauthConstants.PASSWORD_PARAMETER, password)
				.execute();
		
		Token token = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			token = httpResponse.getEntity(Token.class);
		} else {
			throw new OauthException(httpResponse.getStatusCode(), httpResponse.getEntity(Error.class));
		}
		
		return token;
	}
	
	public Connector toConnector() {
		
		String status = "Connected";
		Boolean isConnected = Boolean.TRUE;
		
		Token token = null;
		
		if (isNull(getConnector())) {
			
			String connectString = new StringBuilder()
					.append(getRequest().getClientId())
					.append(":")
					.append(getRequest().getClientSecret())
					.append(":")
					.append(getRequest().getUsername())
					.append(":")
					.append(getRequest().getPassword())
					.toString();
			
			VaultEntry vaultEntry = VaultEntry.of(connectString);
			dynamoDBMapper.save(vaultEntry);
			
			try {
				token = login(connectString);			
			} catch (OauthException e) {
				isConnected = Boolean.FALSE;
				status = String.format("Failed to Connect. Error: %s [ %s ]", e.getError(), e.getErrorDescription());
			}
			
			Connector connector = Connector.builder()
					.name(getRequest().getName())
					.authEndpoint(getType().getAuthEndpoint())
					.grantType(getType().getGrantType())
					.type(getType().getName())
					.iconHref(getType().getIconHref())
					.typeName(getType().getDisplayName())
					.username(isConnected ? getRequest().getUsername() : null)
					.clientId(isConnected ? getRequest().getClientId() : null)
					.credentialsKey(vaultEntry.getToken())
					.connectedAs(isConnected ? getRequest().getUsername() : null)
					.connectedOn(isConnected ? new Date(Long.valueOf(token.getIssuedAt())) : null)
					.status(status)
					.isConnected(isConnected)
					.build();
			
			return connector;
			
		} else {
			
			VaultEntry vaultEntry = null;
			
			if (isNotNull(getConnector().getCredentialsKey())) {
				
				vaultEntry = dynamoDBMapper.load(VaultEntry.class, getConnector().getCredentialsKey());
				
				String[] values = vaultEntry.getValue().split(":");
				
				ConnectorRequest request = ConnectorRequest.builder()
						.name(isNotNull(getRequest().getName()) ? getRequest().getName() : getConnector().getName())
						.clientId(isNotNull(getRequest().getClientId()) ? getRequest().getClientId() : values[0])
						.clientSecret(isNotNull(getRequest().getClientSecret()) ? getRequest().getClientSecret() : values[1])
						.username(isNotNull(getRequest().getUsername()) ? getRequest().getUsername() : values[2])
						.password(isNotNull(getRequest().getPassword()) ? getRequest().getPassword() : values[3])
						.build();
				
				this.request = request;	
				
			} else {
				vaultEntry = new VaultEntry();
			}
			
			String connectString = new StringBuilder()
					.append(getRequest().getClientId())
					.append(":")
					.append(getRequest().getClientSecret())
					.append(":")
					.append(getRequest().getUsername())
					.append(":")
					.append(getRequest().getPassword())
					.toString();
			
			vaultEntry.setValue(connectString);
			
			dynamoDBMapper.save(vaultEntry);
			
			try {
				token = login(connectString);			
			} catch (OauthException e) {
				isConnected = Boolean.FALSE;
				status = String.format("Failed to Connect. Error: %s - %s )", e.getError(), e.getErrorDescription());
			}
			
			Connector connector = Connector.builder()
					.from(getConnector())
					.name(getRequest().getName())
					.lastUpdatedBy(UserInfo.of(ClaimsContext.getClaims()))
					.lastUpdatedOn(Date.from(Instant.now()))
					.authEndpoint(getType().getAuthEndpoint())
					.grantType(getType().getGrantType())
					.type(getType().getName())
					.iconHref(getType().getIconHref())
					.typeName(getType().getDisplayName())
					.username(isConnected ? getRequest().getUsername() : null)
					.clientId(isConnected ? getRequest().getClientId() : null)
					.credentialsKey(vaultEntry.getToken())
					.connectedAs(isConnected ? getRequest().getUsername() : null)
					.connectedOn(isConnected ? new Date(Long.valueOf(token.getIssuedAt())) : null)
					.status(status)
					.isConnected(isConnected)
					.build();
			
			return connector;
		}		
	}
	
	private Token login(String connectString) {
		String[] values = connectString.split(":");
		return login(values[0], values[1], values[2], values[3]);
	}
}