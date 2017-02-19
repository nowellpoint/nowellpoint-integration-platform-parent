package com.nowellpoint.api.rest.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

import java.util.Set;

import javax.inject.Inject;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.nowellpoint.api.rest.domain.Job;
import com.nowellpoint.api.rest.domain.JobSchedule;
import com.nowellpoint.api.rest.domain.JobScheduleList;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

public class AbstractScheduledJobService extends AbstractCacheService {

	@Inject
	protected DocumentManagerFactory documentManagerFactory;
	
	protected JobScheduleList findAllByOwner(String ownerId) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
		Set<com.nowellpoint.api.model.document.JobSchedule> documents = documentManager.find(
				com.nowellpoint.api.model.document.JobSchedule.class,
				eq ( "owner", new ObjectId( ownerId ) ) );
		JobScheduleList resources = new JobScheduleList(documents);
		return resources;
	}
	
	protected JobScheduleList findByStatus(String status) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
		Set<com.nowellpoint.api.model.document.JobSchedule> documents = documentManager.find(
				com.nowellpoint.api.model.document.JobSchedule.class, 
				eq ( "status", status ) );
		JobScheduleList resources = new JobScheduleList(documents);
		return resources;
	}
	
	protected JobSchedule findById(String id) {
		com.nowellpoint.api.model.document.JobSchedule document = get(com.nowellpoint.api.model.document.JobSchedule.class, id);
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.fetch( com.nowellpoint.api.model.document.JobSchedule.class, new ObjectId( id ) );
			set(id, document);
		}
		JobSchedule jobSchedule = new JobSchedule( document );
		return jobSchedule;
	}
	
	protected void create(JobSchedule jobSchedule) {
		MongoDocument document = jobSchedule.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( document );
		documentManager.refresh( document );
		jobSchedule.fromDocument(document);
		set(jobSchedule.getId(), document);
	}
	
	protected void submit(Job job) {
		Bson query = and ( 
				eq ( "scheduledJobId", new ObjectId( job.getScheduledJobId() ) ), 
				or ( eq ( "status", "Scheduled" ), eq ( "status", "Stopped" ))); 
		
		MongoDocument document = job.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.upsert(query, document);
	}
	
	protected void update(JobSchedule jobSchedule) {
		MongoDocument document = jobSchedule.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		documentManager.refresh( document );
		jobSchedule.fromDocument(document);
		set(jobSchedule.getId(), document);
	}
	
	protected void delete(JobSchedule jobSchedule) {
		MongoDocument document = jobSchedule.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.deleteOne(document);
		del(jobSchedule.getId());
	}
}