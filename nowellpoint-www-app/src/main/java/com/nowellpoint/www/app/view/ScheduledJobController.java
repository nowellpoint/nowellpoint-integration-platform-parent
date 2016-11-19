package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.TokenCredentials;
import com.nowellpoint.client.model.AccountProfile;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.Environment;
import com.nowellpoint.client.model.GetResult;
import com.nowellpoint.client.model.RunHistory;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.ScheduledJob;
import com.nowellpoint.client.model.ScheduledJobType;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.ScheduledJobRequest;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.Path;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class ScheduledJobController extends AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(ScheduledJobController.class.getName());
	
	public static class Template {
		public static final String SCHEDULED_JOBS_LIST = String.format(APPLICATION_CONTEXT, "scheduled-jobs-list.html");
		public static final String SCHEDULED_JOB_SELECT = String.format(APPLICATION_CONTEXT, "scheduled-job-create.html");
		public static final String SCHEDULED_JOB_EDIT = String.format(APPLICATION_CONTEXT, "scheduled-job-edit.html");
		public static final String SCHEDULED_JOB = String.format(APPLICATION_CONTEXT, "scheduled-job.html");
		public static final String SCHEDULE_JOB_RUN_HISTORY = String.format(APPLICATION_CONTEXT, "scheduled-job-run-history.html");
		public static final String SCHEDULE = String.format(APPLICATION_CONTEXT, "schedule.html");
	}

	public ScheduledJobController() {
		super(ScheduledJobController.class);
	}
	
	@Override
	public void configureRoutes(Configuration configuration) {
        get(Path.Route.SCHEDULED_JOBS_LIST, (request, response) -> getScheduledJobs(configuration, request, response));
        get(Path.Route.SCHEDULED_JOB_SELECT_TYPE, (request, response) -> selectType(configuration, request, response));
        get(Path.Route.SCHEDULED_JOB_SELECT_CONNECTOR, (request, response) -> selectConnector(configuration, request, response));
        get(Path.Route.SCHEDULED_JOB_SELECT_ENVIRONMENT, (request, response) -> selectEnvironment(configuration, request, response));
        get(Path.Route.SCHEDULED_JOB_SET_SCHEDULE, (request, response) -> setSchedule(configuration, request, response));
        post(Path.Route.SCHEDULED_JOB_CREATE, (request, response) -> createScheduledJob(configuration, request, response));
        get(Path.Route.SCHEDULED_JOB_VIEW, (request, response) -> viewScheduledJob(configuration, request, response));
        get(Path.Route.SCHEDULED_JOB_EDIT, (request, response) -> editScheduledJob(configuration, request, response));
        post(Path.Route.SCHEDULED_JOB_UPDATE, (request, response) -> updateScheduledJob(configuration, request, response));
        post(Path.Route.SCHEDULED_JOB_START, (request, response) -> startScheduledJob(configuration, request, response));
        post(Path.Route.SCHEDULED_JOB_STOP, (request, response) -> stopScheduledJob(configuration, request, response));
        post(Path.Route.SCHEDULED_JOB_TERMINATE, (request, response) -> terminateScheduledJob(configuration, request, response));
        get(Path.Route.SCHEDULED_JOB_RUN_HISTORY, (request, response) -> getRunHistory(configuration, request, response));
        get(Path.Route.SCHEDULED_JOB_DOWNLOAD_FILE, (request, response) -> downloadFile(configuration, request, response));
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String getScheduledJobs(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		GetResult<List<ScheduledJob>> getResult = new NowellpointClient(new TokenCredentials(token))
				.scheduledJob()
				.getScheduledJobs();
		
		Map<String, Object> model = getModel();
		model.put("scheduledJobList", getResult.getTarget());

		return render(configuration, request, response, model, Template.SCHEDULED_JOBS_LIST);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String selectType(Configuration configuration, Request request, Response response) {

		Token token = getToken(request);
		
		AccountProfile accountProfile = getAccount(request);
		
		if (accountProfile.getSubscription().getPlanId() == null) {
			response.redirect(Path.Route.ACCOUNT_PROFILE_LIST_PLANS.replace(":id", accountProfile.getId()));
			halt();
		}
		
		List<ScheduledJobType> scheduledJobTypes = new NowellpointClient(new TokenCredentials(token))
				.scheduledJobType()
				.getScheduledJobTypesByLanguage(accountProfile.getLanguageSidKey());
		
		Map<String, Object> model = getModel();
		model.put("step", "select-type");
    	model.put("scheduledJobTypeList", scheduledJobTypes);
    	model.put("title", getLabel(request, "select.scheduled.job.type"));
    	
    	return render(configuration, request, response, model, Template.SCHEDULED_JOB_SELECT);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 * @throws JsonProcessingException
	 */
	
	private String selectConnector(Configuration configuration, Request request, Response response) throws JsonProcessingException {

		Token token = getToken(request);
		
		String jobTypeId = request.queryParams("job-type-id");
		
		ScheduledJobType scheduledJobType = new NowellpointClient(new TokenCredentials(token))
				.scheduledJobType()
				.getById(jobTypeId);
		
		ScheduledJob scheduledJob = new ScheduledJob();
		scheduledJob.setId(UUID.randomUUID().toString());
		scheduledJob.setJobTypeId(scheduledJobType.getId());
		scheduledJob.setJobTypeCode(scheduledJobType.getCode());
		scheduledJob.setJobTypeName(scheduledJobType.getName());
		
		putValue(token, scheduledJob.getId(), objectMapper.writeValueAsString(scheduledJob));
		
		Map<String, Object> model = getModel();
		model.put("step", "select-connector");
		model.put("scheduledJob", scheduledJob);
    	model.put("title", getLabel(request, "select.connector"));
		
		if ("SALESFORCE_METADATA_BACKUP".equals(scheduledJob.getJobTypeCode())) {
			
			List<SalesforceConnector> salesforceConnectors = new NowellpointClient(new TokenCredentials(token))
					.salesforceConnector()
					.getSalesforceConnectors();

	    	model.put("salesforceConnectorsList", salesforceConnectors);
		}
		
    	return render(configuration, request, response, model, Template.SCHEDULED_JOB_SELECT);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private String selectEnvironment(Configuration configuration, Request request, Response response) throws IOException {
		
		Token token = getToken(request);
		
		String id = request.queryParams("id");
		String connectorId = request.queryParams("connector-id");
		
		ScheduledJob scheduledJob = objectMapper.readValue(getValue(token, id), ScheduledJob.class);
		scheduledJob.setConnectorId(connectorId);
		
		putValue(token, scheduledJob.getId(), objectMapper.writeValueAsString(scheduledJob));
		
		Map<String, Object> model = getModel();
		model.put("step", "select-environment");
		model.put("scheduledJob", scheduledJob);
		model.put("title", getLabel(request, "select.environment"));
		
		if ("SALESFORCE_METADATA_BACKUP".equals(scheduledJob.getJobTypeCode())) {
			
			List<Environment> environments = new NowellpointClient(new TokenCredentials(token))
					.salesforceConnector()
					.getEnvironments(connectorId);
			
			model.put("environments", environments);
		}
		
    	return render(configuration, request, response, model, Template.SCHEDULED_JOB_SELECT);
	}

	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private String setSchedule(Configuration configuration, Request request, Response response) throws IOException {
		
		Token token = getToken(request);
		
		String id = request.queryParams("id");
		String environmentKey = request.queryParams("environment-key");
		
		ScheduledJob scheduledJob = objectMapper.readValue(getValue(token, id), ScheduledJob.class);
		
		GetResult<Environment> result = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.getEnvironment(scheduledJob.getConnectorId(), environmentKey);
		
		scheduledJob.setNotificationEmail(result.getTarget().getEmail());
		scheduledJob.setEnvironmentKey(result.getTarget().getKey());
		scheduledJob.setEnvironmentName(result.getTarget().getEnvironmentName());
		
		putValue(token, scheduledJob.getId(), objectMapper.writeValueAsString(scheduledJob));
			
		Map<String, Object> model = getModel();
		model.put("step", "set-schedule");
		model.put("scheduledJob", scheduledJob);
		model.put("action", Path.Route.SCHEDULED_JOB_CREATE);
		model.put("title", getLabel(request, "schedule.job"));
		
		return render(configuration, request, response, model, Template.SCHEDULED_JOB_SELECT);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException
	 */
	
	private String createScheduledJob(Configuration configuration, Request request, Response response) throws JsonParseException, JsonMappingException, IOException, ParseException {
		
		Token token = getToken(request);
		
		String id = request.queryParams("id");
		String notificationEmail = request.queryParams("notificationEmail");
		String description = request.queryParams("description");
		String scheduleDate = request.queryParams("scheduleDate");
		String scheduleTime = request.queryParams("scheduleTime");
		
		try {
			
			new SimpleDateFormat("yyyy-MM-dd").parse(scheduleDate);
			new SimpleDateFormat("HH:mm").parse(scheduleTime);
			
		} catch (ParseException e) {
			LOGGER.error(e.getMessage());
			
			ScheduledJob scheduledJob = objectMapper.readValue(getValue(token, id), ScheduledJob.class);
			
			Map<String, Object> model = getModel();
			model.put("step", "set-schedule");
			model.put("scheduledJob", scheduledJob);
			model.put("action", Path.Route.SCHEDULED_JOB_CREATE);
			model.put("title", getLabel(request, "schedule.job"));
			model.put("errorMessage", MessageProvider.getMessage(getLocale(request), "unparseable.date.time"));
			return render(configuration, request, response, model, Template.SCHEDULED_JOB_SELECT);
		}

		ScheduledJob scheduledJob = objectMapper.readValue(getValue(token, id), ScheduledJob.class);
		
		ScheduledJobRequest createScheduledJobRequest = new ScheduledJobRequest()
				.withConnectorId(scheduledJob.getConnectorId())
				.withNotificationEmail(notificationEmail)
				.withDescription(description)
				.withEnvironmentKey(scheduledJob.getEnvironmentKey())
				.withJobTypeId(scheduledJob.getJobTypeId())
				.withScheduleDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", getLocale(request)).parse(scheduleDate.concat("T").concat(scheduleTime).concat(":00")));
		
		CreateResult<ScheduledJob> createScheduledJobResult = new NowellpointClient(new TokenCredentials(token))
				.scheduledJob()
				.create(createScheduledJobRequest);
		
		if (! createScheduledJobResult.isSuccess()) {
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"); 
			
			scheduledJob.setNotificationEmail(notificationEmail);
			scheduledJob.setDescription(description);
			scheduledJob.setScheduleDate(sdf.parse(scheduleDate.concat("T").concat(scheduleTime).concat(":00.SSSZ")));
			
			Map<String, Object> model = getModel();
			model.put("step", "set-schedule");
			model.put("scheduledJob", scheduledJob);
			model.put("action", Path.Route.SCHEDULED_JOB_CREATE);
			model.put("title", getLabel(request, "schedule.job"));
			model.put("errorMessage", createScheduledJobResult.getErrorMessage());
			return render(configuration, request, response, model, Template.SCHEDULED_JOB_SELECT);
		}
		
		removeValue(token, id);
		
		response.redirect(Path.Route.SCHEDULED_JOB_VIEW.replace(":id", createScheduledJobResult.getTarget().getId()));
		
		return "";
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String viewScheduledJob(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		GetResult<ScheduledJob> result = new NowellpointClient(new TokenCredentials(token))
				.scheduledJob()
				.get(id);
		
		String createdByHref = Path.Route.ACCOUNT_PROFILE.replace(":id", result.getTarget().getCreatedBy().getId());
		String lastModifiedByHref = Path.Route.ACCOUNT_PROFILE.replace(":id", result.getTarget().getLastModifiedBy().getId());
		
		Map<String, Object> model = getModel();
		model.put("scheduledJob", result.getTarget());
		model.put("createdByHref", createdByHref);
		model.put("lastModifiedByHref", lastModifiedByHref);
		model.put("connectorHref", Path.Route.CONNECTORS_SALESFORCE_VIEW.replace(":id", result.getTarget().getConnectorId()));
		model.put("successMessage", getValue(token, "success.message"));
		
		if (model.get("successMessage") != null) {
			removeValue(token, "success.message");
		}
		
		return render(configuration, request, response, model, Template.SCHEDULED_JOB);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String editScheduledJob(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		String view = request.queryParams("view");
		
		Token token = getToken(request);
		
		GetResult<ScheduledJob> result = new NowellpointClient(new TokenCredentials(token))
				.scheduledJob()
				.get(id);
		
		Map<String, Object> model = getModel();
		model.put("scheduledJob", result.getTarget());
		model.put("mode", "edit");
		model.put("action", Path.Route.SCHEDULED_JOB_UPDATE.replace(":id", id));
		
		if (view != null && view.equals("1")) {
			model.put("cancel", Path.Route.SCHEDULED_JOBS_LIST);
		} else {
			model.put("cancel", Path.Route.SCHEDULED_JOB_VIEW.replace(":id", id));
		}
		
		return render(configuration, request, response, model, Template.SCHEDULED_JOB_EDIT);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String startScheduledJob(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		UpdateResult<ScheduledJob> updateResult = new NowellpointClient(new TokenCredentials(token))
				.scheduledJob() 
				.start(id);
		
		putValue(token, "success.message", MessageProvider.getMessage(getLocale(request), "activate.scheduled.job.success"));
		
		response.redirect(Path.Route.SCHEDULED_JOB_VIEW.replace(":id", updateResult.getTarget().getId()));
		
		return "";
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String stopScheduledJob(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		ScheduledJob scheduledJob = new NowellpointClient(new TokenCredentials(token))
				.scheduledJob() 
				.stop(id)
				.getTarget();
		
		putValue(token, "success.message", MessageProvider.getMessage(getLocale(request), "deactivate.scheduled.job.success"));
		
		response.redirect(Path.Route.SCHEDULED_JOB_VIEW.replace(":id", scheduledJob.getId()));
		
		return "";
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String terminateScheduledJob(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		UpdateResult<ScheduledJob> updateResult = new NowellpointClient(new TokenCredentials(token))
				.scheduledJob() 
				.terminate(id);
		
		putValue(token, "success.message", MessageProvider.getMessage(getLocale(request), "terminate.scheduled.job.success"));
		
		response.redirect(Path.Route.SCHEDULED_JOB_VIEW.replace(":id", updateResult.getTarget().getId()));
		
		return "";
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 * @throws ParseException
	 */
	
	private String updateScheduledJob(Configuration configuration, Request request, Response response) throws ParseException {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		String notificationEmail = request.queryParams("notificationEmail");
		String environmentKey = request.queryParams("environmentKey");
		String description = request.queryParams("description");
		String scheduleDate = request.queryParams("scheduleDate");
		String scheduleTime = request.queryParams("scheduleTime");
		
		try {

			new SimpleDateFormat("yyyy-MM-dd").parse(scheduleDate);
			new SimpleDateFormat("HH:mm").parse(scheduleTime);
			
		} catch (ParseException e) {
			LOGGER.error(e.getMessage());
			
			GetResult<ScheduledJob> result = new NowellpointClient(new TokenCredentials(token))
					.scheduledJob()
					.get(id);
			
			String view = request.queryParams("view");
			
			Map<String, Object> model = getModel();
			model.put("scheduledJob", result.getTarget());
			model.put("mode", "edit");
			model.put("action", Path.Route.SCHEDULED_JOB_UPDATE.replace(":id", id));
			model.put("errorMessage", MessageProvider.getMessage(getLocale(request), "unparseable.date.time"));
			if (view != null && view.equals("1")) {
				model.put("cancel", Path.Route.SCHEDULED_JOBS_LIST);
			} else {
				model.put("cancel", Path.Route.SCHEDULED_JOB_VIEW.replace(":id", id));
			}
			return render(configuration, request, response, model, Template.SCHEDULED_JOB_EDIT);
		}	
		
		ScheduledJobRequest scheduledJobRequest = new ScheduledJobRequest()
				.withId(id)
				.withNotificationEmail(notificationEmail)
				.withDescription(description)
				.withEnvironmentKey(environmentKey)
				.withScheduleDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", getLocale(request)).parse(scheduleDate.concat("T").concat(scheduleTime).concat(":00")));

		UpdateResult<ScheduledJob> updateResult = new NowellpointClient(new TokenCredentials(token))
				.scheduledJob() 
				.update(scheduledJobRequest);
		
		if (! updateResult.isSuccess()) {
			
			GetResult<ScheduledJob> result = new NowellpointClient(new TokenCredentials(token))
					.scheduledJob()
					.get(id);
			
			String view = request.queryParams("view");
			
			Map<String, Object> model = getModel();
			model.put("scheduledJob", result.getTarget());
			model.put("mode", "edit");
			model.put("action", Path.Route.SCHEDULED_JOB_UPDATE.replace(":id", id));
			model.put("errorMessage", updateResult.getErrorMessage());
			if (view != null && view.equals("1")) {
				model.put("cancel", Path.Route.SCHEDULED_JOBS_LIST);
			} else {
				model.put("cancel", Path.Route.SCHEDULED_JOB_VIEW.replace(":id", id));
			}
			return render(configuration, request, response, model, Template.SCHEDULED_JOB_EDIT);
		}
		
		response.redirect(Path.Route.SCHEDULED_JOB_VIEW.replace(":id", updateResult.getTarget().getId()));
		
		return "";
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String getRunHistory(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		String fireInstanceId = request.params(":fireInstanceId");
		
		GetResult<RunHistory> getResult = new NowellpointClient(new TokenCredentials(token))
				.scheduledJob()
				.runHistory()
				.get(id, fireInstanceId);
		
		Map<String, Object> model = getModel();
		model.put("scheduledJob", new ScheduledJob(id));
		model.put("runHistory", getResult.getTarget());
		
		return render(configuration, request, response, model, Template.SCHEDULE_JOB_RUN_HISTORY);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String downloadFile(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		String fireInstanceId = request.params(":fireInstanceId");
		String filename = request.params(":filename");
		
		GetResult<String> getResult = new NowellpointClient(new TokenCredentials(token))
				.scheduledJob()
				.runHistory()
				.getFile(id, fireInstanceId, filename);
		
		return getResult.getTarget();
	}
}