package com.nowellpoint.api.job;

import java.sql.Date;
import java.time.Instant;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;

import com.nowellpoint.api.rest.domain.Job;
import com.nowellpoint.api.rest.domain.JobList;
import com.nowellpoint.api.service.JobService;
import com.nowellpoint.util.Assert;

@Singleton
@Startup
public class JobOperator  {
	
	private static final Logger LOGGER = Logger.getLogger(JobOperator.class);
	
	private static Scheduler scheduler;
	
	@Inject
	private JobService jobService;
	
	@PostConstruct
	public void startJobOperator() {
		
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		try {
			scheduler = schedulerFactory.getScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			LOGGER.error(e);
		}
		
		JobList jobList = jobService.findAllScheduled();
		
		jobList.getItems().stream().forEach(job -> {
			LOGGER.info(job.getId());
			submitJob(job);
		});
		
	}
	
	@PreDestroy
	public void stopJobOperator() {
		shutdown();
	}
	
	public void submitJob(@Observes Job job) {
		
		try {
			Class <? extends org.quartz.Job> jobClass = Class.forName (job.getClassName()).asSubclass (org.quartz.Job.class);
			
			JobKey jobKey = new JobKey(job.getId().toString(), job.getJobName());

		    JobDetail jobDetail = JobBuilder.newJob(jobClass)
		    		.withIdentity(jobKey)
		    		.build();
		    
		    Trigger trigger = TriggerBuilder
					.newTrigger()
					.startAt(job.getSchedule().getRunAt())
					.build();
		    
		    if (job.getSchedule().getRunAt().after(Date.from(Instant.now()))) {
		    	job.setStatus("Scheduled");
		    } else {
		    	job.setStatus("Submitted");
		    }
			
//			if (Job.ScheduleOptions.RUN_WHEN_SUBMITTED.equals(job.getScheduleOption())) {
//				
//				trigger = TriggerBuilder
//						.newTrigger()
//						.startNow()
//						.build();
//				
//				job.getSchedule().setRunAt(Date.from(Instant.now()));
//				job.setStatus("Submitted");
//				
//			} else if (Job.ScheduleOptions.ONCE.equals(job.getScheduleOption())) {
//				
//				if (job.getSchedule().getRunAt().before(Date.from(Instant.now().plusSeconds(5)))) {
//					
//					trigger = TriggerBuilder
//							.newTrigger()
//							.startNow()
//							.build();
//					
//					job.setStatus("Submitted");
//					
//				} else {
//					
//					trigger = TriggerBuilder
//							.newTrigger()
//							.startAt(job.getSchedule().getRunAt())
//							.build();
//					
//					job.setStatus("Scheduled");
//				}
//				
//			} else if (Job.ScheduleOptions.SCHEDULE.equals(job.getScheduleOption())) {
//				
//			} else if (Job.ScheduleOptions.SPECIFIC_DAYS.equals(job.getScheduleOption())) {
//				
//			} else {
//				throw new IllegalArgumentException(String.format("Invalid Schedule Option: %s. Valid values are: RUN_WHEN_SUBMITTED, ONCE, SCHEDULE and SPECIFIC_DAYS", job.getScheduleOption()));
//			}
			
			if (Assert.isNotNull(trigger)) {
				scheduler.scheduleJob(jobDetail, trigger);
				scheduler.getListenerManager().addJobListener(
						new SalesforceMetadataBackupListener(), 
						KeyMatcher.keyEquals(jobKey));
			}
			
		} catch (ClassNotFoundException | SchedulerException | IllegalArgumentException e) {
			LOGGER.error(e.getMessage());
			job.setStatus("Not Submitted");
			job.setFailureMessage(e.getMessage());
		}
		
		jobService.updateJob(job);
	}
	
	public static void pause() {
		try {
			scheduler.pauseAll();
		} catch (SchedulerException e) {
			LOGGER.error(e);
		}
	}
	
	public static void shutdown() {
		try {
			scheduler.shutdown(true);
		} catch (SchedulerException e) {
			LOGGER.error(e);
		}
	}
	
	public static void resume() {
		try {
			scheduler.resumeAll();
		} catch (SchedulerException e) {
			LOGGER.error(e);
		}
	}
	
	public static void start() {
		try {
			scheduler.start();
		} catch (SchedulerException e) {
			LOGGER.error(e);
		}
	}
}