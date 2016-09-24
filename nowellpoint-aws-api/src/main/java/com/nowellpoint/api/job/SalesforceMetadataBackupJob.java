package com.nowellpoint.api.job;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nowellpoint.api.model.document.ScheduledJob;
import com.nowellpoint.mongodb.document.MongoDocumentService;

public class SalesforceMetadataBackupJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		ScheduledJobService service = new ScheduledJobService();
		
		Set<ScheduledJob> scheduledJobs = service.getScheduledJobs();
		
		if (scheduledJobs.size() > 0) {
			ExecutorService executor = Executors.newFixedThreadPool(scheduledJobs.size());
			
		    scheduledJobs.stream().forEach(scheduledJob -> {
		    	
		    	executor.submit(() -> {
		    		scheduledJob.setStatus("Running");
		    		scheduledJob.setScheduleDate(Date.from(ZonedDateTime.ofInstant(scheduledJob.getScheduleDate().toInstant(), ZoneId.of("UTC")).plusDays(1).toInstant()));
			    	scheduledJob.setLastRunStatus("Success");
			    	scheduledJob.setLastRunDate(Date.from(Instant.now()));
			    	
			    	service.replace(scheduledJob);
			    	
			    	ZonedDateTime dateTime = ZonedDateTime.ofInstant(scheduledJob.getScheduleDate().toInstant(), ZoneId.of("UTC"));
			    	scheduledJob.setYear(dateTime.getYear());
			    	scheduledJob.setMonth(dateTime.getMonth().getValue());
			    	scheduledJob.setDay(dateTime.getDayOfMonth());
			    	scheduledJob.setHour(dateTime.getHour());
			    	scheduledJob.setMinute(dateTime.getMinute());
			    	scheduledJob.setSecond(dateTime.getSecond());
			    	scheduledJob.setStatus("Scheduled");
			    	
			    	service.replace(scheduledJob);
		    	});
		    });
		}
	}
}

class ScheduledJobService extends MongoDocumentService<ScheduledJob> {

	public ScheduledJobService() {
		super(ScheduledJob.class);
	}
	
	public ScheduledJob replace(ScheduledJob scheduledJob) {
		return super.replace(scheduledJob.getOwner().getIdentity().getId().toString(), scheduledJob);
	}
	
	public Set<ScheduledJob> getScheduledJobs() {
		return super.find( and ( 
				eq ( "status", "Scheduled" ), 
				eq ( "jobTypeCode", "SALESFORCE_METADATA_BACKUP" ) ) );
	}
}