package com.nowellpoint.api.service;

import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import com.nowellpoint.api.model.domain.Instance;
import com.nowellpoint.api.model.domain.SalesforceConnector;
import com.nowellpoint.api.model.domain.SalesforceConnectorList;
import com.nowellpoint.client.sforce.model.Token;

/**
 * 
 * 
 * @author jherson
 *
 * 
 */

public interface SalesforceConnectorService {
	
	SalesforceConnectorList findAllByOwner(String ownerId);
	
	SalesforceConnector createSalesforceConnector(Token token);
	
	void updateSalesforceConnector(String id, SalesforceConnector salesforceConnector);
	
	void deleteSalesforceConnector(String id);
	
	SalesforceConnector findById(String id);
	
	Set<Instance> getInstances(String id);
	
	Instance getInstance(String id, String key);
	
	void addInstance(String id, Instance instance);
	
	void updateInstance(String id, String key, Instance instance);
	
	void updateInstance(SalesforceConnector resource, Instance instance);
	
	Instance updateInstance(String id, String key, MultivaluedMap<String, String> parameters);
	
	Instance testConnection(String id, String key);
	
	Instance buildEnvironment(String id, String key);
	
	void removeInstance(String id, String key);
}