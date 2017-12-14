package com.nowellpoint.api.service.impl;

import static com.mongodb.client.model.Filters.eq;

import java.util.Set;

import javax.inject.Inject;

import org.bson.types.ObjectId;

import com.nowellpoint.api.rest.domain.Connector;
import com.nowellpoint.api.rest.domain.ConnectorList;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

public class AbstractConnectorService extends AbstractCacheService {
	
	@Inject
	protected DocumentManagerFactory documentManagerFactory;
	
	protected Connector retrieve(String id) {		
		com.nowellpoint.api.model.document.Connector document = get(com.nowellpoint.api.model.document.Connector.class, id );
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.fetch(com.nowellpoint.api.model.document.Connector.class, new ObjectId( id ) );
			set(id, document);
		}
		Connector resource = Connector.of( document );
		return resource;
	}
	
	protected ConnectorList findAllByOwner(String ownerId) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
		Set<com.nowellpoint.api.model.document.Connector> documents = documentManager.find(
				com.nowellpoint.api.model.document.Connector.class,
				eq ( "owner", new ObjectId( ownerId ) ) );
		ConnectorList resources = new ConnectorList( documents );
		return resources;
	}
	
	protected void create(Connector connector) {
		MongoDocument document = connector.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( document );
		connector.fromDocument(document);
		set(connector.getId(), document);
	}
	
	protected void update(Connector connector) {
		MongoDocument document = connector.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		connector.fromDocument(document);
		set(connector.getId(), document);
	}
	
	protected void delete(Connector connector) {
		MongoDocument document = connector.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.deleteOne(document);
		del(connector.getId());
	}
}