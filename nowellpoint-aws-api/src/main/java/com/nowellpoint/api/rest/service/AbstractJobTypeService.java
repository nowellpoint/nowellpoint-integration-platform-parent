package com.nowellpoint.api.rest.service;

import java.util.Set;

import javax.inject.Inject;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.nowellpoint.api.rest.domain.JobType;
import com.nowellpoint.api.rest.domain.JobTypeList;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

abstract class AbstractJobTypeService extends AbstractCacheService {
	
	@Inject
	protected DocumentManagerFactory documentManagerFactory;
	
	protected JobType findById(String id) {
		com.nowellpoint.api.model.document.JobType document = get(com.nowellpoint.api.model.document.JobType.class, id);
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.fetch(com.nowellpoint.api.model.document.JobType.class, new ObjectId( id ) ); 
			set(id, document);
		}
		JobType jobType = JobType.of( document );
		return jobType;
	}
	
	protected JobType findOne(Bson query) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		MongoDocument document = documentManager.findOne(com.nowellpoint.api.model.document.JobType.class, query );
		JobType jobType = JobType.of( document );
		return jobType;
	}
	
	protected JobTypeList query(Bson query) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		Set<com.nowellpoint.api.model.document.JobType> documents = documentManager.find( com.nowellpoint.api.model.document.JobType.class, query );
		JobTypeList scheduledJobTypes = new JobTypeList( documents );
		return scheduledJobTypes;
	}
	
	protected void create(JobType jobType) {
		MongoDocument document = jobType.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( document );
		jobType.fromDocument(document);
		set(jobType.getId(), document);
	}
	
	protected void update(JobType jobType) {
		MongoDocument document = jobType.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		jobType.fromDocument(document);
		set(jobType.getId(), document);
	}
	
	protected void delete(JobType jobType) {
		MongoDocument document = jobType.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.deleteOne(document);
		del(jobType.getId());
	}
}