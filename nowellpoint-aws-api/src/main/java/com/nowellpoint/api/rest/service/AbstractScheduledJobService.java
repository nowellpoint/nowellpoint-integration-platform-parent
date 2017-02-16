package com.nowellpoint.api.rest.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

import java.util.Set;

import javax.inject.Inject;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.nowellpoint.api.rest.domain.Job;
import com.nowellpoint.api.rest.domain.ScheduledJob;
import com.nowellpoint.api.rest.domain.ScheduledJobList;
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
		Set<com.nowellpoint.api.model.document.ScheduledJob> documents = documentManager.find(
				com.nowellpoint.api.model.document.ScheduledJob.class,
				eq ( "owner", new ObjectId( ownerId ) ) );
		ScheduledJobList resources = new ScheduledJobList(documents);
		return resources;
	}
	
	protected ScheduledJobList findByStatus(String status) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
		Set<com.nowellpoint.api.model.document.ScheduledJob> documents = documentManager.find(
				com.nowellpoint.api.model.document.ScheduledJob.class, 
				eq ( "status", status ) );
		ScheduledJobList resources = new ScheduledJobList(documents);
		return resources;
	}
	
	protected ScheduledJob findById(String id) {
		com.nowellpoint.api.model.document.ScheduledJob document = get(com.nowellpoint.api.model.document.ScheduledJob.class, id);
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.fetch( com.nowellpoint.api.model.document.ScheduledJob.class, new ObjectId( id ) );
			set(id, document);
		}
		ScheduledJob scheduledJob = new ScheduledJob( document );
		return scheduledJob;
	}
	
	protected void create(ScheduledJob scheduledJob) {
		MongoDocument document = scheduledJob.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( document );
		documentManager.refresh( document );
		scheduledJob.fromDocument(document);
		set(scheduledJob.getId(), document);
	}
	
	protected void submit(Job job) {
		Bson query = and ( 
				eq ( "scheduledJobId", new ObjectId( job.getScheduledJobId() ) ), 
				or ( eq ( "status", "Scheduled" ), eq ( "status", "Stopped" ))); 
		
		MongoDocument document = job.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.upsert(query, document);
	}
	
	protected void update(ScheduledJob scheduledJob) {
		MongoDocument document = scheduledJob.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		documentManager.refresh( document );
		scheduledJob.fromDocument(document);
		set(scheduledJob.getId(), document);
	}
	
	protected void delete(ScheduledJob scheduledJob) {
		MongoDocument document = scheduledJob.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.deleteOne(document);
		del(scheduledJob.getId());
	}
}