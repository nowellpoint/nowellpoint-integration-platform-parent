package com.nowellpoint.api.rest.domain;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.annotation.Nullable;

import org.immutables.value.Value;

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
import com.nowellpoint.util.Assert;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractConnectorWrapper {
	public abstract ConnectorType getType();
	public abstract ConnectorRequest getRequest();
	public abstract @Nullable Connector getConnector();
	
	private static final String OAUTH_TOKEN_URI = "%s/services/oauth2/token";
	private static final DynamoDBMapper dynamoDBMapper = DynamoDBMapperProvider.getDynamoDBMapper();
	
	public Token login() {
		HttpResponse httpResponse = RestResource.post(String.format(OAUTH_TOKEN_URI, getType().getAuthEndpoint()))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.acceptCharset(StandardCharsets.UTF_8)
				.parameter(OauthConstants.GRANT_TYPE_PARAMETER, OauthConstants.PASSWORD_GRANT_TYPE)
				.parameter(OauthConstants.CLIENT_ID_PARAMETER, getRequest().getClientId())
				.parameter(OauthConstants.CLIENT_SECRET_PARAMETER, getRequest().getClientSecret())
				.parameter(OauthConstants.USERNAME_PARAMETER, getRequest().getUsername())
				.parameter(OauthConstants.PASSWORD_PARAMETER, getRequest().getPassword())
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
		
		String connectionStatus = "Connected";
		Boolean isConnected = Boolean.TRUE;
		
		try {
			login();			
		} catch (OauthException e) {
			isConnected = Boolean.FALSE;
			connectionStatus = String.format("Failed to Connect. (%s + : %s )", e.getError(), e.getErrorDescription());
		}
		
		if (Assert.isNull(getConnector())) {
			
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
			
			Connector connector = Connector.builder()
					.name(getRequest().getName())
					.authEndpoint(getType().getAuthEndpoint())
					.grantType(getType().getGrantType())
					.type(getType().getName())
					.iconHref(getType().getIconHref())
					.typeName(getType().getDisplayName())
					.credentialsKey(vaultEntry.getToken())
					.connectionDate(Date.from(Instant.now()))
					.connectionStatus(connectionStatus)
					.isConnected(isConnected)
					.build();
			
			return connector;
			
		} else {
			
			VaultEntry vaultEntry = null;
			
			if (Assert.isNotNull(getConnector().getCredentialsKey())) {
				vaultEntry = dynamoDBMapper.load(VaultEntry.class, getConnector().getCredentialsKey());
				
				String[] values = vaultEntry.getValue().split(":");
				
				ConnectorRequest connectorRequest = ConnectorRequest.builder()
						.clientId(values[0])
						.clientSecret(values[1])
						.username(values[2])
						.password(values[3])
						.build();
				
				String connectString = new StringBuilder()
						.append(Assert.isNotNullOrEmpty(getRequest().getClientId()) ? getRequest().getClientId() : connectorRequest.getClientId())
						.append(":")
						.append(Assert.isNotNullOrEmpty(getRequest().getClientSecret()) ? getRequest().getClientSecret() : connectorRequest.getClientSecret())
						.append(":")
						.append(Assert.isNotNullOrEmpty(getRequest().getUsername()) ? getRequest().getUsername() : connectorRequest.getUsername())
						.append(":")
						.append(Assert.isNotNullOrEmpty(getRequest().getPassword()) ? getRequest().getPassword() : connectorRequest.getPassword())
						.toString();
				
				vaultEntry.setValue(connectString);
				
			} else {
				
				String connectString = new StringBuilder()
						.append(getRequest().getClientId())
						.append(":")
						.append(getRequest().getClientSecret())
						.append(":")
						.append(getRequest().getUsername())
						.append(":")
						.append(getRequest().getPassword())
						.toString();
				
				vaultEntry = new VaultEntry();
				vaultEntry.setValue(connectString);
			}
			
			dynamoDBMapper.save(vaultEntry);
			
			Connector connector = Connector.builder()
					.from(getConnector())
					.lastUpdatedBy(UserInfo.of(ClaimsContext.getClaims()))
					.lastUpdatedOn(Date.from(Instant.now()))
					.name(getRequest().getName())
					.authEndpoint(getType().getAuthEndpoint())
					.grantType(getType().getGrantType())
					.type(getType().getName())
					.iconHref(getType().getIconHref())
					.typeName(getType().getDisplayName())
					.credentialsKey(vaultEntry.getToken())
					.connectionDate(Date.from(Instant.now()))
					.connectionStatus(connectionStatus)
					.isConnected(isConnected)
					.build();
			
			return connector;
		}		
	}
}