package com.nowellpoint.api.rest.service;

import javax.inject.Inject;

import org.bson.conversions.Bson;

import com.nowellpoint.api.rest.domain.Registration;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.MongoDocument;

public class AbstractRegistrationService extends AbstractCacheService {
	
	@Inject
	protected DocumentManagerFactory documentManagerFactory;

	protected void create(Registration registration) {
		MongoDocument document = registration.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( document );
		registration.fromDocument( document );
		set( registration.getId(), document );
	}
	
	protected Registration findOne(Bson query) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		com.nowellpoint.api.model.document.Registration document = documentManager.findOne( com.nowellpoint.api.model.document.Registration.class, query );			
		Registration registration = Registration.of( document );
		return registration;
	}
}