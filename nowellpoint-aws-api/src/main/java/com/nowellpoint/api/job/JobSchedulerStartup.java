package com.nowellpoint.api.job;

import static org.quartz.DateBuilder.evenMinuteDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

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
public class JobSchedulerStartup implements ServletContextListener {
	
    private Scheduler scheduler;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		
		try {
			
			scheduler = schedulerFactory.getScheduler();
		    scheduler.start();
		     	       
		    Date runTime = evenMinuteDate(new Date());
		    
		    Trigger trigger = newTrigger()
		    		.withIdentity("trigger1", "group1")
		    		.startAt(runTime)
		    		.withSchedule(SimpleScheduleBuilder.simpleSchedule()
		    				.withIntervalInMinutes(1)
		    				.repeatForever())
		    		.build();
		    
		    JobKey jobKey = new JobKey("SALESFORCE_METADATA_BACKUP", "group1");

		    JobDetail job1 = newJob(SalesforceMetadataBackupJob.class)
		    		.withIdentity(jobKey)
		    		.build();
		    
		    scheduler.getListenerManager().addJobListener(
		    		new SalesforceMetadataBackupListener(), KeyMatcher.keyEquals(jobKey)
		    );
		                           
		    scheduler.scheduleJob(job1, trigger); 
		    
		} catch (SchedulerException e) {
			e.printStackTrace();
		}	
	}
}