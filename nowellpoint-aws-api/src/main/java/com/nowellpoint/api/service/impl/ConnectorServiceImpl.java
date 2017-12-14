package com.nowellpoint.api.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.nowellpoint.api.model.dynamodb.VaultEntry;
import com.nowellpoint.api.rest.domain.Connector;
import com.nowellpoint.api.rest.domain.ConnectorList;
import com.nowellpoint.api.rest.domain.ConnectorRequest;
import com.nowellpoint.api.rest.domain.ConnectorType;
import com.nowellpoint.api.rest.domain.OrganizationInfo;
import com.nowellpoint.api.rest.domain.SalesforceCredentials;
import com.nowellpoint.api.rest.domain.TestConnectorRequest;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.service.ConnectorService;
import com.nowellpoint.api.service.VaultEntryService;
import com.nowellpoint.api.util.ClaimsContext;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.UsernamePasswordGrantRequest;
import com.nowellpoint.util.Assert;

public class ConnectorServiceImpl extends AbstractConnectorService implements ConnectorService {
	
	@Inject
	private VaultEntryService vaultEntryService;
	
	private static final Map<String,ConnectorType> connectorTypes = createTypeMap();

	private static Map<String,ConnectorType> createTypeMap() {
		
		List<ConnectorType> connectorTypeList = new ArrayList<ConnectorType>();
		
		connectorTypeList.add(ConnectorType.builder()
				.name("SALESFORCE_SANDBOX")
				.grantType("password")
				.displayName("Salesforce Sandbox")
				.authEndpoint("https://test.salesforce.com")
				.iconHref("https://d3iep6okqojnln.cloudfront.net/salesforce-logo.jpg")
				.build());
		
		connectorTypeList.add(ConnectorType.builder()
				.name("SALESFORCE_PRODUCTION")
				.grantType("password")
				.displayName("Salesforce Production")
				.authEndpoint("https://login.salesforce.com")
				.iconHref("https://d3iep6okqojnln.cloudfront.net/salesforce-logo.jpg")
				.build());
		
		return Collections.unmodifiableMap(connectorTypeList.stream().collect(Collectors.toMap(t -> t.getName(), t -> t)));
	}
	
	private static final String CONNECTED = "Connected";
	private static final String DISCONNECTED = "Disconnected";
	private static final String FAILED_TO_CONNECT = "Failed to connect";
	
	@Override
	public ConnectorList getConnectors() {
		ConnectorList list = findAllByOwner(ClaimsContext.getClaims().getBody().getAudience());
		return list;
	}

	@Override
	public Connector findById(String id) {
		return retrieve(id);
	}

	@Override
	public Connector createConnector(ConnectorRequest request) {
		
		ConnectorType type = getConnectorType(request.getType());
		
		if (type == null) {
			throw new IllegalArgumentException(String.format("Invalid Connector Type: %s", request.getType()));
		}
		
		//UserInfo who = UserInfo.of(ClaimsContext.getClaims());
		
		//OrganizationInfo owner = OrganizationInfo.of(ClaimsContext.getClaims());
		
		//Date now = Date.from(Instant.now());
		
		String connectionStatus = testConnection(type, request);
		
		SalesforceCredentials credentials = SalesforceCredentials.builder()
				.clientId(request.getClientId())
				.clientSecret(request.getClientSecret())
				.username(request.getUsername())
				.password(request.getPassword())
				.build();
		
		VaultEntry vaultEntry = vaultEntryService.store(credentials.asString());
		
		Connector connector = Connector.builder()
				//.createdBy(who)
				//.createdOn(now)
				//.lastUpdatedBy(who)
				//.lastUpdatedOn(now)
				.name(request.getName())
				.authEndpoint(type.getAuthEndpoint())
				.grantType(type.getGrantType())
				.type(type.getName())
				.iconHref(type.getIconHref())
				.typeName(type.getDisplayName())
				.credentialsKey(vaultEntry.getToken())
				//.owner(owner)
				.connectionDate(Date.from(Instant.now()))
				.connectionStatus(connectionStatus)
				.isConnected(isConnected(connectionStatus))
				.build();
		
		create(connector);
		
		return connector;
	}

	@Override
	public Connector updateConnector(String id, ConnectorRequest request) {
		
		Connector original = retrieve(id);
		
		ConnectorType type = getConnectorType(original.getType());
		
		String connectionStatus = testConnection(type, request);
		
		VaultEntry vaultEntry = vaultEntryService.retrive(original.getCredentialsKey());
		
		SalesforceCredentials storedCredentials = SalesforceCredentials.of(vaultEntry.getValue());
		
		SalesforceCredentials credentials = SalesforceCredentials.builder()
				.clientId(Assert.isNotNullOrEmpty(request.getClientId()) ? request.getClientId() : storedCredentials.getClientId())
				.clientSecret(Assert.isNotNullOrEmpty(request.getClientSecret()) ? request.getClientSecret() : storedCredentials.getClientSecret())
				.username(Assert.isNotNullOrEmpty(request.getUsername()) ? request.getUsername() : storedCredentials.getUsername())
				.password(Assert.isNotNullOrEmpty(request.getPassword()) ? request.getPassword() : storedCredentials.getPassword())
				.build();
		
		vaultEntryService.replace(original.getCredentialsKey(), credentials.asString());
		
		Connector connector = Connector.builder()
				.from(original)
				.lastUpdatedBy(UserInfo.of(ClaimsContext.getClaims()))
				.lastUpdatedOn(Date.from(Instant.now()))
				.name(request.getName())
				.connectionDate(Date.from(Instant.now()))
				.connectionStatus(connectionStatus)
				.isConnected(isConnected(connectionStatus))
				.build();
		
		update(connector);
		
		return connector;
	}
	
	@Override
	public void deleteConnector(String id) {
		Connector connector = findById(id);
		vaultEntryService.remove(connector.getCredentialsKey());
		delete(connector);
	}
	
	@Override
	public Connector refresh(String id) {
		Connector original = findById(id);
		
		VaultEntry vaultEntry = vaultEntryService.retrive(original.getCredentialsKey());
		
		SalesforceCredentials storedCredentials = SalesforceCredentials.of(vaultEntry.getValue());
		
		try {
			
			OauthAuthenticationResponse authResponse = login(
					original.getAuthEndpoint(), 
					storedCredentials.getClientId(), 
					storedCredentials.getClientSecret(), 
					storedCredentials.getUsername(), 
					storedCredentials.getPassword());
			
		} catch (OauthException e) {
			//status = String.format("%s. %s: %s", FAILED_TO_CONNECT, e.getError(), e.getErrorDescription());
		}
		
		
		
		UserInfo who = UserInfo.of(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
		Connector connector = Connector.builder()
				.from(original)
				.connectionDate(now)
				//.connectionStatus(connectionStatus)
				//.isConnected(isConnected(connectionStatus))
				.connectionStatus(DISCONNECTED)
				.build();
		
		update(connector);
		
		return connector;
	}
	
	@Override
	public Connector disconnect(String id) {
		Connector original = findById(id);
		
		vaultEntryService.remove(original.getCredentialsKey());
		
		UserInfo who = UserInfo.of(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
		Connector connector = Connector.builder()
				.from(original)
				.lastUpdatedBy(who)
				.lastUpdatedOn(now)
				.connectionDate(null)
				.credentialsKey(null)
				.connectionStatus(DISCONNECTED)
				.isConnected(Boolean.FALSE)
				.build();
		
		update(connector);
		
		return connector;
	}
	
	/**
	 * 
	 * 
	 * 
	 */
	
	private ConnectorType getConnectorType(String type) {
		return connectorTypes.get(type);
	}
	
	private Boolean isConnected(String connectionStatus) {
		return CONNECTED.equals(connectionStatus) ? Boolean.TRUE : Boolean.FALSE;
	}
	
	private String testConnection(ConnectorType type, ConnectorRequest request) {
		String status = null;
		
		if ("SALESFORCE_SANDBOX".equals(type.getName()) || "SALESFORCE_PRODUCTION".equals(type.getName())) {
			try {
				SalesforceCredentials credentials = SalesforceCredentials.builder()
						.clientId(request.getClientId())
						.clientSecret(request.getClientSecret())
						.username(request.getUsername())
						.password(request.getPassword())
						.build();
				
				credentials.login(type.getAuthEndpoint());
				
				status = CONNECTED;
				
			} catch (OauthException e) {
				status = String.format("%s. %s: %s", FAILED_TO_CONNECT, e.getError(), e.getErrorDescription());
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