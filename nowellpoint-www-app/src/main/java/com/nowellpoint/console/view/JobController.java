package com.nowellpoint.console.view;

import java.util.Map;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Job;
import com.nowellpoint.client.model.JobList;
import com.nowellpoint.client.model.JobRequest;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.www.app.util.TemplateBuilder;

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
		
		JobList jobList = NowellpointClient.defaultClient(token)
				.job()
				.getJobs();
		
		Map<String, Object> model = getModel();
    	model.put("jobList", jobList.getItems());
    	
    	return TemplateBuilder.template()
				.configuration(configuration)
				.controllerClass(JobController.class)
				.identity(getIdentity(request))
				.locale(getLocale(request))
				.model(model)
				.templateName(Template.JOBS_LIST)
				.timeZone(getTimeZone(request))
				.build();
	}
	
	public static String viewJob(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		Job job = NowellpointClient.defaultClient(token)
			.job()
			.get(id);
		
		Map<String, Object> model = getModel();
		model.put("job", job);
		
		return TemplateBuilder.template()
				.configuration(configuration)
				.controllerClass(JobController.class)
				.identity(getIdentity(request))
				.locale(getLocale(request))
				.model(model)
				.templateName(Template.JOBS_VIEW)
				.timeZone(getTimeZone(request))
				.build();
	}
	
	public static String runJob(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		UpdateResult<Job> result = NowellpointClient.defaultClient(token)
			.job()
			.run(id);
		
		Map<String, Object> model = getModel();
		model.put("job", result.getTarget());
		
		return TemplateBuilder.template()
				.configuration(configuration)
				.controllerClass(JobController.class)
				.identity(getIdentity(request))
				.locale(getLocale(request))
				.model(model)
				.templateName(Template.JOBS_VIEW)
				.timeZone(getTimeZone(request))
				.build();
	}
	
	public static String stopJob(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		UpdateResult<Job> result = NowellpointClient.defaultClient(token)
			.job()
			.stop(id);
		
		Map<String, Object> model = getModel();
		model.put("job", result.getTarget());
		
		return TemplateBuilder.template()
				.configuration(configuration)
				.controllerClass(JobController.class)
				.identity(getIdentity(request))
				.locale(getLocale(request))
				.model(model)
				.templateName(Template.JOBS_VIEW)
				.timeZone(getTimeZone(request))
				.build();
	}
	
	public static String terminateJob(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		UpdateResult<Job> result = NowellpointClient.defaultClient(token)
			.job()
			.terminate(id);
		
		Map<String, Object> model = getModel();
		model.put("job", result.getTarget());
		
		return TemplateBuilder.template()
				.configuration(configuration)
				.controllerClass(JobController.class)
				.identity(getIdentity(request))
				.locale(getLocale(request))
				.model(model)
				.templateName(Template.JOBS_VIEW)
				.timeZone(getTimeZone(request))
				.build();
	}
	
	public static String viewOutputs(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		Job job = NowellpointClient.defaultClient(token)
			.job()
			.get(id);
		
		Map<String, Object> model = getModel();
		model.put("job", job);
		
		return TemplateBuilder.template()
				.configuration(configuration)
				.controllerClass(JobController.class)
				.identity(getIdentity(request))
				.locale(getLocale(request))
				.model(model)
				.templateName(Template.JOBS_OUTPUTS)
				.timeZone(getTimeZone(request))
				.build();
	}
	
	public static String testWebhookUrl(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		UpdateResult<Job> updateResult = NowellpointClient.defaultClient(token)
				.job()
				.testWebHookUrl(id);
		
		System.out.println(updateResult.isSuccess());
		
		response.status(204);
		return "";
	}
	
	public static String submitJob(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		UpdateResult<Job> updateResult = NowellpointClient.defaultClient(token)
				.job()
				.submit(id);
		
		if (! updateResult.isSuccess()) {
			response.status(400);
		}
		
		return responseBody(updateResult);
	}
	
	public static String downloadOutputFile(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String filename = request.queryParams("filename");
		
		String content = NowellpointClient.defaultClient(token)
				.job()
				.downloadOutputFile(id, filename);
		
		return content;
	}
	
	public static String createJob(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		Identity identity = getIdentity(request);
		
		String connectorId = request.params(":connectorId");
		String jobTypeId = request.queryParams("jobTypeId");
		String notificationEmail = request.queryParams("notificationEmail");
		String slackWebhookUrl = request.queryParams("slackWebhookUrl");
		String description = request.queryParams("description");
		String scheduleOption = request.queryParams("scheduleOption");
		String runAt = request.queryParams("runAt");
		String startAt = request.queryParams("startAt");
		String endAt = request.queryParams("endAt");
		String timeInterval = request.queryParams("timeInterval");
		String timeUnit = request.queryParams("timeUnit");
		
		JobRequest jobRequest = new JobRequest().withConnectorId(connectorId)
				.withRunAt(runAt)
				.withJobTypeId(jobTypeId)
				.withDescription(description)
				.withTimeZone(identity.getTimeZone())
				.withNotificationEmail(notificationEmail)
				.withSlackWebhookUrl(slackWebhookUrl)
				.withScheduleOption(scheduleOption)
				.withStartAt(startAt)
				.withEndAt(endAt)
				.withTimeUnit(timeUnit)
				.withTimeInterval(timeInterval);
		
		CreateResult<Job> createResult = NowellpointClient.defaultClient(token)
				.job()
				.create(jobRequest);
		
		if (! createResult.isSuccess()) {
			response.status(400);
			return showError(createResult.getErrorMessage());
		}
		
		return "";
	}
	
	public static String updateJob(Configuration configuration, Request request, Response response) {		
		Token token = getToken(request);
		Identity identity = getIdentity(request);
		
		String id = request.params(":id");

		String notificationEmail = request.queryParams("notificationEmail");
		String slackWebhookUrl = request.queryParams("slackWebhookUrl");
		String description = request.queryParams("description");
		//String scheduleOption = request.queryParams("scheduleOption");
		//String runAt = request.queryParams("runAt");
		//String startAt = request.queryParams("startAt");
		//String endAt = request.queryParams("endAt");
		//String timeInterval = request.queryParams("timeInterval");
		//String timeUnit = request.queryParams("timeUnit");
		
		JobRequest jobRequest = new JobRequest()
				//.withRunAt(runAt)
				.withDescription(description)
				.withTimeZone(identity.getTimeZone())
				.withNotificationEmail(notificationEmail)
				.withSlackWebhookUrl(slackWebhookUrl);
				//.withScheduleOption(scheduleOption)
				//.withStartAt(startAt)
				//.withEndAt(endAt)
				//.withTimeUnit(timeUnit)
				//.withTimeInterval(timeInterval);
		
		UpdateResult<Job> updateResult = NowellpointClient.defaultClient(token)
				.job()
				.update(id, jobRequest);
		
		if (! updateResult.isSuccess()) {
			response.status(400);
		}
		
		return responseBody(updateResult);
	}
}