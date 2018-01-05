package com.nowellpoint.api.service.impl;

import com.nowellpoint.api.rest.domain.Connector;
import com.nowellpoint.api.rest.domain.ConnectorList;
import com.nowellpoint.api.rest.domain.ConnectorRequest;
import com.nowellpoint.api.service.ConnectorService;
import com.nowellpoint.api.util.ClaimsContext;

public class ConnectorServiceImpl extends AbstractConnectorService implements ConnectorService {
	
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
		
		Connector connector = build(request);
		
		create(connector);
		
		return connector;
	}

	@Override
	public Connector updateConnector(String id, ConnectorRequest request) {
		
		Connector original = retrieve(id);
		
		Connector connector = build(original, request);
		
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
		
		Connector connector = refresh(original);
		
		update(connector);
		
		return connector;
	}
	
	@Override
	public Connector disconnect(String id) {
		
		Connector original = findById(id);
		
		Connector connector = disconnect(original);
		
		update(connector);
		
		return connector;
	}
}