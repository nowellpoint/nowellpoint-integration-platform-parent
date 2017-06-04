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
	
	public SalesforceConnectorList findAllByOwner(String ownerId);
	
	public SalesforceConnector createSalesforceConnector(Token token);
	
	public SalesforceConnector updateSalesforceConnector(String id, String name, String tag, String ownerId);
	
	public void deleteSalesforceConnector(SalesforceConnector salesforceConnector);
	
	public SalesforceConnector findById(String id);
	
	public void test(SalesforceConnector salesforceConnector);
	
	public void build(SalesforceConnector salesforceConnector);
	
	public void metadataBackup(SalesforceConnector salesforceConnector);
}