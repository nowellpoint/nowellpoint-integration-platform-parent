package com.nowellpoint.api.document.service;

import java.util.Set;

import org.modelmapper.TypeToken;

import com.nowellpoint.api.dto.Id;
import com.nowellpoint.api.dto.SalesforceConnectorDTO;
import com.nowellpoint.api.model.SalesforceConnector;
import com.nowellpoint.api.service.AbstractModelMapper;

/**************************************************************************************************************************
 * 
 * 
 * @author jherson
 *
 * 
 *************************************************************************************************************************/

public class SalesforceConnectorDocumentService extends AbstractModelMapper<SalesforceConnector> {
	
	/**************************************************************************************************************************
	 * 
	 * 
	 * constructor
	 * 
	 * 
	 *************************************************************************************************************************/
	
	public SalesforceConnectorDocumentService() {
		super(SalesforceConnector.class);
	}
	
	public SalesforceConnectorDTO findSalesforceConnector(Id id) {
		SalesforceConnector document = findById(id.getValue());
		return modelMapper.map(document, SalesforceConnectorDTO.class);
	}
	
	public Set<SalesforceConnectorDTO> findAllByOwner() {
		Set<SalesforceConnector> documents = findAllByOwner(getSubject());
		Set<SalesforceConnectorDTO> resources = modelMapper.map(documents, new TypeToken<Set<SalesforceConnectorDTO>>() {}.getType());
		return resources;
	}
	
	public void createSalesforceConnector(SalesforceConnectorDTO salesforceConnector) {
		SalesforceConnector document = modelMapper.map(salesforceConnector, SalesforceConnector.class);
		create(getSubject(), document);
		modelMapper.map(document, salesforceConnector);
	}
	
	public void updateSalesforceConnector(SalesforceConnectorDTO salesforceConnector) {
		SalesforceConnector document = modelMapper.map(salesforceConnector, SalesforceConnector.class);
		replace(getSubject(), document);
		modelMapper.map(document, salesforceConnector);
	}
	
	public void deleteSalesforceConnector(SalesforceConnectorDTO salesforceConnector) {
		SalesforceConnector document = modelMapper.map(salesforceConnector, SalesforceConnector.class);
		delete(document);
	}
}