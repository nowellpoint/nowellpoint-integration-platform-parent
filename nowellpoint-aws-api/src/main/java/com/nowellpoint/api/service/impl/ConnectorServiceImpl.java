package com.nowellpoint.api.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.nowellpoint.api.rest.domain.Connector;
import com.nowellpoint.api.rest.domain.ConnectorList;
import com.nowellpoint.api.rest.domain.ConnectorRequest;
import com.nowellpoint.api.rest.domain.ConnectorType;
import com.nowellpoint.api.rest.domain.SalesforceConnectorWrapper;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.service.ConnectorService;
import com.nowellpoint.api.service.VaultEntryService;
import com.nowellpoint.api.util.ClaimsContext;
import com.nowellpoint.api.util.UserContext;
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
	
	private static final String DISCONNECTED = "Disconnected";
	
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
		
		if (Assert.isNull(type)) {
			throw new IllegalArgumentException(String.format("Invalid Connector Type: %s", request.getType()));
		}
		
		Connector connector = buildConnector(type, request);
		
		create(connector);
		
		return connector;
	}

	@Override
	public Connector updateConnector(String id, ConnectorRequest request) {
		
		Connector original = retrieve(id);
		
		Connector connector = buildConnector(original, request);
		
		update(connector);
		
		return connector;
	}
	
	@Override
	public void deleteConnector(String id) {
		Connector connector = findById(id);
		if (Assert.isNotNull(connector.getCredentialsKey())) {
			vaultEntryService.remove(connector.getCredentialsKey());
		}
		delete(connector);
	}
	
	@Override
	public Connector refresh(String id) {
		
		Connector original = findById(id);
		
		if (Assert.isNull(original.getCredentialsKey())) {
			throw new IllegalArgumentException("Unable to Refresh Connector. Conecctor is missing credentials. Please update the connector with valid credentials");
		}
		
		Connector connector = refreshConnector(original);
		
		update(connector);
		
		return connector;
	}
	
	@Override
	public Connector disconnect(String id) {
		Connector original = findById(id);
		
		if (Assert.isNotNull(original.getCredentialsKey())) {
			vaultEntryService.remove(original.getCredentialsKey());
		}
		
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
	
	private Connector refreshConnector(Connector original) {
		if ("SALESFORCE_SANDBOX".equals(original.getType()) || "SALESFORCE_PRODUCTION".equals(original.getType())) {
			SalesforceConnectorWrapper wrapper = SalesforceConnectorWrapper.of(original);
			return wrapper.toConnector();
		}
		
		return null;
		
	}
	
	private Connector buildConnector(Connector original, ConnectorRequest request) {
		if ("SALESFORCE_SANDBOX".equals(original.getType()) || "SALESFORCE_PRODUCTION".equals(original.getType())) {
			SalesforceConnectorWrapper wrapper = SalesforceConnectorWrapper.of(original, request);
			return wrapper.toConnector();
		}
		
		return null;
	}
	
	private Connector buildConnector(ConnectorType type, ConnectorRequest request) {
		if ("SALESFORCE_SANDBOX".equals(request.getType()) || "SALESFORCE_PRODUCTION".equals(request.getType())) {
			SalesforceConnectorWrapper wrapper = SalesforceConnectorWrapper.of(type, request);
			return wrapper.toConnector();
		}	
		
		return null;
	}
	
	private ConnectorType getConnectorType(String type) {
		return connectorTypes.get(type);
	}
}