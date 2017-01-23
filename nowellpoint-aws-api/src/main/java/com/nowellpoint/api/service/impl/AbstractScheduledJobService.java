package com.nowellpoint.api.service.impl;

import static com.mongodb.client.model.Filters.eq;

import java.util.Set;

import javax.inject.Inject;

import org.bson.types.ObjectId;

import com.mongodb.DBRef;
import com.nowellpoint.api.model.domain.ScheduledJob;
import com.nowellpoint.api.model.domain.ScheduledJobList;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

public class AbstractScheduledJobService extends AbstractCacheService {

	@Inject
	protected DocumentManagerFactory documentManagerFactory;
	
	protected ScheduledJobList findAllByOwner(String ownerId) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
		Set<com.nowellpoint.api.model.document.ScheduledJob> documents = documentManager.find(com.nowellpoint.api.model.document.ScheduledJob.class,
				eq ( "owner.identity", new DBRef( documentManager.resolveCollectionName( com.nowellpoint.api.model.document.AccountProfile.class ), 
						new ObjectId( ownerId ) ) ) );
		ScheduledJobList resources = new ScheduledJobList(documents);
		return resources;
	}
	
	protected ScheduledJobList findByStatus(String status) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
		Set<com.nowellpoint.api.model.document.ScheduledJob> documents = documentManager.find(com.nowellpoint.api.model.document.ScheduledJob.class, 
				eq ( "status", status ) );
		ScheduledJobList resources = new ScheduledJobList(documents);
		return resources;
	}
	
	protected ScheduledJob findById(String id) {
		com.nowellpoint.api.model.document.ScheduledJob document = get(com.nowellpoint.api.model.document.ScheduledJob.class, id);
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.findOne( com.nowellpoint.api.model.document.ScheduledJob.class, new ObjectId( id ) );
			set(id, document);
		}
		ScheduledJob scheduledJob = new ScheduledJob( document );
		return scheduledJob;
	}
	
	protected void create(ScheduledJob scheduledJob) {
		MongoDocument document = scheduledJob.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( document );
		set(scheduledJob.getId(), document);
	}
	
	protected void update(ScheduledJob scheduledJob) {
		MongoDocument document = scheduledJob.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		set(scheduledJob.getId(), document);
	}
	
	protected void delete(ScheduledJob scheduledJob) {
		MongoDocument document = scheduledJob.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.deleteOne(document);
		del(scheduledJob.getId());
	}
}