package com.nowellpoint.api.service.impl;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.time.Instant;
import java.util.Date;

import com.nowellpoint.api.model.domain.SObjectDetail;
import com.nowellpoint.api.model.domain.UserInfo;
import com.nowellpoint.api.service.SObjectDetailService;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.util.Assert;

public class SObjectDetailServiceImpl extends AbstractSObjectDetailService implements SObjectDetailService {

	public SObjectDetailServiceImpl() {
		
	}
	
	@Override
	public void createOrUpdateSObjectDetail(SObjectDetail sobjectDetail) {
		if (Assert.isNull(sobjectDetail.getId())) {
			createSObjectDetail(sobjectDetail);
		} else {
			updateSObjectDetail(sobjectDetail);
		}
	}
	
	@Override
	public void createSObjectDetail(SObjectDetail sobjectDetail) {
		
		UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
		sobjectDetail.setCreatedDate(now);
		sobjectDetail.setCreatedBy(userInfo);
		sobjectDetail.setLastModifiedDate(now);
		sobjectDetail.setLastModifiedBy(userInfo);
		sobjectDetail.setSystemCreatedDate(now);
		sobjectDetail.setSystemModifiedDate(now);
		
		create(sobjectDetail);
	}
	
	@Override
	public void updateSObjectDetail(SObjectDetail sobjectDetail) {
		UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
		sobjectDetail.setLastModifiedDate(now);
		sobjectDetail.setLastModifiedBy(userInfo);
		sobjectDetail.setSystemModifiedDate(now);
		
		update(sobjectDetail);
	}
	
	@Override
	public SObjectDetail findByName(String instanceKey, String name) {
		return super.query( and ( eq ( "environmentKey", instanceKey ), eq ( "name", name ) ) );
	}
}