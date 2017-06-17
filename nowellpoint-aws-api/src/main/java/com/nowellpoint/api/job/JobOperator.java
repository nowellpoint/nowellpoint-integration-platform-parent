/**
 * 
 * Copyright 2015-2016 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.nowellpoint.api.job;

import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerKey.triggerKey; 

import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.DateBuilder.IntervalUnit;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;

import org.jboss.logging.Logger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;

import com.nowellpoint.api.annotation.Stop;
import com.nowellpoint.api.annotation.Submit;
import com.nowellpoint.api.annotation.Terminate;
import com.nowellpoint.api.rest.domain.Job;
import com.nowellpoint.api.rest.domain.JobScheduleOptions;
import com.nowellpoint.util.Assert;

@Singleton
@Startup
public class JobOperator  {
	
	private static final Logger LOGGER = Logger.getLogger(JobOperator.class);
	
	private static Scheduler scheduler;
	
	@PostConstruct
	public void startJobOperator() {
		
		SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		try {
			scheduler = schedulerFactory.getScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			LOGGER.error(e);
		}	
	}
	
	@PreDestroy
	public void stopJobOperator() {
		shutdown();
	}
	
	public void unscheduleJob(@Observes @Stop Job job) {
		
		try {
			scheduler.unscheduleJob(triggerKey(job.getId().toString(), job.getScheduleOption()));
		} catch (SchedulerException e) {
			LOGGER.error(e);
		}
	}
	
	public void deleteJob(@Observes @Terminate Job job) {

		try {
			scheduler.deleteJob(jobKey(job.getId().toString(), job.getJobName()));
		} catch (SchedulerException e) {
			LOGGER.error(e);
		}
	}
	
	public void submitJob(@Observes @Submit Job job) {
		
		try {
			Class <? extends org.quartz.Job> jobClass = Class.forName (job.getClassName()).asSubclass (org.quartz.Job.class);
			
			JobKey jobKey = new JobKey(job.getId().toString(), job.getJobName());
			
			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put(job.getId(), job.toDocument());

		    JobDetail jobDetail = JobBuilder.newJob(jobClass)
		    		.withIdentity(jobKey)
		    		.usingJobData(jobDataMap)
		    		.build();
		    
		    TriggerKey triggerKey = new TriggerKey(job.getId().toString(), job.getScheduleOption());
		    
		    Trigger trigger = null;
			
			if (JobScheduleOptions.RUN_WHEN_SUBMITTED.equals(job.getScheduleOption()) || JobScheduleOptions.RUN_ONCE.equals(job.getScheduleOption())) {
				
				trigger = TriggerBuilder.newTrigger()
						.withIdentity(triggerKey)
						.startAt(job.getSchedule().getRunAt())
						.build();
				
			} else if (JobScheduleOptions.RUN_ON_SCHEDULE.equals(job.getScheduleOption())) {
				
				IntervalUnit intervalUnit = null;
				
				if (job.getSchedule().getTimeUnit() == TimeUnit.DAYS) {
					intervalUnit = IntervalUnit.DAY;
				} else if (job.getSchedule().getTimeUnit() == TimeUnit.HOURS) {
					intervalUnit = IntervalUnit.HOUR;
				} else if (job.getSchedule().getTimeUnit() == TimeUnit.MINUTES) {
					intervalUnit = IntervalUnit.MINUTE;
				} else if (job.getSchedule().getTimeUnit() == TimeUnit.SECONDS) {
					intervalUnit = IntervalUnit.SECOND;
				}
				
				trigger = TriggerBuilder.newTrigger()
						.withIdentity(triggerKey)
						.startAt(job.getSchedule().getRunAt())
						.endAt(job.getSchedule().getEndAt())
						.withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
								.withInterval(job.getSchedule().getTimeInterval(), intervalUnit)
								.inTimeZone(job.getSchedule().getTimeZone())
								.preserveHourOfDayAcrossDaylightSavings(Boolean.TRUE))
						.build();
				
			} else if (JobScheduleOptions.RUN_ON_SPECIFIC_DAYS.equals(job.getScheduleOption())) {
				
			} else {
				throw new IllegalArgumentException(String.format("Invalid Schedule Option: %s. Valid values are: RUN_WHEN_SUBMITTED, RUN_ONCE, RUN_ON_SCHEDULE and RUN_ON_SPECIFIC_DAYS", job.getScheduleOption()));
			}
			
			if (Assert.isNotNull(trigger)) {
				scheduler.scheduleJob(jobDetail, trigger);
				scheduler.getListenerManager().addJobListener(
						new SalesforceMetadataBackupListener(), 
						KeyMatcher.keyEquals(jobKey));
			}
			
		} catch (ClassNotFoundException | SchedulerException | IllegalArgumentException e) {
			LOGGER.error(e.getMessage());
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