package com.nowellpoint.api.model.mapper;

import java.util.HashSet;
import java.util.Set;

import org.modelmapper.TypeToken;

import com.nowellpoint.api.model.dto.Id;
import com.nowellpoint.api.model.dto.SalesforceConnector;

/**
 * 
 * 
 * @author jherson
 *
 * 
 */

public class SalesforceConnectorModelMapper extends AbstractModelMapper<com.nowellpoint.api.model.document.SalesforceConnector> {
	
	/**
	 * 
	 * 
	 * constructor
	 * 
	 * 
	 */
	
	public SalesforceConnectorModelMapper() {
		super(com.nowellpoint.api.model.document.SalesforceConnector.class);
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @return
	 * 
	 * 
	 */
	
	public SalesforceConnector findSalesforceConnector(Id id) {
		com.nowellpoint.api.model.document.SalesforceConnector document = findById(id.toString());
		return modelMapper.map(document, SalesforceConnector.class);
	}
	
	/**
	 * 
	 * 
	 * @return
	 * 
	 * 
	 */
	
	public Set<SalesforceConnector> findAllByOwner() {
		Set<com.nowellpoint.api.model.document.SalesforceConnector> documents = findAllByOwner(getSubject());
		Set<SalesforceConnector> resources = modelMapper.map(documents, new TypeToken<HashSet<SalesforceConnector>>() {}.getType());
		return resources;
	}
	
	/**
	 * 
	 * 
	 * @param salesforceConnector
	 * 
	 * 
	 */
	
	public void createSalesforceConnector(SalesforceConnector salesforceConnector) {
		com.nowellpoint.api.model.document.SalesforceConnector document = modelMapper.map(salesforceConnector, com.nowellpoint.api.model.document.SalesforceConnector.class);
		create(getSubject(), document);
		modelMapper.map(document, salesforceConnector);
	}
	
	/**
	 * 
	 * 
	 * @param salesforceConnector
	 * 
	 * 
	 */
	
	public void updateSalesforceConnector(SalesforceConnector salesforceConnector) {
		com.nowellpoint.api.model.document.SalesforceConnector document = modelMapper.map(salesforceConnector, com.nowellpoint.api.model.document.SalesforceConnector.class);
		replace(getSubject(), document);
		modelMapper.map(document, salesforceConnector);
	}
	
	/**
	 * 
	 * 
	 * @param salesforceConnector
	 * 
	 * 
	 */
	
	public void deleteSalesforceConnector(SalesforceConnector salesforceConnector) {
		com.nowellpoint.api.model.document.SalesforceConnector document = modelMapper.map(salesforceConnector, com.nowellpoint.api.model.document.SalesforceConnector.class);
		delete(getSubject(), document);
	}
}