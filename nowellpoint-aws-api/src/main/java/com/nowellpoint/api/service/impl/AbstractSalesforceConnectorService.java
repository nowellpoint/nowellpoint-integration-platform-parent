package com.nowellpoint.api.service.impl;

import static com.mongodb.client.model.Filters.eq;

import java.util.Set;

import javax.inject.Inject;

import org.bson.types.ObjectId;

import com.mongodb.DBRef;
import com.mongodb.client.model.Filters;
import com.nowellpoint.api.model.domain.SalesforceConnector;
import com.nowellpoint.api.model.domain.SalesforceConnectorList;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

/**
 * 
 * 
 * @author jherson
 *
 * 
 */

abstract class AbstractSalesforceConnectorService extends AbstractCacheService {
	
	@Inject
	protected DocumentManagerFactory documentManagerFactory;
	
	protected SalesforceConnectorList findAllByOwner(String ownerId) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
		Set<com.nowellpoint.api.model.document.SalesforceConnector> documents = documentManager.find(com.nowellpoint.api.model.document.SalesforceConnector.class,
				eq ( "owner.identity", new DBRef( documentManager.resolveCollectionName( com.nowellpoint.api.model.document.AccountProfile.class ), 
						new ObjectId( ownerId ) ) ) );
		SalesforceConnectorList resources = new SalesforceConnectorList(documents);
		return resources;
	}
	
	protected void create(SalesforceConnector salesforceConnector) {
		MongoDocument document = salesforceConnector.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( document );
		set(salesforceConnector.getId(), document);
	}
	
	protected void update(SalesforceConnector salesforceConnector) {
		MongoDocument document = salesforceConnector.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		set(salesforceConnector.getId(), document);
	}
	
	protected void delete(SalesforceConnector salesforceConnector) {
		MongoDocument document = salesforceConnector.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.deleteOne(document);
		del(salesforceConnector.getId());
	}
	
	protected void deleteSobjects(String instanceKey) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.deleteMany(com.nowellpoint.api.model.document.SObjectDetail.class, Filters.eq ( "environmentKey", instanceKey ));
	}
	
	protected SalesforceConnector findById(String id) {		
		com.nowellpoint.api.model.document.SalesforceConnector document = get(com.nowellpoint.api.model.document.SalesforceConnector.class, id );
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.fetch(com.nowellpoint.api.model.document.SalesforceConnector.class, new ObjectId( id ) );
			set(id, document);
		}
		SalesforceConnector resource = new SalesforceConnector( document );
		return resource;
	}
}