package com.nowellpoint.api.service.impl;

import java.util.Set;

import javax.inject.Inject;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.nowellpoint.api.model.domain.ScheduledJobType;
import com.nowellpoint.api.model.domain.ScheduledJobTypeList;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

abstract class AbstractScheduledJobTypeService extends AbstractCacheService {
	
	@Inject
	protected DocumentManagerFactory documentManagerFactory;
	
	protected ScheduledJobType findById(String id) {
		com.nowellpoint.api.model.document.ScheduledJobType document = get(com.nowellpoint.api.model.document.ScheduledJobType.class, id);
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.findOne(com.nowellpoint.api.model.document.ScheduledJobType.class, new ObjectId( id ) ); 
			set(id, document);
		}
		ScheduledJobType scheduledJobType = new ScheduledJobType( document );
		return scheduledJobType;
	}
	
	protected ScheduledJobTypeList query(Bson query) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		Set<com.nowellpoint.api.model.document.ScheduledJobType> documents = documentManager.find(com.nowellpoint.api.model.document.ScheduledJobType.class, query );
		ScheduledJobTypeList scheduledJobTypes = new ScheduledJobTypeList( documents );
		return scheduledJobTypes;
	}
	
	protected void create(ScheduledJobType scheduledJobType) {
		MongoDocument document = scheduledJobType.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( document );
		set(scheduledJobType.getId(), document);
	}
	
	protected void update(ScheduledJobType scheduledJobType) {
		MongoDocument document = scheduledJobType.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		set(scheduledJobType.getId(), document);
	}
	
	protected void delete(ScheduledJobType scheduledJobType) {
		MongoDocument document = scheduledJobType.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.deleteOne(document);
		del(scheduledJobType.getId());
	}
}