package com.nowellpoint.api.service;

import com.nowellpoint.api.model.domain.SObjectDetail;

public interface SObjectDetailService {
	
	/**
	 * 
	 * 
	 * 
	 */
	
	void createOrUpdateSObjectDetail(SObjectDetail sobjectDetail);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	void createSObjectDetail(SObjectDetail sobjectDetail);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	void updateSObjectDetail(SObjectDetail sobjectDetail);
	
	/**
	 * 
	 * 
	 * 
	 */
	
	SObjectDetail findByName(String environmentKey, String name);
}