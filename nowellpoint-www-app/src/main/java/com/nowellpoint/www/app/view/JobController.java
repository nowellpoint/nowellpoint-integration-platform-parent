package com.nowellpoint.www.app.view;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Job;
import com.nowellpoint.client.model.JobList;
import com.nowellpoint.client.model.JobRequest;
import com.nowellpoint.client.model.Token;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class JobController extends AbstractStaticController {
	
	public static class Template {
		public static final String JOBS_LIST = String.format(APPLICATION_CONTEXT, "jobs-list.html");
	}
	
	public static String listJobs(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		JobList jobList = new NowellpointClient(token)
				.job()
				.getJobs();
		
		Map<String, Object> model = getModel();
    	model.put("jobList", jobList.getItems());
    	
    	return render(JobController.class, configuration, request, response, model, Template.JOBS_LIST);
	}
	
	public static String viewJob(Configuration configuration, Request request, Response response) {
		return null;
	}
	
	public static String createJob(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		Identity identity = getIdentity(request);
		
		String jobName = request.queryParams("jobName");
		String notificationEmail = request.queryParams("notificationEmail");
		String description = request.queryParams("description");
		String scheduleOption = request.queryParams("scheduleOption");
		String startDate = request.queryParams("startDate");
		
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		JobRequest jobRequest = null;
		
		if ("RUN_WHEN_SUBMITTED".equals(scheduleOption)) {
			
			jobRequest = new JobRequest().withJobName(jobName)
					.withDescription(description)
					.withTimeZone(identity.getTimeZoneSidKey())
					.withNotificationEmail(notificationEmail)
					.withScheduleOption(scheduleOption)
					.withStart(Date.from(Instant.now()))
					.withEnd(Date.from(Instant.now()));
			
		} else if ("ONCE".equals(scheduleOption)) {
			
			Date date = null;
			try {
				date = dateTimeFormat.parse(startDate);
			} catch (ParseException e) {
				response.status(400);
				return showError(e.getLocalizedMessage());
			}
			
			jobRequest = new JobRequest().withJobName(jobName)
					.withDescription(description)
					.withTimeZone(identity.getTimeZoneSidKey())
					.withNotificationEmail(notificationEmail)
					.withScheduleOption(scheduleOption)
					.withStart(date)
					.withEnd(date);
			
		} else if ("SCHEDULE".equals(scheduleOption)) {
			
		} else if ("SPECIFIC_DAYS".equals(scheduleOption)) {
			
		} else {
			
		}
		
		CreateResult<Job> createRequest = new NowellpointClient(token)
				.job()
				.create(jobRequest);
		
		if (! createRequest.isSuccess()) {
			response.status(400);
		}
		
		return "";
	}
	
	public static String updateJob(Configuration configuration, Request request, Response response) {
		return null;
	}
}