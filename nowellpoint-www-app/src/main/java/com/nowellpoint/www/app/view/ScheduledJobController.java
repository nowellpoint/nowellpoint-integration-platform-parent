package com.nowellpoint.www.app.view;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.TokenCredentials;
import com.nowellpoint.client.model.AccountProfile;
import com.nowellpoint.client.model.Environment;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.ScheduledJob;
import com.nowellpoint.client.model.ScheduledJobType;
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
	 * newScheduledJob
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route newScheduledJob = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		AccountProfile account = getAccount(request);
		
		String step = request.params(":step");
		
		Map<String, Object> model = getModel();
		model.put("step", step);
		
		if ("select-type".equals(step)) {
			
			List<ScheduledJobType> scheduledJobTypes = new NowellpointClient(new TokenCredentials(token))
					.getScheduledJobTypeResource()
					.getScheduledJobTypesByLanguage(account.getLanguageSidKey());
			
	    	model.put("scheduledJobTypeList", scheduledJobTypes);
	    	model.put("title", MessageProvider.getMessage(getDefaultLocale(request), "select.scheduled.job.type"));
	    	
		} else if ("select-connector".equals(step)) {
			
			String jobTypeId = request.queryParams("job-type-id");
			
			ScheduledJob scheduledJob = new NowellpointClient(new TokenCredentials(token))
					.getScheduledJobResource()
					.createScheduledJob(null, null, jobTypeId, null, null, null);
			
			if ("SALESFORCE_METADATA_BACKUP".equals(scheduledJob.getJobTypeCode())) {
				
				List<SalesforceConnector> salesforceConnectors = new NowellpointClient(new TokenCredentials(token))
						.getSalesforceConnectorResource()
						.getSalesforceConnectors();

		    	model.put("salesforceConnectorsList", salesforceConnectors);
			}
			
			model.put("scheduledJob", scheduledJob);
	    	model.put("title", MessageProvider.getMessage(getDefaultLocale(request), "select.connector"));
	    	
		} else if ("select-environment".equals(step)) {
			
			String id = request.queryParams("id");
			String connectorId = request.queryParams("connector-id");
			
			ScheduledJob scheduledJob = new NowellpointClient(new TokenCredentials(token))
					.getScheduledJobResource() 
					.updateScheduledJob(id, null, null, connectorId, null, null);
			
			if ("SALESFORCE_METADATA_BACKUP".equals(scheduledJob.getJobTypeCode())) {
				
				List<Environment> environments = new NowellpointClient(new TokenCredentials(token))
						.getSalesforceConnectorResource()
						.getEnvironments(connectorId);
				
				model.put("environments", environments);
			}
			
			model.put("scheduledJob", scheduledJob);
			model.put("title", MessageProvider.getMessage(getDefaultLocale(request), "select.environment"));
			
		} else if ("schedule".equals(step)) {
			
			String connectorId = request.queryParams("connector-id");
			String environmentKey = request.queryParams("environment-key");
			
			Environment environment = new NowellpointClient(new TokenCredentials(token))
					.getSalesforceConnectorResource()
					.getEnvironment(connectorId, environmentKey);
			
			ScheduledJob scheduledJob = objectMapper.readValue(request.cookie("com.nowellpoint.scheduled.job"), ScheduledJob.class);
			scheduledJob.setEnvironmentKey(environment.getKey());
			scheduledJob.setEnvironmentName(environment.getEnvironmentName());
			
			model.put("scheduledJob", scheduledJob);
			model.put("title", MessageProvider.getMessage(getDefaultLocale(request), "schedule.job"));
		}
		
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
		
		String description = request.queryParams("description");
		String connectorType = request.queryParams("connectorType");
		String connectorId = request.queryParams("connectorId");
		
		ScheduledJob scheduledJob = new NowellpointClient(new TokenCredentials(token))
				.getScheduledJobResource()
				.createScheduledJob("environmentKey", "description", "jobTypeId", "connectorId", Date.from(Instant.now()), Date.from(Instant.now()));
		
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
		String connectorId = request.queryParams("connectorId");
		String scheduleDate = request.queryParams("scheduleDate");
		String scheduleTime = request.queryParams("scheduleTime");
		
		ScheduledJob scheduledJob = new NowellpointClient(new TokenCredentials(token))
				.getScheduledJobResource() 
				.updateScheduledJob(id, environmentKey, description, connectorId, null, null);
		
		response.redirect(Path.Route.SCHEDULED_JOB_VIEW.replace(":id", scheduledJob.getId()));
		
		return "";
	};
}