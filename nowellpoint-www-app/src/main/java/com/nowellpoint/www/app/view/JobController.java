package com.nowellpoint.www.app.view;

import java.text.ParseException;
import java.util.Map;

import com.nowellpoint.client.NowellpointClientOrig;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Job;
import com.nowellpoint.client.model.JobExecution;
import com.nowellpoint.client.model.JobList;
import com.nowellpoint.client.model.JobRequest;
import com.nowellpoint.client.model.Token;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class JobController extends AbstractStaticController {
	
	public static class Template {
		public static final String JOBS_LIST = String.format(APPLICATION_CONTEXT, "jobs-list.html");
		public static final String JOBS_VIEW = String.format(APPLICATION_CONTEXT, "jobs-view.html");
		public static final String JOBS_OUTPUTS = String.format(APPLICATION_CONTEXT, "jobs-outputs.html");
	}
	
	public static String listJobs(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		JobList jobList = new NowellpointClientOrig(token)
				.job()
				.getJobs();
		
		Map<String, Object> model = getModel();
    	model.put("jobList", jobList.getItems());
    	
    	return render(JobController.class, configuration, request, response, model, Template.JOBS_LIST);
	}
	
	public static String viewJob(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		Job job = new NowellpointClientOrig(token)
			.job()
			.get(id);
		
		Map<String, Object> model = getModel();
		model.put("job", job);
		
		return render(JobController.class, configuration, request, response, model, Template.JOBS_VIEW);
	}
	
	public static String runJob(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		Job job = new NowellpointClientOrig(token)
				.job()
				.run(id);
		
		Map<String, Object> model = getModel();
		model.put("job", job);
		
		return render(JobController.class, configuration, request, response, model, Template.JOBS_VIEW);
	}
	
	public static String downloadOutputFile(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String fireInstanceId = request.params(":fireInstanceId");
		String filename = request.params(":filename");
		
		String content = new NowellpointClientOrig(token)
				.job()
				.jobExecution()
				.downloadOutputFile(id, fireInstanceId, filename);
		
		return content;
	}
	
	public static String createJob(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		Identity identity = getIdentity(request);
		
		String connectorId = request.params(":connectorId");
		String jobTypeId = request.queryParams("jobTypeId");
		String notificationEmail = request.queryParams("notificationEmail");
		String description = request.queryParams("description");
		String scheduleOption = request.queryParams("scheduleOption");
		String runAt = request.queryParams("runAt");
		String start = request.queryParams("start");
		String end = request.queryParams("end");
		
		try {

			JobRequest jobRequest = new JobRequest().withConnectorId(connectorId)
					.withRunAt(runAt)
					.withJobTypeId(jobTypeId)
					.withDescription(description)
					.withTimeZone(identity.getTimeZoneSidKey())
					.withNotificationEmail(notificationEmail)
					.withScheduleOption(scheduleOption)
					.withStart(start)
					.withEnd(end);
			
			CreateResult<Job> createRequest = new NowellpointClientOrig(token)
					.job()
					.create(jobRequest);
			
			if (! createRequest.isSuccess()) {
				response.status(400);
				return showError(createRequest.getErrorMessage());
			}
		
		} catch (ParseException e) {
			response.status(400);
			return showError(e.getLocalizedMessage());
		}
		
		return "";
	}
	
	public static String viewOutputs(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String fireInstanceId = request.params(":fireInstanceId");
		
		JobExecution jobExecution = new NowellpointClientOrig(token)
			.job()
			.jobExecution()
			.get(id, fireInstanceId);
		
		Map<String, Object> model = getModel();
		model.put("jobId", id);
		model.put("jobExecution", jobExecution);
		return render(JobController.class, configuration, request, response, model, Template.JOBS_OUTPUTS);
	}
	
	public static String updateJob(Configuration configuration, Request request, Response response) {
		return null;
	}
}