package com.nowellpoint.api.model.mapper;

import java.util.HashSet;
import java.util.Set;

import org.modelmapper.TypeToken;

import com.nowellpoint.api.model.document.SalesforceConnectorDocument;
import com.nowellpoint.api.model.dto.Id;
import com.nowellpoint.api.model.dto.SalesforceConnector;

/**************************************************************************************************************************
 * 
 * 
 * @author jherson
 *
 * 
 *************************************************************************************************************************/

public class SalesforceConnectorModelMapper extends AbstractModelMapper<SalesforceConnectorDocument> {
	
	/**************************************************************************************************************************
	 * 
	 * 
	 * constructor
	 * 
	 * 
	 *************************************************************************************************************************/
	
	public SalesforceConnectorModelMapper() {
		super(SalesforceConnectorDocument.class);
	}
	
	public SalesforceConnector findSalesforceConnector(Id id) {
		SalesforceConnectorDocument document = findById(id.getValue());
		return modelMapper.map(document, SalesforceConnector.class);
	}
	
	public Set<SalesforceConnector> findAllByOwner() {
		Set<SalesforceConnectorDocument> documents = findAllByOwner(getSubject());
		Set<SalesforceConnector> resources = modelMapper.map(documents, new TypeToken<HashSet<SalesforceConnector>>() {}.getType());
		return resources;
	}
	
	public void createSalesforceConnector(SalesforceConnector salesforceConnector) {
		SalesforceConnectorDocument document = modelMapper.map(salesforceConnector, SalesforceConnectorDocument.class);
		create(getSubject(), document);
		modelMapper.map(document, salesforceConnector);
	}
	
	public void updateSalesforceConnector(SalesforceConnector salesforceConnector) {
		SalesforceConnectorDocument document = modelMapper.map(salesforceConnector, SalesforceConnectorDocument.class);
		replace(getSubject(), document);
		modelMapper.map(document, salesforceConnector);
	}
	
	public void deleteSalesforceConnector(SalesforceConnector salesforceConnector) {
		SalesforceConnectorDocument document = modelMapper.map(salesforceConnector, SalesforceConnectorDocument.class);
		delete(getSubject(), document);
	}
}