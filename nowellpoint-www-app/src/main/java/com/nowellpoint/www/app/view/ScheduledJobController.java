package com.nowellpoint.www.app.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.TokenCredentials;
import com.nowellpoint.client.model.AccountProfile;
import com.nowellpoint.client.model.CreateScheduledJobRequest;
import com.nowellpoint.client.model.Environment;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.ScheduledJob;
import com.nowellpoint.client.model.ScheduledJobType;
import com.nowellpoint.client.model.UpdateScheduledJobRequest;
import com.nowellpoint.client.model.idp.Token;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;
import spark.Route;

public class ScheduledJobController extends AbstractController {

	public ScheduledJobController(Configuration configuration) {
		super(ScheduledJobController.class, configuration);
	}
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * getApplications
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route getScheduledJobs = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		List<ScheduledJob> scheduledJobs = new NowellpointClient(new TokenCredentials(token))
				.getScheduledJobResource()
				.getScheduledJobs();
		
		Map<String, Object> model = getModel();
		model.put("scheduledJobList", scheduledJobs);
		
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
		
		List<ScheduledJobType> scheduledJobTypes = new NowellpointClient(new TokenCredentials(token))
				.getScheduledJobTypeResource()
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
				.getScheduledJobTypeResource()
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
					.getSalesforceConnectorResource()
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
					.getSalesforceConnectorResource()
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
		
		Environment environment = new NowellpointClient(new TokenCredentials(token))
				.getSalesforceConnectorResource()
				.getEnvironment(scheduledJob.getConnectorId(), environmentKey);
		
		scheduledJob.setEnvironmentKey(environment.getKey());
		scheduledJob.setEnvironmentName(environment.getEnvironmentName());
		
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
		String description = request.queryParams("description");
		String scheduleDate = request.queryParams("scheduleDate");
		String scheduleTime = request.queryParams("scheduleTime");
		
		try {
			
			new SimpleDateFormat("yyyy-MM-dd").parse(scheduleDate);
			new SimpleDateFormat("HH:mm:ss").parse(scheduleTime);
			
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
		
		CreateScheduledJobRequest createScheduledJobRequest = new CreateScheduledJobRequest()
				.withConnectorId(scheduledJob.getConnectorId())
				.withDescription(description)
				.withEnvironmentKey(scheduledJob.getEnvironmentKey())
				.withJobTypeId(scheduledJob.getJobTypeId())
				.withScheduleDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", getDefaultLocale(getAccount(request))).parse(scheduleDate.concat("T").concat(scheduleTime)));
		
		scheduledJob = new NowellpointClient(new TokenCredentials(token))
				.getScheduledJobResource()
				.createScheduledJob(createScheduledJobRequest);
		
		removeValue(token, id);
		
		response.redirect(Path.Route.SCHEDULED_JOB_VIEW.replace(":id", scheduledJob.getId()));
		
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
		
		ScheduledJob scheduledJob = new NowellpointClient(new TokenCredentials(token))
				.getScheduledJobResource()
				.getScheduledJob(id);
		
		Map<String, Object> model = getModel();
		model.put("scheduledJob", scheduledJob);
		model.put("connectorHref", Path.Route.CONNECTORS_SALESFORCE_VIEW.replace(":id", scheduledJob.getConnectorId()));
		model.put("successMessage", request.cookie("successMessage"));
		
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
		
		ScheduledJob scheduledJob = new NowellpointClient(new TokenCredentials(token))
				.getScheduledJobResource()
				.getScheduledJob(id);
		
		Map<String, Object> model = getModel();
		model.put("scheduledJob", scheduledJob);
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
	 * updateScheduledJob
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route updateScheduledJob = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		String environmentKey = request.queryParams("environmentKey");
		String description = request.queryParams("description");
		String scheduleDate = request.queryParams("scheduleDate");
		String scheduleTime = request.queryParams("scheduleTime");
		
		try {
			
			new SimpleDateFormat("yyyy-MM-dd").parse(scheduleDate);
			new SimpleDateFormat("HH:mm:ss").parse(scheduleTime);
			
		} catch (ParseException e) {
			LOGGER.error(e.getMessage());
			
			ScheduledJob scheduledJob = new NowellpointClient(new TokenCredentials(token))
					.getScheduledJobResource()
					.getScheduledJob(id);
			
			String view = request.queryParams("view");
			
			Map<String, Object> model = getModel();
			model.put("scheduledJob", scheduledJob);
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
		
		UpdateScheduledJobRequest updateScheduledJobRequest = new UpdateScheduledJobRequest()
				.withId(id)
				.withDescription(description)
				.withEnvironmentKey(environmentKey)
				.withScheduleDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", getDefaultLocale(getAccount(request))).parse(scheduleDate.concat("T").concat(scheduleTime)));
		
		ScheduledJob scheduledJob = new NowellpointClient(new TokenCredentials(token))
				.getScheduledJobResource() 
				.updateScheduledJob(updateScheduledJobRequest);
		
		response.redirect(Path.Route.SCHEDULED_JOB_VIEW.replace(":id", scheduledJob.getId()));
		
		return "";
	};
}