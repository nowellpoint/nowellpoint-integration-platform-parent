package com.nowellpoint.api.service.impl;

import java.time.Instant;
import java.util.Date;

import com.nowellpoint.api.rest.domain.Connector;
import com.nowellpoint.api.rest.domain.ConnectorList;
import com.nowellpoint.api.rest.domain.ConnectorRequest;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.service.ConnectorService;
import com.nowellpoint.api.util.ClaimsContext;
import com.nowellpoint.api.util.UserContext;

public class ConnectorServiceImpl extends AbstractConnectorService implements ConnectorService {
	
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
		
		Connector connector = buildConnector(request);
		
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
		
		delete(connector);
	}
	
	@Override
	public Connector refresh(String id) {
		
		Connector original = findById(id);
		
		if (! original.getIsConnected()) {
			throw new IllegalArgumentException("Connector has been disconnected. Unable to refresh the connector. Please update the connector with valid credentials");
		}
		
		Connector connector = refreshConnector(original);
		
		update(connector);
		
		return connector;
	}
	
	@Override
	public Connector disconnect(String id) {
		
		Connector original = findById(id);
		
		UserInfo who = UserInfo.of(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
		Connector connector = Connector.builder()
				.from(original)
				.lastUpdatedBy(who)
				.lastUpdatedOn(now)
				.connectedOn(null)
				.username(null)
				.password(null)
				.clientId(null)
				.clientSecret(null)
				.status(DISCONNECTED)
				.isConnected(Boolean.FALSE)
				.build();
		
		update(connector);
		
		return connector;
	}
}