 package com.nowellpoint.api.job;

import static org.quartz.DateBuilder.evenMinuteDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;
import java.util.Set;

import org.jboss.logging.Logger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;

import com.nowellpoint.api.rest.domain.JobSchedule;
import com.nowellpoint.api.rest.service.JobScheduleServiceImpl;
import com.nowellpoint.api.service.JobScheduleService;

public class JobSchedulerManager2 {
	
	private static final Logger LOGGER = Logger.getLogger(JobSchedulerManager2.class);
	
    private static Scheduler scheduler;

	public void contextDestroyed() {
		shutdown();
	}

	public void contextInitialized() {
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		
		Date runTime = evenMinuteDate(new Date());
		
		JobScheduleService jobScheduleService = new JobScheduleServiceImpl();
		Set<JobSchedule> jobSchedules = jobScheduleService.findScheduled().getItems();
		
		jobSchedules.stream().forEach(job -> {
			
			JobKey jobKey = new JobKey(job.getId(), job.getJobType().getCode());
			
			JobDetail jobDetail = newJob(SalesforceMetadataBackupJob2.class)
					.withIdentity(jobKey)
		    		.build();
			
			//CronScheduleBuilder.cronSchedule(cronExpression);
		    	
		    Trigger trigger = newTrigger()
		    		.withIdentity("EXECUTE_EVERY_MINUTE", job.getId())
		    		.startAt(runTime)
		    		.withSchedule(SimpleScheduleBuilder.simpleSchedule()
		    				.withIntervalInMinutes(1)
		    				.repeatForever())
		    		.build();
		    	
		    try {
				scheduler = schedulerFactory.getScheduler();
				scheduler.scheduleJob(jobDetail, trigger);
			} catch (SchedulerException e) {
				LOGGER.error(e);
			} 
		});
		
		try {
			scheduler.start();
		} catch (SchedulerException e) {
			LOGGER.error(e);
		}  
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