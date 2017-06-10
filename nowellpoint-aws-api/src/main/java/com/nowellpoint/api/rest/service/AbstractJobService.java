package com.nowellpoint.api.rest.service;

import static com.mongodb.client.model.Filters.eq;

import java.util.Set;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.nowellpoint.annotation.Stop;
import com.nowellpoint.annotation.Submit;
import com.nowellpoint.annotation.Terminate;
import com.nowellpoint.api.rest.domain.Job;
import com.nowellpoint.api.rest.domain.JobList;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

public class AbstractJobService extends AbstractCacheService {
	
	@Inject
	protected DocumentManagerFactory documentManagerFactory;
	
	@Inject
	@Submit
	private Event<Job> submitJob;
	
	@Inject
	@Stop
	private Event<Job> stopJob;
	
	@Inject
	@Terminate
	private Event<Job> terminateJob;
	
	protected Job findById(String id) {
		com.nowellpoint.api.model.document.Job document = get(com.nowellpoint.api.model.document.Job.class, id);
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.fetch( com.nowellpoint.api.model.document.Job.class, new ObjectId( id ) );
			set(id, document);
		}
		Job job = Job.of( document );
		return job;
	}
	
	protected JobList findAllByOwner(String ownerId) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
		Set<com.nowellpoint.api.model.document.Job> documents = documentManager.find(
				com.nowellpoint.api.model.document.Job.class,
				eq ( "owner", new ObjectId( ownerId ) ) );
		JobList resources = new JobList(documents);
		return resources;
	}
	
	protected JobList findAllScheduled() {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
		Set<com.nowellpoint.api.model.document.Job> documents = documentManager.find(
				com.nowellpoint.api.model.document.Job.class,
				eq ( "status", "SCHEDULED" ) );
		JobList resources = new JobList(documents);
		return resources;
	}
	
	protected JobList query(Bson query) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
		Set<com.nowellpoint.api.model.document.Job> documents = documentManager.find( com.nowellpoint.api.model.document.Job.class, query );
		JobList resources = new JobList(documents);
		return resources;
	}
	
	protected void create(Job job) {
		com.nowellpoint.api.model.document.Job document = job.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( document );
		job.fromDocument(document);
		set(job.getId(), document);
		submitJob.fire(job);
	}
	
	protected void update(Job job) {
		MongoDocument document = job.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		job.fromDocument(document);
		set(job.getId(), document);
	}
	
	protected void delete(Job job) {
		MongoDocument document = job.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.deleteOne(document);
		del(job.getId());
	}
	
	protected void submit(Job job) {
		submitJob.fire(job);
	}
	
	protected void stop(Job job) {
		stopJob.fire(job);
	}
	
	protected void termiante(Job job) {
		terminateJob.fire(job);
	}
}