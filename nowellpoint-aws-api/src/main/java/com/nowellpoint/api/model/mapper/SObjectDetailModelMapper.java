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
	 * @param id
	 * @return
	 */
	
	protected SObjectDetail findSObjectDetail(String id) {
		com.nowellpoint.api.model.document.SObjectDetail document = findById(id);
		return modelMapper.map(document, SObjectDetail.class);
	}	
	
	/**
	 * 
	 * @param sobjectDescription
	 */
	
	protected void createOrUpdateSObjectDetail(SObjectDetail sobjectDescription) {
		com.nowellpoint.api.model.document.SObjectDetail document = modelMapper.map(sobjectDescription, com.nowellpoint.api.model.document.SObjectDetail.class);
		if (isNull(sobjectDescription.getId())) {
			create(document);
		} else {
			replace(document);
		}
		modelMapper.map(document, sobjectDescription);
	}
	
	/**
	 * 
	 * @param sobjectDescription
	 */
	
	protected void createSObjectDetail(SObjectDetail sobjectDescription) {
		com.nowellpoint.api.model.document.SObjectDetail document = modelMapper.map(sobjectDescription, com.nowellpoint.api.model.document.SObjectDetail.class);
		create(document);
		modelMapper.map(document, sobjectDescription);
	}
	
	/**
	 * 
	 * @param sobjectDescription
	 */
	
	protected void updateSObjectDetail(SObjectDetail sobjectDescription) {
		com.nowellpoint.api.model.document.SObjectDetail document = modelMapper.map(sobjectDescription, com.nowellpoint.api.model.document.SObjectDetail.class);
		replace(document);
		modelMapper.map(document, sobjectDescription);
	}
	
	/**
	 * 
	 * @param sobjectDescription
	 */
	
	protected void deleteSObjectDetail(SObjectDetail sobjectDescription) {
		com.nowellpoint.api.model.document.SObjectDetail document = modelMapper.map(sobjectDescription, com.nowellpoint.api.model.document.SObjectDetail.class);
		delete(document);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	
	protected SObjectDetail findSObjectDetailByName(String environmentKey, String name) {
		com.nowellpoint.api.model.document.SObjectDetail document = findOne( eq ( "name", name ) );
		SObjectDetail sobjectDescription = modelMapper.map(document, SObjectDetail.class);
		return sobjectDescription; 
	}
}