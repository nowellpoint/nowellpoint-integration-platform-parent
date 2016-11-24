package com.nowellpoint.api.service;

import com.nowellpoint.api.model.domain.SObjectDescription;
import com.nowellpoint.api.model.mapper.SObjectDescriptionModelMapper;

public class SObjectDescriptionService extends SObjectDescriptionModelMapper {

	public SObjectDescriptionService() {
		
	}
	
	public void createOrUpdateSObjectDescription(SObjectDescription sobjectDescription) {
		super.createOrUpdateSObjectDescription(sobjectDescription);
	}
	
	public void createSObjectDescription(SObjectDescription sobjectDescription) {
		super.createSObjectDescription(sobjectDescription);
	}
	
	public void updateSObjectDescription(SObjectDescription sobjectDescription) {
		super.updateSObjectDescription(sobjectDescription);
	}
	
	public SObjectDescription findSObjectDescriptionByName(String environmentKey, String name) {
		return super.findSObjectDescriptionByName(environmentKey, name);
	}
}