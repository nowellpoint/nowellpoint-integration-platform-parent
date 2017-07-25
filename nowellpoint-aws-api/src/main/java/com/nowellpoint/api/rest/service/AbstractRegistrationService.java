package com.nowellpoint.api.rest.service;

import javax.inject.Inject;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.nowellpoint.api.rest.domain.Registration;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

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
	
	protected void update(Registration registration) {
		MongoDocument document = registration.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		registration.fromDocument( document );
		set( registration.getId(), document );
	}
	
	protected void delete(Registration registration) {
		MongoDocument document = registration.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.deleteOne(document);
		del(registration.getId());
	}
	
	protected Registration findById(String id) {
		com.nowellpoint.api.model.document.Registration document = get(com.nowellpoint.api.model.document.Registration.class, id);
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.fetch( com.nowellpoint.api.model.document.Registration.class, new ObjectId( id ) );
			set(id, document);
		}
		Registration registration = Registration.of( document );
		return registration;
	}
	
	protected Registration findOne(Bson query) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		com.nowellpoint.api.model.document.Registration document = documentManager.findOne( com.nowellpoint.api.model.document.Registration.class, query );			
		Registration registration = Registration.of( document );
		return registration;
	}
}