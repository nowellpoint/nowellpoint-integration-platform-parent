package com.nowellpoint.api.service;

import java.time.Instant;
import java.util.Date;

import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.nowellpoint.api.model.domain.ScheduledJobType;
import com.nowellpoint.api.model.domain.ScheduledJobTypeList;
import com.nowellpoint.mongodb.document.MongoDocumentService;

public class ScheduledJobTypeService {
	
	private MongoDocumentService mongoDocumentService = new MongoDocumentService();

	public ScheduledJobTypeService() {
		
	}
	
	public ScheduledJobType findById(String id) {
		com.nowellpoint.api.model.document.ScheduledJobType document = mongoDocumentService.find(com.nowellpoint.api.model.document.ScheduledJobType.class, new ObjectId( id ) ); 
		ScheduledJobType resource = new ScheduledJobType( document );
		return resource;
	}
	
	public ScheduledJobTypeList findByLanguage(String languageSidKey) {
		FindIterable<com.nowellpoint.api.model.document.ScheduledJobType> documents = mongoDocumentService.find(com.nowellpoint.api.model.document.ScheduledJobType.class, Filters.eq ( "languageSidKey", languageSidKey ) ); 
		ScheduledJobTypeList resources = new ScheduledJobTypeList( documents );
		return resources;
	}
	
	public void createScheduledJobType(ScheduledJobType scheduledJobType) {
		//UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
		scheduledJobType.setCreatedDate(now);
		//scheduledJobType.setCreatedBy(userInfo);
		scheduledJobType.setLastModifiedDate(now);
		//scheduledJobType.setLastModifiedBy(userInfo);
		scheduledJobType.setSystemCreatedDate(now);
		scheduledJobType.setSystemModifiedDate(now);
		
		mongoDocumentService.create(scheduledJobType.toDocument());
	}
	
	public void updateScheduledJobType(String id, ScheduledJobType scheduledJobType) {
		scheduledJobType.setId(id);
		
		//UserInfo userInfo = new UserInfo(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
				
		scheduledJobType.setLastModifiedDate(now);
		//scheduledJobType.setLastModifiedBy(userInfo);
		scheduledJobType.setSystemModifiedDate(now);
		
		mongoDocumentService.replace(scheduledJobType.toDocument());
	}
	
	public void deleteScheduledJobType(String id) {
		ScheduledJobType scheduledJobType = findById(id);
		mongoDocumentService.delete(scheduledJobType);
	}
}