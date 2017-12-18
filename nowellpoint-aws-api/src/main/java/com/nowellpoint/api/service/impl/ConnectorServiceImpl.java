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
import com.nowellpoint.api.rest.domain.ConnectorWrapper;
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
		
		Connector connector = buildTypeWrapper(null, type, request);
		
		create(connector);
		
		return connector;
	}

	@Override
	public Connector updateConnector(String id, ConnectorRequest request) {
		
		Connector original = retrieve(id);
		
		ConnectorType type = getConnectorType(original.getType());
		
		Connector connector = buildTypeWrapper(original, type, request);
		
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
		
		if (Assert.isNull(original.getCredentialsKey())) {
			throw new IllegalArgumentException("Unable to Refresh Connector. Conecctor is missing credentials. Please update the connector with valid credentials");
		}
		
		ConnectorType type = getConnectorType(original.getType());
		
		ConnectorRequest request = ConnectorRequest.builder().build();
		
		Connector connector = buildTypeWrapper(original, type, request);
		
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
	
	private Connector buildTypeWrapper(Connector connector, ConnectorType type, ConnectorRequest request) {
		
		if ("SALESFORCE_SANDBOX".equals(request.getType()) || "SALESFORCE_PRODUCTION".equals(request.getType())) {
			
			if (Assert.isNull(connector)) {
				
				ConnectorWrapper wrapper = ConnectorWrapper.builder()
						.request(request)
						.type(type)
						.build();
				
				return wrapper.toConnector();
				
			} else {
				
				ConnectorWrapper wrapper = ConnectorWrapper.builder()
						.connector(connector)
						.request(request)
						.type(type)
						.build();
				
				return wrapper.toConnector();
			}
		}	
		
		return null;
	}
	
	private ConnectorType getConnectorType(String type) {
		return connectorTypes.get(type);
	}
}