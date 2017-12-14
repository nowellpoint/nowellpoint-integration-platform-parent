package com.nowellpoint.api.service;

import com.nowellpoint.api.rest.domain.Connector;
import com.nowellpoint.api.rest.domain.ConnectorList;
import com.nowellpoint.api.rest.domain.ConnectorRequest;

public interface ConnectorService {
	
	public ConnectorList getConnectors();
	
	public Connector findById(String id);
	
	public Connector createConnector(ConnectorRequest request);
	
	public Connector updateConnector(String id, ConnectorRequest request);
	
	public void deleteConnector(String id);
	
	public Connector disconnect(String id);
	
	public Connector refresh(String id);
	
}