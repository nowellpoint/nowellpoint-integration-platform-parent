package com.nowellpoint.api.job;

import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.DateBuilder.IntervalUnit;

import java.sql.Date;
import java.time.Instant;
import java.util.TimeZone;

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
		    
		    Trigger trigger = null;
			
			if (Job.ScheduleOptions.RUN_WHEN_SUBMITTED.equals(job.getScheduleOption()) || Job.ScheduleOptions.RUN_ONCE.equals(job.getScheduleOption())) {
				
				trigger = TriggerBuilder
						.newTrigger()
						.startAt(job.getSchedule().getRunAt())
						.build();
				
			} else if (Job.ScheduleOptions.RUN_ON_SCHEDULE.equals(job.getScheduleOption())) {
				
				trigger = TriggerBuilder.newTrigger()
						.startAt(job.getSchedule().getStartAt())
						.endAt(job.getSchedule().getEndAt())
						.withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
								.withInterval(job.getSchedule().getTimeInterval(), IntervalUnit.valueOf(job.getSchedule().getTimeUnit()))
								.inTimeZone(TimeZone.getTimeZone(job.getSchedule().getTimeZone())))
						.build();
				
			} else if (Job.ScheduleOptions.RUN_ON_SPECIFIC_DAYS.equals(job.getScheduleOption())) {
				
			} else {
				throw new IllegalArgumentException(String.format("Invalid Schedule Option: %s. Valid values are: RUN_WHEN_SUBMITTED, ONCE, SCHEDULE and SPECIFIC_DAYS", job.getScheduleOption()));
			}
			
			if (Assert.isNotNull(trigger)) {
				scheduler.scheduleJob(jobDetail, trigger);
				scheduler.getListenerManager().addJobListener(
						new SalesforceMetadataBackupListener(), 
						KeyMatcher.keyEquals(jobKey));
			}
			
			if (trigger.getStartTime().after(Date.from(Instant.now()))) {
				job.setStatus(Job.Statuses.SCHEDULED);
			} else {
				job.setStatus(Job.Statuses.SUBMITTED);
			}
			
		} catch (ClassNotFoundException | SchedulerException | IllegalArgumentException e) {
			LOGGER.error(e.getMessage());
			job.setStatus(Job.Statuses.ERROR);
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