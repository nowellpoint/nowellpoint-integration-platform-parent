package com.nowellpoint.api.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.nowellpoint.api.model.dynamodb.VaultEntry;
import com.nowellpoint.api.rest.domain.Connector;
import com.nowellpoint.api.rest.domain.ConnectorRequest;
import com.nowellpoint.api.rest.domain.ConnectorType;
import com.nowellpoint.api.rest.domain.OrganizationInfo;
import com.nowellpoint.api.rest.domain.SalesforceCredentials;
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
	
	private static Map<String,ConnectorType> connectorTypes = new HashMap<String,ConnectorType>();
	
	public ConnectorServiceImpl() {
		
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
		
		connectorTypes = connectorTypeList.stream().collect(Collectors.toMap(t -> t.getName(), t -> t));
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
		
		UserInfo who = UserInfo.of(ClaimsContext.getClaims().getBody().getSubject());
		
		OrganizationInfo owner = OrganizationInfo.of(ClaimsContext.getClaims().getBody().getAudience());
		
		Date now = Date.from(Instant.now());
		
		String connectionStatus = testConnection(type, request);
		
		SalesforceCredentials credentials = SalesforceCredentials.builder()
				.clientId(request.getClientId())
				.clientSecret(request.getClientSecret())
				.username(request.getUsername())
				.password(request.getPassword())
				.build();
		
		VaultEntry vaultEntry = vaultEntryService.store(credentials.asString());
		
		Connector connector = Connector.builder()
				.createdBy(who)
				.createdOn(now)
				.lastUpdatedBy(who)
				.lastUpdatedOn(now)
				.name(request.getName())
				.authEndpoint(type.getAuthEndpoint())
				.grantType(type.getGrantType())
				.type(type.getName())
				.iconHref(type.getIconHref())
				.typeName(type.getDisplayName())
				.credentialsKey(vaultEntry.getToken())
				.owner(owner)
				.connectionDate(now)
				.connectionStatus(connectionStatus)
				.build();
		
		create(connector);
		
		return connector;
	}

	@Override
	public Connector updateConnector(String id, ConnectorRequest request) {
		
		Connector original = retrieve(id);
		
		UserInfo who = UserInfo.of(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
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
				.lastUpdatedBy(who)
				.lastUpdatedOn(now)
				.name(request.getName())
				.connectionDate(now)
				.connectionStatus(connectionStatus)
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
	
	/**
	 * 
	 * 
	 * 
	 */
	
	private ConnectorType getConnectorType(String type) {
		return connectorTypes.get(type);
	}
	
	private String testConnection(ConnectorType type, ConnectorRequest request) {
		String status = null;
		
		if ("SALESFORCE_SANDBOX".equals(type.getName()) || "SALESFORCE_PRODUCTION".equals(type.getName())) {
			try {
				login(type.getAuthEndpoint(), request.getClientId(), request.getClientSecret(), request.getUsername(), request.getPassword());
				status = "Connected";
			} catch (OauthException e) {
				status = String.format("Failed. %s: %s", e.getError(), e.getErrorDescription());
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