package com.nowellpoint.api.rest.service;

import static com.mongodb.client.model.Filters.eq;

import java.time.Instant;
import java.util.Date;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.bson.types.ObjectId;

import com.nowellpoint.api.model.document.Job;
import com.nowellpoint.api.model.document.UserRef;
import com.nowellpoint.api.rest.domain.JobSpecification;
import com.nowellpoint.api.rest.domain.JobSpecificationList;
import com.nowellpoint.api.rest.domain.JobStatus;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

public class AbstractJobSpecificationService extends AbstractCacheService {
	
	@Inject
	private Event<Job> jobEvent;

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
		JobSpecification jobSchedule = new JobSpecification( document );
		return jobSchedule;
	}
	
	protected void create(JobSpecification jobSchedule) {
		MongoDocument document = jobSchedule.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( document );
		documentManager.refresh( document );
		jobSchedule.fromDocument(document);
		set(jobSchedule.getId(), document);
	}
	
	private void submitJob(JobSpecification jobSchedule) {
		
		Date now = Date.from(Instant.now());
		
		UserRef userRef = new UserRef(jobSchedule.getLastUpdatedBy().getId());
		
		Job job = new Job();
		job.setScheduledJobId(jobSchedule.getId());
		job.setCreatedBy(userRef);
		job.setCreatedOn(now);
		job.setHours(jobSchedule.getHours());
		job.setJobName(jobSchedule.getJobType().getCode());
		job.setLastUpdatedBy(userRef);
		job.setLastUpdatedOn(now);
		job.setDayOfMonth(jobSchedule.getDayOfMonth());
		job.setDayOfWeek(jobSchedule.getDayOfWeek());
		job.setEnd(jobSchedule.getEnd());
		job.setStart(jobSchedule.getStart());
		job.setTimeZone(jobSchedule.getTimeZone());
		job.setMinutes(jobSchedule.getMinutes());
		job.setMonth(jobSchedule.getMonth());
		job.setSeconds(jobSchedule.getSeconds());
		job.setStatus(JobStatus.SCHEDULED);
		job.setYear(jobSchedule.getYear());
		
		jobEvent.fire(job);
	}
	
	protected void update(JobSpecification jobSchedule) {
		MongoDocument document = jobSchedule.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		documentManager.refresh( document );
		jobSchedule.fromDocument( document );
		set( jobSchedule.getId(), document );
		
		if (jobSchedule.getStatus().equals(JobStatus.SCHEDULED)) {
			submitJob(jobSchedule);
		}
	}
	
	protected void delete(JobSpecification jobSchedule) {
		MongoDocument document = jobSchedule.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.deleteOne(document);
		del(jobSchedule.getId());
	}
}