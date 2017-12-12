package com.nowellpoint.api.service;

import com.nowellpoint.api.rest.domain.Connector;
import com.nowellpoint.api.rest.domain.ConnectorRequest;

public interface ConnectorService {
	
	public Connector findById(String id);
	
	public Connector createConnector(ConnectorRequest request);
	
	public Connector updateConnector(String id, ConnectorRequest request);
	
	public void deleteConnector(String id);
	
}