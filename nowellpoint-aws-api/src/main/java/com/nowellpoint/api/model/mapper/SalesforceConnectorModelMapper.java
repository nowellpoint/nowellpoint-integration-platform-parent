package com.nowellpoint.api.model.mapper;

import java.util.HashSet;
import java.util.Set;

import org.modelmapper.TypeToken;

import com.nowellpoint.api.model.domain.SalesforceConnector;

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
	 * @param id the primary key for the SalesforceConnector
	 * @return SalesforceConnector for id
	 * 
	 * 
	 */
	
	protected SalesforceConnector findSalesforceConnector(String id) {
		com.nowellpoint.api.model.document.SalesforceConnector document = findById(id);
		return modelMapper.map(document, SalesforceConnector.class);
	}
	
	/**
	 * 
	 * 
	 * @return set of SalesforceConnectors found for current owner based on logged in user
	 * 
	 * 
	 */
	
	protected Set<SalesforceConnector> findAllByOwner() {
		Set<com.nowellpoint.api.model.document.SalesforceConnector> documents = findAllByOwner(getSubject());
		Set<SalesforceConnector> resources = modelMapper.map(documents, new TypeToken<HashSet<SalesforceConnector>>() {}.getType());
		return resources;
	}
	
	/**
	 * 
	 * 
	 * @param salesforceConnector the SalesforceConnector to be created
	 * 
	 * 
	 */
	
	protected void createSalesforceConnector(SalesforceConnector salesforceConnector) {
		com.nowellpoint.api.model.document.SalesforceConnector document = modelMapper.map(salesforceConnector, com.nowellpoint.api.model.document.SalesforceConnector.class);
		create(document);
		hset(encode(getSubject()), document);
		modelMapper.map(document, salesforceConnector);
	}
	
	/**
	 * 
	 * 
	 * @param salesforceConnector the SalesforceConnector to be updated
	 * 
	 * 
	 */
	
	protected void updateSalesforceConnector(SalesforceConnector salesforceConnector) {
		com.nowellpoint.api.model.document.SalesforceConnector document = modelMapper.map(salesforceConnector, com.nowellpoint.api.model.document.SalesforceConnector.class);
		replace(document);
		hset(encode(getSubject()), document);
		modelMapper.map(document, salesforceConnector);
	}
	
	/**
	 * 
	 * 
	 * @param salesforceConnector the SalesforceConnector to be deleted
	 * 
	 * 
	 */
	
	protected void deleteSalesforceConnector(SalesforceConnector salesforceConnector) {
		com.nowellpoint.api.model.document.SalesforceConnector document = modelMapper.map(salesforceConnector, com.nowellpoint.api.model.document.SalesforceConnector.class);
		delete(document);
		hdel(encode(getSubject()), document);
	}
}