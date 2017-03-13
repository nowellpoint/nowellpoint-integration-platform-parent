package com.nowellpoint.api.rest.service;

import static com.mongodb.client.model.Filters.eq;

import java.util.Set;

import javax.inject.Inject;

import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;
import com.nowellpoint.api.rest.domain.SObjectDetail;
import com.nowellpoint.api.rest.domain.SalesforceConnector;
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
	
	protected void create(SalesforceConnector salesforceConnector) {
		MongoDocument document = salesforceConnector.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( document );
		salesforceConnector.fromDocument(document);
		set(salesforceConnector.getId(), document);
	}
	
	protected void update(SalesforceConnector salesforceConnector) {
		MongoDocument document = salesforceConnector.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		salesforceConnector.fromDocument(document);
		set(salesforceConnector.getId(), document);
	}
	
	protected void delete(SalesforceConnector salesforceConnector) {
		MongoDocument document = salesforceConnector.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.deleteOne(document);
		del(salesforceConnector.getId());
	}
	
	protected void deleteSObjectDetail(String connectorId) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.deleteMany(com.nowellpoint.api.model.document.SObjectDetail.class, Filters.eq ( "connectorId", new ObjectId( connectorId )));
	}
	
	protected void create(SObjectDetail sobjectDetail) {
		MongoDocument document = sobjectDetail.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne(document);
		sobjectDetail.fromDocument(document);
	}
	
	protected SObjectDetail findSObjectDetail(String id, String sobjectName) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		com.nowellpoint.api.model.document.SObjectDetail document = documentManager.findOne(com.nowellpoint.api.model.document.SObjectDetail.class, Filters.and ( 
				Filters.eq ( "connectorId", new ObjectId( id )), 
				Filters.eq("result.name", sobjectName)));
		SObjectDetail sobjectDetail = SObjectDetail.of( document );
		return sobjectDetail;
	}
	
	protected SalesforceConnector findById(String id) {		
		com.nowellpoint.api.model.document.SalesforceConnector document = get(com.nowellpoint.api.model.document.SalesforceConnector.class, id );
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.fetch(com.nowellpoint.api.model.document.SalesforceConnector.class, new ObjectId( id ) );
			set(id, document);
		}
		SalesforceConnector resource = SalesforceConnector.of( document );
		return resource;
	}
}