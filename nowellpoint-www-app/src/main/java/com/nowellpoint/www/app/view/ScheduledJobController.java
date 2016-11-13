package com.nowellpoint.www.app.view;

import static spark.Spark.halt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import spark.Route;

public class ScheduledJobController extends AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(ScheduledJobController.class.getName());

	public ScheduledJobController(Configuration configuration) {
		super(ScheduledJobController.class, configuration);
	}
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * getScheduledJobs
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route getScheduledJobs = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		GetResult<List<ScheduledJob>> getResult = new NowellpointClient(new TokenCredentials(token))
				.scheduledJob()
				.getScheduledJobs();
		
		Map<String, Object> model = getModel();
		model.put("scheduledJobList", getResult.getTarget());

		return render(request, model, Path.Template.SCHEDULED_JOBS_LIST);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * selectType
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route selectType = (Request request, Response response) -> {
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
    	model.put("title", getLabel(accountProfile, "select.scheduled.job.type"));
    	
    	return render(request, model, Path.Template.SCHEDULED_JOB_SELECT);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * selectConnector
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route selectConnector = (Request request, Response response) -> {
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
    	model.put("title", getLabel(getAccount(request), "select.connector"));
		
		if ("SALESFORCE_METADATA_BACKUP".equals(scheduledJob.getJobTypeCode())) {
			
			List<SalesforceConnector> salesforceConnectors = new NowellpointClient(new TokenCredentials(token))
					.salesforceConnector()
					.getSalesforceConnectors();

	    	model.put("salesforceConnectorsList", salesforceConnectors);
		}
		
    	return render(request, model, Path.Template.SCHEDULED_JOB_SELECT);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * selectEnvironment
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route selectEnvironment = (Request request, Response response) -> {
		Token token = getToken(request);
		
		String id = request.queryParams("id");
		String connectorId = request.queryParams("connector-id");
		
		ScheduledJob scheduledJob = objectMapper.readValue(getValue(token, id), ScheduledJob.class);
		scheduledJob.setConnectorId(connectorId);
		
		putValue(token, scheduledJob.getId(), objectMapper.writeValueAsString(scheduledJob));
		
		Map<String, Object> model = getModel();
		model.put("step", "select-environment");
		model.put("scheduledJob", scheduledJob);
		model.put("title", getLabel(getAccount(request), "select.environment"));
		
		if ("SALESFORCE_METADATA_BACKUP".equals(scheduledJob.getJobTypeCode())) {
			
			List<Environment> environments = new NowellpointClient(new TokenCredentials(token))
					.salesforceConnector()
					.getEnvironments(connectorId);
			
			model.put("environments", environments);
		}
		
    	return render(request, model, Path.Template.SCHEDULED_JOB_SELECT);
	};

	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * setSchedule
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route setSchedule = (Request request, Response response) -> {
		
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
		model.put("title", getLabel(getAccount(request), "schedule.job"));
		
		return render(request, model, Path.Template.SCHEDULED_JOB_SELECT);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * createScheduledJob
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route createScheduledJob = (Request request, Response response) -> {
		
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
			model.put("title", getLabel(getAccount(request), "schedule.job"));
			model.put("errorMessage", MessageProvider.getMessage(getDefaultLocale(getAccount(request)), "unparseable.date.time"));
			return render(request, model, Path.Template.SCHEDULED_JOB_SELECT);
		}

		ScheduledJob scheduledJob = objectMapper.readValue(getValue(token, id), ScheduledJob.class);
		
		ScheduledJobRequest createScheduledJobRequest = new ScheduledJobRequest()
				.withConnectorId(scheduledJob.getConnectorId())
				.withNotificationEmail(notificationEmail)
				.withDescription(description)
				.withEnvironmentKey(scheduledJob.getEnvironmentKey())
				.withJobTypeId(scheduledJob.getJobTypeId())
				.withScheduleDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", getDefaultLocale(getAccount(request))).parse(scheduleDate.concat("T").concat(scheduleTime).concat(":00")));
		
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
			model.put("title", getLabel(getAccount(request), "schedule.job"));
			model.put("errorMessage", createScheduledJobResult.getErrorMessage());
			return render(request, model, Path.Template.SCHEDULED_JOB_SELECT);
		}
		
		removeValue(token, id);
		
		response.redirect(Path.Route.SCHEDULED_JOB_VIEW.replace(":id", createScheduledJobResult.getTarget().getId()));
		
		return "";
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * viewScheduledJob
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route viewScheduledJob = (Request request, Response response) -> {
		
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
		
		return render(request, model, Path.Template.SCHEDULED_JOB);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * editScheduledJob
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route editScheduledJob = (Request request, Response response) -> {
		
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
		
		return render(request, model, Path.Template.SCHEDULED_JOB_EDIT);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * startScheduledJob
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route startScheduledJob = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		UpdateResult<ScheduledJob> updateResult = new NowellpointClient(new TokenCredentials(token))
				.scheduledJob() 
				.start(id);
		
		putValue(token, "success.message", MessageProvider.getMessage(getDefaultLocale(getAccount(request)), "activate.scheduled.job.success"));
		
		response.redirect(Path.Route.SCHEDULED_JOB_VIEW.replace(":id", updateResult.getTarget().getId()));
		
		return "";
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * stopScheduledJob
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route stopScheduledJob = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		ScheduledJob scheduledJob = new NowellpointClient(new TokenCredentials(token))
				.scheduledJob() 
				.stop(id)
				.getTarget();
		
		putValue(token, "success.message", MessageProvider.getMessage(getDefaultLocale(getAccount(request)), "deactivate.scheduled.job.success"));
		
		response.redirect(Path.Route.SCHEDULED_JOB_VIEW.replace(":id", scheduledJob.getId()));
		
		return "";
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * terminateScheduledJob
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route terminateScheduledJob = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		UpdateResult<ScheduledJob> updateResult = new NowellpointClient(new TokenCredentials(token))
				.scheduledJob() 
				.terminate(id);
		
		putValue(token, "success.message", MessageProvider.getMessage(getDefaultLocale(getAccount(request)), "terminate.scheduled.job.success"));
		
		response.redirect(Path.Route.SCHEDULED_JOB_VIEW.replace(":id", updateResult.getTarget().getId()));
		
		return "";
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * updateScheduledJob
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route updateScheduledJob = (Request request, Response response) -> {
		
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
			model.put("errorMessage", MessageProvider.getMessage(getDefaultLocale(getAccount(request)), "unparseable.date.time"));
			if (view != null && view.equals("1")) {
				model.put("cancel", Path.Route.SCHEDULED_JOBS_LIST);
			} else {
				model.put("cancel", Path.Route.SCHEDULED_JOB_VIEW.replace(":id", id));
			}
			return render(request, model, Path.Template.SCHEDULED_JOB_EDIT);
		}	
		
		ScheduledJobRequest scheduledJobRequest = new ScheduledJobRequest()
				.withId(id)
				.withNotificationEmail(notificationEmail)
				.withDescription(description)
				.withEnvironmentKey(environmentKey)
				.withScheduleDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", getDefaultLocale(getAccount(request))).parse(scheduleDate.concat("T").concat(scheduleTime).concat(":00")));

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
			return render(request, model, Path.Template.SCHEDULED_JOB_EDIT);
		}
		
		response.redirect(Path.Route.SCHEDULED_JOB_VIEW.replace(":id", updateResult.getTarget().getId()));
		
		return "";
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * getRunHistory
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route getRunHistory = (Request request, Response response) -> {
		
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
		
		return render(request, model, Path.Template.SCHEDULE_JOB_RUN_HISTORY);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * downloadFile
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route downloadFile = (Request request, Response response) -> {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String fireInstanceId = request.params(":fireInstanceId");
		String filename = request.params(":filename");
		
		GetResult<String> getResult = new NowellpointClient(new TokenCredentials(token))
				.scheduledJob()
				.runHistory()
				.getFile(id, fireInstanceId, filename);
		
		return getResult.getTarget();
	};
}