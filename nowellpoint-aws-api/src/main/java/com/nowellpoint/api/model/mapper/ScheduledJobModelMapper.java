package com.nowellpoint.api.model.mapper;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.modelmapper.TypeToken;

import com.mongodb.DBRef;
import com.mongodb.client.model.UpdateOptions;
import com.nowellpoint.api.model.document.ScheduledJobRequest;
import com.nowellpoint.api.model.document.UserRef;
import com.nowellpoint.api.model.domain.ScheduledJob;
import com.nowellpoint.api.model.domain.UserInfo;
import com.nowellpoint.util.Properties;
import com.nowellpoint.mongodb.document.MongoDatastore;

/**
 * 
 * 
 * @author jherson
 * 
 *
 */

public class ScheduledJobModelMapper extends AbstractModelMapper<com.nowellpoint.api.model.document.ScheduledJob> {

	/**
	 * 
	 * 
	 *
	 * 
	 */
	
	public ScheduledJobModelMapper() {
		super(com.nowellpoint.api.model.document.ScheduledJob.class);
	}

	protected ScheduledJob findScheduedJobById(String id) {
		com.nowellpoint.api.model.document.ScheduledJob document = find(id);
		return modelMapper.map(document, ScheduledJob.class);
	}
	
	protected void createScheduledJob(ScheduledJob scheduledJob) {
		scheduledJob.setCreatedBy(new UserInfo(getSubject()));
		scheduledJob.setLastModifiedBy(new UserInfo(getSubject()));
		com.nowellpoint.api.model.document.ScheduledJob document = modelMapper.map(scheduledJob, com.nowellpoint.api.model.document.ScheduledJob.class);
		create(document);
		hset(encode(getSubject()), document);
		submitScheduledJobRequest(document);
		modelMapper.map(document, scheduledJob);
	}
	
	protected void updateScheduledJob(ScheduledJob scheduledJob) {
		scheduledJob.setLastModifiedBy(new UserInfo(getSubject()));
		com.nowellpoint.api.model.document.ScheduledJob document = modelMapper.map(scheduledJob, com.nowellpoint.api.model.document.ScheduledJob.class);
		replace(document);
		hset(encode(getSubject()), document);
		submitScheduledJobRequest(document);
		modelMapper.map(document, scheduledJob);
	}
	
	protected void deleteScheduledJob(ScheduledJob scheduledJob) {
		com.nowellpoint.api.model.document.ScheduledJob document = modelMapper.map(scheduledJob, com.nowellpoint.api.model.document.ScheduledJob.class);
		delete(document);
		hdel(encode(getSubject()), document);
	}
	
	protected Set<ScheduledJob> findAllScheduled() {
		Set<com.nowellpoint.api.model.document.ScheduledJob> documents = super.find( eq ( "status", "Scheduled" ) );
		Set<ScheduledJob> scheduledJobs = modelMapper.map(documents, new TypeToken<HashSet<ScheduledJob>>() {}.getType());
		return scheduledJobs;
	}
	
	private void submitScheduledJobRequest(com.nowellpoint.api.model.document.ScheduledJob scheduledJob) {
		ZonedDateTime dateTime = ZonedDateTime.ofInstant(scheduledJob.getScheduleDate().toInstant(), ZoneId.of("UTC"));
		Date now = Date.from(Instant.now());
		
		String collectionName = MongoDatastore.getCollectionName( com.nowellpoint.api.model.document.AccountProfile.class );
		ObjectId id = new ObjectId( System.getProperty( Properties.DEFAULT_SUBJECT ) );

		DBRef reference = new DBRef( collectionName, id );
		
		UserRef userRef = new UserRef();
		userRef.setIdentity(reference);

		ScheduledJobRequest scheduledJobRequest = new ScheduledJobRequest();
		scheduledJobRequest.setScheduledJobId(scheduledJob.getId());
		scheduledJobRequest.setConnectorId(scheduledJob.getConnectorId());
		scheduledJobRequest.setConnectorType(scheduledJob.getConnectorType());
		scheduledJobRequest.setOwner(scheduledJob.getOwner());
		scheduledJobRequest.setCreatedDate(now);
		scheduledJobRequest.setCreatedBy(userRef);
		scheduledJobRequest.setDescription(scheduledJob.getDescription());
		scheduledJobRequest.setEnvironmentKey(scheduledJob.getEnvironmentKey());
		scheduledJobRequest.setEnvironmentName(scheduledJob.getEnvironmentName());
		scheduledJobRequest.setIsSandbox(scheduledJob.getIsSandbox());
		scheduledJobRequest.setJobTypeCode(scheduledJob.getJobTypeCode());
		scheduledJobRequest.setJobTypeId(scheduledJob.getJobTypeCode());
		scheduledJobRequest.setJobTypeName(scheduledJob.getJobTypeName());
		scheduledJobRequest.setLastModifiedDate(now);
		scheduledJobRequest.setLastModifiedBy(userRef);
		scheduledJobRequest.setNotificationEmail(scheduledJob.getNotificationEmail());
		scheduledJobRequest.setScheduleDate(scheduledJob.getScheduleDate());
		scheduledJobRequest.setStatus(scheduledJob.getStatus());
		scheduledJobRequest.setSystemCreatedDate(now);
		scheduledJobRequest.setSystemModifiedDate(now);
		scheduledJobRequest.setYear(dateTime.getYear());
		scheduledJobRequest.setMonth(dateTime.getMonth().getValue());
		scheduledJobRequest.setDay(dateTime.getDayOfMonth());
		scheduledJobRequest.setHour(dateTime.getHour());
		scheduledJobRequest.setMinute(dateTime.getMinute());
		scheduledJobRequest.setSecond(dateTime.getSecond());
		
		MongoDatastore.getCollection( ScheduledJobRequest.class ).replaceOne( and ( 
				eq ( "scheduledJobId", scheduledJob.getId().toString() ), 
				or ( eq ( "status", "Scheduled" ), eq ( "status", "Stopped" ))), 
				scheduledJobRequest, 
				new UpdateOptions().upsert(true));
	}
}