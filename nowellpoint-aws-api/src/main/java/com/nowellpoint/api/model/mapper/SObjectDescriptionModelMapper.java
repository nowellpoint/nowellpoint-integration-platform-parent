package com.nowellpoint.api.model.mapper;

import static com.mongodb.client.model.Filters.eq;
import static com.nowellpoint.util.Assert.isNull;

import com.nowellpoint.api.model.domain.SObjectDescription;

/**
 * 
 * 
 * @author jherson
 *
 * 
 */

public class SObjectDescriptionModelMapper extends AbstractModelMapper<com.nowellpoint.api.model.document.SObjectDescription> {
	
	/**
	 * 
	 */
	
	public SObjectDescriptionModelMapper() {
		super(com.nowellpoint.api.model.document.SObjectDescription.class);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	protected SObjectDescription findSObjectDescription(String id) {
		com.nowellpoint.api.model.document.SObjectDescription document = findById(id);
		return modelMapper.map(document, SObjectDescription.class);
	}	
	
	/**
	 * 
	 * @param sobjectDescription
	 */
	
	protected void createOrUpdateSObjectDescription(SObjectDescription sobjectDescription) {
		com.nowellpoint.api.model.document.SObjectDescription document = modelMapper.map(sobjectDescription, com.nowellpoint.api.model.document.SObjectDescription.class);
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
	
	protected void createSObjectDescription(SObjectDescription sobjectDescription) {
		com.nowellpoint.api.model.document.SObjectDescription document = modelMapper.map(sobjectDescription, com.nowellpoint.api.model.document.SObjectDescription.class);
		create(document);
		modelMapper.map(document, sobjectDescription);
	}
	
	/**
	 * 
	 * @param sobjectDescription
	 */
	
	protected void updateSObjectDescription(SObjectDescription sobjectDescription) {
		com.nowellpoint.api.model.document.SObjectDescription document = modelMapper.map(sobjectDescription, com.nowellpoint.api.model.document.SObjectDescription.class);
		replace(document);
		modelMapper.map(document, sobjectDescription);
	}
	
	/**
	 * 
	 * @param sobjectDescription
	 */
	
	protected void deleteSObjectDescription(SObjectDescription sobjectDescription) {
		com.nowellpoint.api.model.document.SObjectDescription document = modelMapper.map(sobjectDescription, com.nowellpoint.api.model.document.SObjectDescription.class);
		delete(document);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	
	protected SObjectDescription findSObjectDescriptionByName(String environmentKey, String name) {
		com.nowellpoint.api.model.document.SObjectDescription document = findOne( eq ( "name", name ) );
		SObjectDescription sobjectDescription = modelMapper.map(document, SObjectDescription.class);
		return sobjectDescription; 
	}
}