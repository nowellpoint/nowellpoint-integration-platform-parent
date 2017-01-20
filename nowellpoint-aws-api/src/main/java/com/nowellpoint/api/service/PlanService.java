package com.nowellpoint.api.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import com.nowellpoint.api.model.domain.Plan;
import com.nowellpoint.api.model.domain.PlanList;
import com.nowellpoint.mongodb.document.MongoDocumentService;
import com.nowellpoint.util.Assert;

public class PlanService {
	
	private MongoDocumentService mongoDocumentService = new MongoDocumentService();

	public PlanService() {
		super();
	}
	
	public PlanList getAllActive(String localeSidKey, String languageLocaleKey) {
		if (Assert.isNull(localeSidKey)) {
			throw new IllegalArgumentException("Missing localeSidKey query parameter");
		}
		
		if (Assert.isNull(languageLocaleKey)) {
			throw new IllegalArgumentException("Missing languageLocaleKey query parameter");
		}
		
		FindIterable<com.nowellpoint.api.model.document.Plan> documents = mongoDocumentService.find(com.nowellpoint.api.model.document.Plan.class, and ( 
				eq ( "isActive", Boolean.TRUE ), 
				eq ( "localeSidKey", localeSidKey ), 
				eq ( "languageLocaleKey", languageLocaleKey ) ) );
		
		PlanList resources = new PlanList(documents);
		
		return resources;
		
	}
	
	public Plan findById(String id) {
		com.nowellpoint.api.model.document.Plan document = mongoDocumentService.find(com.nowellpoint.api.model.document.Plan.class, new ObjectId( id ) );
		Plan resource = new Plan( document );
		return resource;
	}
	
	public Plan findByPlanCode(String planCode) {
		com.nowellpoint.api.model.document.Plan document = mongoDocumentService.findOne(com.nowellpoint.api.model.document.Plan.class, eq ( "planCode", planCode ));
		Plan resource = new Plan( document );
		return resource;
	}
}