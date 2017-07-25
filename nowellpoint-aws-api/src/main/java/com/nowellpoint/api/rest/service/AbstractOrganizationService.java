package com.nowellpoint.api.rest.service;

import javax.inject.Inject;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.nowellpoint.api.rest.domain.OrganizationOld;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

public abstract class AbstractOrganizationService extends AbstractCacheService {

	@Inject
	protected DocumentManagerFactory documentManagerFactory;
	
	protected void create(OrganizationOld organizationOld) {
		MongoDocument document = organizationOld.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( document );
		organizationOld.fromDocument( document );
		set( organizationOld.getId(), document );
	}
	
	protected void update(OrganizationOld organizationOld) {
		MongoDocument document = organizationOld.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		organizationOld.fromDocument( document );
		set( organizationOld.getId(), document );
	}
	
	protected OrganizationOld findById(String id) {
		com.nowellpoint.api.model.document.Organization document = get(com.nowellpoint.api.model.document.Organization.class, id);
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.fetch( com.nowellpoint.api.model.document.Organization.class, new ObjectId( id ) );
			set(id, document);
		}
		OrganizationOld organizationOld = OrganizationOld.of( document );
		return organizationOld;
	}
	
	protected OrganizationOld query(Bson query) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		com.nowellpoint.api.model.document.Organization document = documentManager.findOne( com.nowellpoint.api.model.document.Organization.class, query );
		OrganizationOld organizationOld = OrganizationOld.of( document );
		return organizationOld;
	}
}