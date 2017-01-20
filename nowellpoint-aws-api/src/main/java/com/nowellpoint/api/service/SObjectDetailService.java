package com.nowellpoint.api.service;

import static com.mongodb.client.model.Filters.eq;

import java.time.Instant;
import java.util.Date;

import com.nowellpoint.api.model.domain.SObjectDetail;
import com.nowellpoint.api.model.domain.UserInfo;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.mongodb.document.MongoDocumentService;
import com.nowellpoint.util.Assert;

public class SObjectDetailService {
	
	private MongoDocumentService mongoDocumentService = new MongoDocumentService();

	public SObjectDetailService() {
		
	}
	
	public void createOrUpdateSObjectDetail(SObjectDetail sobjectDetail) {
		if (Assert.isNull(sobjectDetail.getId())) {
			createSObjectDetail(sobjectDetail);
		} else {
			updateSObjectDetail(sobjectDetail);
		}
	}
	
	public void createSObjectDetail(SObjectDetail sobjectDetail) {
		
		UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
		sobjectDetail.setCreatedDate(now);
		sobjectDetail.setCreatedBy(userInfo);
		sobjectDetail.setLastModifiedDate(now);
		sobjectDetail.setLastModifiedBy(userInfo);
		sobjectDetail.setSystemCreatedDate(now);
		sobjectDetail.setSystemModifiedDate(now);
		
		mongoDocumentService.create(sobjectDetail.toDocument());
	}
	
	public void updateSObjectDetail(SObjectDetail sobjectDetail) {
		UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
		sobjectDetail.setLastModifiedDate(now);
		sobjectDetail.setLastModifiedBy(userInfo);
		sobjectDetail.setSystemModifiedDate(now);
		
		mongoDocumentService.replace(sobjectDetail.toDocument());
	}
	
	public SObjectDetail findByName(String environmentKey, String name) {
		com.nowellpoint.api.model.document.SObjectDetail document = mongoDocumentService.findOne(com.nowellpoint.api.model.document.SObjectDetail.class, eq ( "name", name ));
		SObjectDetail resource = new SObjectDetail(document);
		return resource;
	}
}