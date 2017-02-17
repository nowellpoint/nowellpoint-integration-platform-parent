package com.nowellpoint.api.job;

import static com.mongodb.client.model.Filters.eq;

import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.bson.types.ObjectId;

import com.nowellpoint.api.model.document.Job;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;

@Singleton
@Startup
public class JobOperator {
	
	@Resource
	TimerService timerService;
	
	@Inject
	private DocumentManagerFactory documentManagerFactory;
	
	@PostConstruct
	public void scheduleJobs() {
		
		DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
		Set<com.nowellpoint.api.model.document.Job> documents = documentManager.find(
				com.nowellpoint.api.model.document.Job.class, 
				eq ( "status", "scheduled" ) );
		
		documents.stream().forEach(job -> {
			TimerConfig timerConfig = new TimerConfig();
			timerConfig.setInfo(job.getId());
			
			ScheduleExpression expression = new ScheduleExpression()
					.dayOfMonth(job.getDayOfMonth())
					.dayOfWeek(job.getDayOfWeek())
					.hour(job.getHours())
					.minute(job.getMinutes())
					.month(job.getMonth())
					.second(job.getSeconds())
					.year(job.getYear())
					.timezone(TimeZone.getDefault().getID());
			
			System.out.println(expression.toString());
			
			timerService.createCalendarTimer(expression, timerConfig);
			
		});
		
	}
	
	@Timeout 
	public void execute(Timer timer) {
		ObjectId jobId = (ObjectId) timer.getInfo();
		System.out.println(jobId);
		DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
		Job job = documentManager.fetch(Job.class, jobId);
		System.out.println(job.getJobName());
	}
}