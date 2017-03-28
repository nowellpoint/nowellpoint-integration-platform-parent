package com.nowellpoint.api.job;

import static com.mongodb.client.model.Filters.eq;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import javax.annotation.PostConstruct;
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

import com.nowellpoint.api.model.document.Job;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;

@Singleton
@Startup
public class JobOperator {
	
	private static final Logger LOGGER = Logger.getLogger(JobOperator.class);
	
	private static Scheduler scheduler;
	
	@Inject
	private DocumentManagerFactory documentManagerFactory;
	
	@PostConstruct
	public void scheduleJobs() {
		
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		try {
			scheduler = schedulerFactory.getScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			LOGGER.error(e);
		}
		
		DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
		Set<com.nowellpoint.api.model.document.Job> documents = documentManager.find(
				com.nowellpoint.api.model.document.Job.class, 
				eq ( "status", "Scheduled" ) );
		
		documents.stream().forEach(job -> {
			LOGGER.info(job.getId());
			submitJob(job);
		});
		
	}
	
	public void submitJob(@Observes Job job) {
		
		try {
			Class <? extends org.quartz.Job> jobClass = Class.forName (job.getClassName()).asSubclass (org.quartz.Job.class);
			
			JobKey jobKey = new JobKey(job.getId().toString(), job.getJobName());

		    JobDetail jobDetail = JobBuilder.newJob(jobClass)
		    		.withIdentity(jobKey)
		    		.build();
		    
		    Calendar calendar = Calendar.getInstance();
			
			if ("RUN_WHEN_SUBMITTED".equals(job.getScheduleOption())) {
				
				calendar.setTime(Date.from(Instant.now()));
				
				job.setSeconds(String.valueOf(calendar.get(Calendar.SECOND)));
				job.setMinutes(String.valueOf(calendar.get(Calendar.MINUTE)));
				job.setHours(String.valueOf(calendar.get(Calendar.HOUR)));
				job.setMonth(String.valueOf(calendar.get(Calendar.MONTH)));
				job.setDayOfMonth(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
				job.setYear(String.valueOf(calendar.get(Calendar.YEAR)));
				job.setDayOfWeek(String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));
				
				Trigger trigger = TriggerBuilder.newTrigger().startNow().build();
				
				scheduler.scheduleJob(jobDetail, trigger);
			}
			
		} catch (ClassNotFoundException e) {
			LOGGER.error(e.getMessage());
			return;
		} catch (SchedulerException e) {
			LOGGER.error(e.getMessage());
			return;
		}
		
		job.setStatus("Submitted");
		
		DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
		documentManager.replaceOne(job);
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