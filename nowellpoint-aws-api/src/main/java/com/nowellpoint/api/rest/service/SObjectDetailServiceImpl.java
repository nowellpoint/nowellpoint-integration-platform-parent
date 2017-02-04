package com.nowellpoint.api.rest.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.time.Instant;
import java.util.Date;

import com.nowellpoint.api.rest.domain.SObjectDetail;
import com.nowellpoint.api.rest.domain.UserInfo;
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
		
		sobjectDetail.setCreatedOn(now);
		sobjectDetail.setCreatedBy(userInfo);
		sobjectDetail.setLastUpdatedOn(now);
		sobjectDetail.setLastModifiedBy(userInfo);
		
		create(sobjectDetail);
	}
	
	@Override
	public void updateSObjectDetail(SObjectDetail sobjectDetail) {
		UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
		sobjectDetail.setLastUpdatedOn(now);
		sobjectDetail.setLastModifiedBy(userInfo);
		
		update(sobjectDetail);
	}
	
	@Override
	public SObjectDetail findByName(String instanceKey, String name) {
		return super.query( and ( eq ( "environmentKey", instanceKey ), eq ( "name", name ) ) );
	}
}