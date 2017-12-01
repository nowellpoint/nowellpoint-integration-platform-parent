package com.nowellpoint.api.service.impl;

import static com.mongodb.client.model.Filters.eq;

import java.util.Set;

import javax.inject.Inject;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.nowellpoint.api.rest.domain.SalesforceConnectorOrig;
import com.nowellpoint.api.rest.domain.SalesforceConnectorList;
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
		Set<com.nowellpoint.api.model.document.SalesforceConnector> documents = documentManager.find(
				com.nowellpoint.api.model.document.SalesforceConnector.class,
				eq ( "owner", new ObjectId( ownerId ) ) );
		SalesforceConnectorList resources = new SalesforceConnectorList(documents);
		return resources;
	}
	
	protected void create(SalesforceConnectorOrig salesforceConnectorOrig) {
		MongoDocument document = salesforceConnectorOrig.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( document );
		salesforceConnectorOrig.fromDocument(document);
		set(salesforceConnectorOrig.getId(), document);
	}
	
	protected void update(SalesforceConnectorOrig salesforceConnectorOrig) {
		MongoDocument document = salesforceConnectorOrig.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		salesforceConnectorOrig.fromDocument(document);
		set(salesforceConnectorOrig.getId(), document);
	}
	
	protected void delete(SalesforceConnectorOrig salesforceConnectorOrig) {
		MongoDocument document = salesforceConnectorOrig.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.deleteOne(document);
		del(salesforceConnectorOrig.getId());
	}
	
	protected SalesforceConnectorOrig findById(String id) {		
		com.nowellpoint.api.model.document.SalesforceConnector document = get(com.nowellpoint.api.model.document.SalesforceConnector.class, id );
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.fetch(com.nowellpoint.api.model.document.SalesforceConnector.class, new ObjectId( id ) );
			set(id, document);
		}
		SalesforceConnectorOrig resource = SalesforceConnectorOrig.of( document );
		return resource;
	}
	
	protected SalesforceConnectorList query(Bson query) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
		Set<com.nowellpoint.api.model.document.SalesforceConnector> documents = documentManager.find( com.nowellpoint.api.model.document.SalesforceConnector.class, query );
		SalesforceConnectorList salesforceConnectorList = new SalesforceConnectorList(documents);
		return salesforceConnectorList;
	}
}