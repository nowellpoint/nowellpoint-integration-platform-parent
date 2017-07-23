package com.nowellpoint.api.rest.service;

import javax.inject.Inject;

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
	}
}