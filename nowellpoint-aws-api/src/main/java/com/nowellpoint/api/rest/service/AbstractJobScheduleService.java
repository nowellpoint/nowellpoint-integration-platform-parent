package com.nowellpoint.api.rest.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

import java.time.Instant;
import java.util.Date;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.nowellpoint.api.model.document.Job;
import com.nowellpoint.api.model.document.UserRef;
import com.nowellpoint.api.rest.domain.JobSchedule;
import com.nowellpoint.api.rest.domain.JobScheduleList;
import com.nowellpoint.api.rest.domain.JobStatus;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

public class AbstractJobScheduleService extends AbstractCacheService {
	
	@Inject
	private Event<Job> jobEvent;

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
	
	private void submitJob(JobSchedule jobSchedule) {
		
		Date now = Date.from(Instant.now());
		
		UserRef userRef = new UserRef(jobSchedule.getLastUpdatedBy().getId());
		
		Job job = new Job();
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
	
	protected void update(JobSchedule jobSchedule) {
		MongoDocument document = jobSchedule.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		documentManager.refresh( document );
		jobSchedule.fromDocument(document);
		set(jobSchedule.getId(), document);
		if (jobSchedule.getStatus().equals(JobStatus.SCHEDULED)) {
			submitJob(jobSchedule);
		}
	}
	
	protected void delete(JobSchedule jobSchedule) {
		MongoDocument document = jobSchedule.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.deleteOne(document);
		del(jobSchedule.getId());
	}
}