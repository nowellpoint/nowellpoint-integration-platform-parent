package com.nowellpoint.api.model.mapper;

import static com.mongodb.client.model.Filters.eq;
import static com.nowellpoint.util.Assert.isNull;

import com.nowellpoint.api.model.domain.SObjectDetail;

/**
 * 
 * 
 * @author jherson
 *
 * 
 */

public class SObjectDetailModelMapper extends AbstractModelMapper<com.nowellpoint.api.model.document.SObjectDetail> {
	
	/**
	 * 
	 */
	
	public SObjectDetailModelMapper() {
		super(com.nowellpoint.api.model.document.SObjectDetail.class);
	}
	
	/**
	 * 
	 * @param id the primary key for the SObjectDetail
	 * @return SObjectDetail for id
	 */
	
	protected SObjectDetail findSObjectDetail(String id) {
		com.nowellpoint.api.model.document.SObjectDetail document = find(id);
		return modelMapper.map(document, SObjectDetail.class);
	}	
	
	/**
	 * 
	 * @param sobjectDetail record to be created or updated
	 */
	
	protected void createOrUpdateSObjectDetail(SObjectDetail sobjectDetail) {
		com.nowellpoint.api.model.document.SObjectDetail document = modelMapper.map(sobjectDetail, com.nowellpoint.api.model.document.SObjectDetail.class);
		if (isNull(sobjectDetail.getId())) {
			create(document);
		} else {
			replace(document);
		}
		modelMapper.map(document, sobjectDetail);
	}
	
	/**
	 * 
	 * @param sobjectDetail record to be created
	 */
	
	protected void createSObjectDetail(SObjectDetail sobjectDetail) {
		com.nowellpoint.api.model.document.SObjectDetail document = modelMapper.map(sobjectDetail, com.nowellpoint.api.model.document.SObjectDetail.class);
		create(document);
		modelMapper.map(document, sobjectDetail);
	}
	
	/**
	 * 
	 * @param sobjectDetail record to be updated
	 */
	
	protected void updateSObjectDetail(SObjectDetail sobjectDetail) {
		com.nowellpoint.api.model.document.SObjectDetail document = modelMapper.map(sobjectDetail, com.nowellpoint.api.model.document.SObjectDetail.class);
		replace(document);
		modelMapper.map(document, sobjectDetail);
	}
	
	/**
	 * 
	 * @param sobjectDetail record to be deleted
	 */
	
	protected void deleteSObjectDetail(SObjectDetail sobjectDetail) {
		com.nowellpoint.api.model.document.SObjectDetail document = modelMapper.map(sobjectDetail, com.nowellpoint.api.model.document.SObjectDetail.class);
		delete(document);
	}
	
	/**
	 * 
	 * @param environmentKey Salesforce environment for SObjectDetails
	 * @param name name of the SObject to lookup
	 * @return  the SObjectDetail record that was found
	 */
	
	protected SObjectDetail findSObjectDetailByName(String environmentKey, String name) {
		com.nowellpoint.api.model.document.SObjectDetail document = findOne( eq ( "name", name ) );
		SObjectDetail sobjectDescription = modelMapper.map(document, SObjectDetail.class);
		return sobjectDescription; 
	}
}