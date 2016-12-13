package com.nowellpoint.api.service;

import com.nowellpoint.api.model.domain.SObjectDetail;
import com.nowellpoint.api.model.mapper.SObjectDetailModelMapper;

public class SObjectDetailService extends SObjectDetailModelMapper {

	public SObjectDetailService() {
		
	}
	
	public void createOrUpdateSObjectDetail(SObjectDetail sobjectDetail) {
		super.createOrUpdateSObjectDetail(sobjectDetail);
	}
	
	public void createSObjectDetail(SObjectDetail sobjectDetail) {
		super.createSObjectDetail(sobjectDetail);
	}
	
	public void updateSObjectDetail(SObjectDetail sobjectDetail) {
		super.updateSObjectDetail(sobjectDetail);
	}
	
	public SObjectDetail findSObjectDetailByName(String environmentKey, String name) {
		return super.findSObjectDetailByName(environmentKey, name);
	}
}