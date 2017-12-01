package com.nowellpoint.api.service.impl;

import static com.mongodb.client.model.Filters.eq;

import java.util.Set;

import javax.inject.Inject;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.nowellpoint.api.rest.domain.JobOrig;
import com.nowellpoint.api.rest.domain.JobList;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

public class AbstractJobService extends AbstractCacheService {
	
	@Inject
	protected DocumentManagerFactory documentManagerFactory;
	
	protected JobOrig findById(String id) {
		com.nowellpoint.api.model.document.Job document = get(com.nowellpoint.api.model.document.Job.class, id);
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.fetch( com.nowellpoint.api.model.document.Job.class, new ObjectId( id ) );
			set(id, document);
		}
		JobOrig jobOrig = JobOrig.of( document );
		return jobOrig;
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
	
	protected void create(JobOrig jobOrig) {
		com.nowellpoint.api.model.document.Job document = jobOrig.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( document );
		jobOrig.fromDocument(document);
		set(jobOrig.getId(), document);
	}
	
	protected void update(JobOrig jobOrig) {
		MongoDocument document = jobOrig.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		jobOrig.fromDocument(document);
		set(jobOrig.getId(), document);
	}
	
	protected void delete(JobOrig jobOrig) {
		MongoDocument document = jobOrig.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.deleteOne(document);
		del(jobOrig.getId());
	}
}