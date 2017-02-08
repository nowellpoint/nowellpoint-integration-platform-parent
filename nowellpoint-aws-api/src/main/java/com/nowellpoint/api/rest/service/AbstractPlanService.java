package com.nowellpoint.api.rest.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.Set;

import javax.inject.Inject;

import org.bson.types.ObjectId;

import com.nowellpoint.api.rest.domain.Plan;
import com.nowellpoint.api.rest.domain.PlanList;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.util.Assert;

public class AbstractPlanService extends AbstractCacheService {
	
	@Inject
	private DocumentManagerFactory documentManagerFactory;
	
	public PlanList getAllActive(String localeSidKey, String languageSidKey) {
		
		if (Assert.isNull(localeSidKey)) {
			throw new IllegalArgumentException("Missing localeSidKey query parameter");
		}
		
		if (Assert.isNull(languageSidKey)) {
			throw new IllegalArgumentException("Missing languageSidKey query parameter");
		}
		
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		
		Set<com.nowellpoint.api.model.document.Plan> documents = documentManager.find(
				com.nowellpoint.api.model.document.Plan.class, and ( 
						eq ( "isActive", Boolean.TRUE ), 
						eq ( "localeSidKey", localeSidKey ), 
						eq ( "languageSidKey", languageSidKey ) ) );
		
		PlanList resources = new PlanList(documents);
		
		return resources;
	}
	
	public Plan findById(String id) {
		com.nowellpoint.api.model.document.Plan document = get(com.nowellpoint.api.model.document.Plan.class, id);
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.fetch(com.nowellpoint.api.model.document.Plan.class, new ObjectId( id ) );
			set(id, document);
		}
		Plan resource = new Plan( document );
		return resource;
	}
	
	public Plan findByPlanCode(String planCode) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		com.nowellpoint.api.model.document.Plan document = documentManager.findOne(com.nowellpoint.api.model.document.Plan.class, eq ( "planCode", planCode ));
		Plan resource = new Plan( document );
		return resource;
	}
}