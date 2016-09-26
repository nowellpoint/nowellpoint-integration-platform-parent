package com.nowellpoint.api.job;

import static org.quartz.DateBuilder.evenMinuteDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.jboss.logging.Logger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;

@WebListener
public class JobSchedulerManager implements ServletContextListener {
	
	private static final Logger LOG = Logger.getLogger(JobSchedulerManager.class);
	
    private static Scheduler scheduler;

	@Override
	public void contextDestroyed(ServletContextEvent contextEvent) {
		shutdown();
	}

	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		try {
			SchedulerFactory schedulerFactory = new StdSchedulerFactory();
			
		    Date runTime = evenMinuteDate(new Date());
		    
		    Trigger trigger = newTrigger()
		    		.withIdentity("EXECUTE_EVERY_MINUTE", "SALESFORCE_METADATA_BACKUP")
		    		.startAt(runTime)
		    		.withSchedule(SimpleScheduleBuilder.simpleSchedule()
		    				.withIntervalInMinutes(1)
		    				.repeatForever())
		    		.build();
		    
		    JobKey jobKey = new JobKey("SALESFORCE_METADATA_BACKUP", "SALESFORCE");

		    JobDetail jobDetail = newJob(SalesforceMetadataBackupJob.class)
		    		.withIdentity(jobKey)
		    		.build();
		    
		    scheduler = schedulerFactory.getScheduler();
		    scheduler.getListenerManager().addJobListener(new SalesforceMetadataBackupListener(), KeyMatcher.keyEquals(jobKey));
		    scheduler.scheduleJob(jobDetail, trigger); 
		    scheduler.start();
		    
		} catch (SchedulerException e) {
			LOG.error(e);
		}	
	}
	
	public static void pause() {
		try {
			scheduler.pauseAll();
		} catch (SchedulerException e) {
			LOG.error(e);
		}
	}
	
	public static void shutdown() {
		try {
			scheduler.shutdown(true);
		} catch (SchedulerException e) {
			LOG.error(e);
		}
	}
	
	public static void resume() {
		try {
			scheduler.resumeAll();
		} catch (SchedulerException e) {
			LOG.error(e);
		}
	}
	
	public static void start() {
		try {
			scheduler.start();
		} catch (SchedulerException e) {
			LOG.error(e);
		}
	}
}