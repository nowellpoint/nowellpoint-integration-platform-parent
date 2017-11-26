package com.nowellpoint.api.service.impl;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.Set;

import javax.inject.Inject;

import org.bson.types.ObjectId;

import com.nowellpoint.api.rest.domain.PlanOrig;
import com.nowellpoint.api.rest.domain.PlanList;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.util.Assert;

public class AbstractPlanService extends AbstractCacheService {
	
	@Inject
	private DocumentManagerFactory documentManagerFactory;
	
	public PlanList getAllActive(String locale, String language) {
		
		if (Assert.isNull(locale)) {
			throw new IllegalArgumentException("Missing locale query parameter");
		}
		
		if (Assert.isNull(language)) {
			throw new IllegalArgumentException("Missing language query parameter");
		}
		
		Set<com.nowellpoint.api.model.document.Plan> documents = hscan(com.nowellpoint.api.model.document.Plan.class, locale.concat(":").concat(language));
		
		if (documents.isEmpty()) {

			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			
			documents = documentManager.find(
					com.nowellpoint.api.model.document.Plan.class, and ( 
							eq ( "isActive", Boolean.TRUE ), 
							eq ( "locale", locale.toLowerCase() ), 
							eq ( "language", language ) ) );
			
			hset(locale.concat(":").concat(language), documents);
			
		}
		
		PlanList planList = new PlanList(documents);
		return planList;
	}
	
	public PlanOrig findById(String id) {
		com.nowellpoint.api.model.document.Plan document = get(com.nowellpoint.api.model.document.Plan.class, id);
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.fetch(com.nowellpoint.api.model.document.Plan.class, new ObjectId( id ) );
			set(id, document);
		}
		PlanOrig resource = PlanOrig.of( document );
		return resource;
	}
	
	public PlanOrig findByPlanCode(String planCode) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		com.nowellpoint.api.model.document.Plan document = documentManager.findOne(com.nowellpoint.api.model.document.Plan.class, eq ( "planCode", planCode ));
		PlanOrig resource = PlanOrig.of( document );
		return resource;
	}
}