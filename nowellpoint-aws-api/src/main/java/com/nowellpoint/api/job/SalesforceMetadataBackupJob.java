package com.nowellpoint.api.job;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nowellpoint.api.model.document.ScheduledJob;
import com.nowellpoint.mongodb.document.MongoDatastore;

public class SalesforceMetadataBackupJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		Set<ScheduledJob> scheduledJobs = MongoDatastore.find( ScheduledJob.class, and ( 
				eq ( "status", "Scheduled" ), 
				eq ( "jobTypeCode", "SALESFORCE_METADATA_BACKUP" ) ) );
		
	    scheduledJobs.stream().forEach(scheduledJob -> {
	    	System.out.println(scheduledJob.getId());
	    	scheduledJob.setScheduleDate(Date.from(scheduledJob.getScheduleDate().toInstant().plus(1, ChronoUnit.DAYS)));
	    	MongoDatastore.replaceOne( scheduledJob );
	    	
	    });
	}
}