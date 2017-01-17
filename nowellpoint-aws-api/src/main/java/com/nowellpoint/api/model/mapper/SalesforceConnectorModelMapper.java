package com.nowellpoint.api.model.mapper;

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
		com.nowellpoint.api.model.document.SalesforceConnector document = fetch(id);
		return modelMapper.map(document, SalesforceConnector.class);
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