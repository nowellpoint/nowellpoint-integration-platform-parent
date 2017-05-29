package com.nowellpoint.api.service;

import com.nowellpoint.api.rest.domain.SalesforceConnector;
import com.nowellpoint.api.rest.domain.SalesforceConnectorList;
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
	
	SalesforceConnector updateSalesforceConnector(String id, String name, String tag, String ownerId);
	
	void deleteSalesforceConnector(SalesforceConnector salesforceConnector);
	
	SalesforceConnector findById(String id);
	
	void test(SalesforceConnector salesforceConnector);
	
	void build(SalesforceConnector salesforceConnector);
	
	void metadataBackup(SalesforceConnector salesforceConnector);
}