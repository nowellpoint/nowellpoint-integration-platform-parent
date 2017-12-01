package com.nowellpoint.api.service;

import com.nowellpoint.api.rest.domain.SalesforceConnectorOrig;
import com.nowellpoint.api.rest.domain.SalesforceConnectorList;
import com.nowellpoint.api.rest.domain.UpdateSalesforceConnectorRequest;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;

/**
 * 
 * 
 * @author jherson
 *
 * 
 */

public interface SalesforceConnectorService {
	
	public SalesforceConnectorList findAllByOwner(String ownerId);
	
	public SalesforceConnectorOrig createSalesforceConnector(Token token);
	
	public SalesforceConnectorOrig updateSalesforceConnector(String id, UpdateSalesforceConnectorRequest request);
	
	public void deleteSalesforceConnector(SalesforceConnectorOrig salesforceConnectorOrig);
	
	public SalesforceConnectorOrig findById(String id);
	
	public void test(SalesforceConnectorOrig salesforceConnectorOrig);
	
	public void build(SalesforceConnectorOrig salesforceConnectorOrig);
	
	public void metadataBackup(SalesforceConnectorOrig salesforceConnectorOrig);
	
	public DescribeSobjectResult describeSobject(SalesforceConnectorOrig salesforceConnectorOrig, String sobject);
}