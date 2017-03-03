package com.nowellpoint.api.rest.service;

import static com.mongodb.client.model.Filters.eq;

import java.util.Set;

import javax.inject.Inject;

import org.bson.types.ObjectId;

import com.nowellpoint.api.rest.domain.JobSpecification;
import com.nowellpoint.api.rest.domain.JobSpecificationList;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

public class AbstractJobSpecificationService extends AbstractCacheService {

	@Inject
	protected DocumentManagerFactory documentManagerFactory;
	
	protected JobSpecificationList findAllByOwner(String ownerId) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
		Set<com.nowellpoint.api.model.document.JobSpecification> documents = documentManager.find(
				com.nowellpoint.api.model.document.JobSpecification.class,
				eq ( "owner", new ObjectId( ownerId ) ) );
		JobSpecificationList resources = new JobSpecificationList(documents);
		return resources;
	}
	
	protected JobSpecificationList findByStatus(String status) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
		Set<com.nowellpoint.api.model.document.JobSpecification> documents = documentManager.find(
				com.nowellpoint.api.model.document.JobSpecification.class, 
				eq ( "status", status ) );
		JobSpecificationList resources = new JobSpecificationList(documents);
		return resources;
	}
	
	protected JobSpecification findById(String id) {
		com.nowellpoint.api.model.document.JobSpecification document = get(com.nowellpoint.api.model.document.JobSpecification.class, id);
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.fetch( com.nowellpoint.api.model.document.JobSpecification.class, new ObjectId( id ) );
			set(id, document);
		}
		JobSpecification jobSchedule = JobSpecification.of( document );
		return jobSchedule;
	}
	
	protected void create(JobSpecification jobSchedule) {
		MongoDocument document = jobSchedule.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( document );
		jobSchedule.fromDocument(document);
		set(jobSchedule.getId(), document);
	}
	
	protected void update(JobSpecification jobSchedule) {
		MongoDocument document = jobSchedule.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		jobSchedule.fromDocument( document );
		set( jobSchedule.getId(), document );
	}
	
	protected void delete(JobSpecification jobSchedule) {
		MongoDocument document = jobSchedule.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.deleteOne(document);
		del(jobSchedule.getId());
	}
}