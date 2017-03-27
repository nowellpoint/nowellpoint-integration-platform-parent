package com.nowellpoint.api.job;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;

import java.io.IOException;
import java.util.Calendar;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.api.model.document.Job;
import com.nowellpoint.mongodb.Datastore;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.util.Properties;

@Singleton
@Startup
public class JobOperator { //implements MessageListener {
	
	private static final Logger LOGGER = Logger.getLogger(JobOperator.class);
	
	@Resource
	TimerService timerService;
	
	@Inject
	DocumentManagerFactory documentManagerFactory;
	
	@PostConstruct
	public void scheduleJobs() {
		
		DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
		Set<com.nowellpoint.api.model.document.Job> documents = documentManager.find(
				com.nowellpoint.api.model.document.Job.class, 
				eq ( "status", "Scheduled" ) );
		
		documents.stream().forEach(job -> {
			LOGGER.info(job.getId());
			submitJob(job);
		});
		
	}
	
	
	
	public void jobEventObserver(@Observes Job job) {
		
		//DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 


		submitJob(job);
	}
	
	public void submitJob(Job job) {
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(job.getId());
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(job.getStart());
		calendar.add(Calendar.SECOND, 30);
		
		if ("RUN_WHEN_SUBMITTED".equals(job.getScheduleOption())) {
			job.setSeconds(String.valueOf(calendar.get(Calendar.SECOND)));
			job.setMinutes(String.valueOf(calendar.get(Calendar.MINUTE)));
			job.setHours(String.valueOf(calendar.get(Calendar.HOUR)));
			job.setMonth(String.valueOf(calendar.get(Calendar.MONTH)));
			job.setDayOfMonth(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
			job.setYear(String.valueOf(calendar.get(Calendar.YEAR)));
			job.setDayOfWeek(String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));
		}
		
		ScheduleExpression expression = new ScheduleExpression()
				.dayOfMonth(job.getDayOfMonth())
				.dayOfWeek(job.getDayOfWeek())
				.hour(job.getHours())
				.minute(job.getMinutes())
				.month(job.getMonth())
				.second(job.getSeconds())
				.year(job.getYear())
				.start(job.getStart())
				.end(job.getEnd())
				.timezone(job.getTimeZone());
		
		Timer timer = timerService.createCalendarTimer(expression, timerConfig);
		
		job.setStatus("Submitted");
		
		DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
		documentManager.replaceOne(job);
	}
	
	@Timeout 
	public void execute(Timer timer) {
		ObjectId jobId = (ObjectId) timer.getInfo();
		System.out.println(jobId);
		DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
		Job job = documentManager.fetch(Job.class, jobId);
		System.out.println(job.getJobName());
	}



//	@Override
//	public void onMessage(Message message) {
//		TextMessage textMessage = (TextMessage) message;
//		try {
//			JsonNode json = new ObjectMapper().readValue(textMessage.getText(), JsonNode.class);
//			System.out.println(json);
//			String id = json.get("id").asText();
//			DocumentManagerFactory documentManagerFactory = Datastore.createDocumentManagerFactory(System.getProperty(Properties.MONGO_CLIENT_URI));
//			DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
//			Job job = documentManager.fetch(Job.class, new ObjectId(id));
//			submitJob(job);
//			message.acknowledge();
//			documentManagerFactory.close();
//		} catch (IOException | JMSException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
}