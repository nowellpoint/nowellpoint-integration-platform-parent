package com.nowellpoint.www.app.view;

import java.util.List;
import java.util.Map;

import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.TokenCredentials;
import com.nowellpoint.client.model.AccountProfile;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.Schedule;
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
		
		if ("select-type".equals(step)) {
			
			List<ScheduledJobType> scheduledJobTypes = new NowellpointClient(new TokenCredentials(token))
					.getScheduledJobTypeResource()
					.getScheduledJobTypesByLanguage(account.getLanguageSidKey());
			
	    	model.put("scheduledJobTypeList", scheduledJobTypes);
	    	model.put("title", MessageProvider.getMessage(getDefaultLocale(request), "select.scheduled.job.type"));
	    	model.put("step", step);
			
		} else if ("select-connector".equals(step)) {
			
			String jobTypeId = request.queryParams("job-type-id");
			String connectorType = request.queryParams("connector-type");
			
			if ("SALESFORCE".equals(connectorType)) {
				
				List<SalesforceConnector> salesforceConnectors = new NowellpointClient(new TokenCredentials(token))
						.getSalesforceConnectorResource()
						.getSalesforceConnectors();

		    	model.put("salesforceConnectorsList", salesforceConnectors);
			}
			
			model.put("jobTypeId", jobTypeId);
	    	model.put("title", MessageProvider.getMessage(getDefaultLocale(request), "select.connector"));
	    	model.put("step", step);
	    	
		} else if ("schedule".equals(step)) {
			
			String connectorId = request.queryParams("connector-id");
			String jobTypeId = request.queryParams("job-type-id");
			
			SalesforceConnector salesforceConnector = new NowellpointClient(new TokenCredentials(token))
					.getSalesforceConnectorResource()
					.getSalesforceConnector(connectorId);
			
			model.put("mode", "new");
			model.put("jobTypeId", jobTypeId);
	    	model.put("connectorId", salesforceConnector.getId());
	    	model.put("connectorType", "SALESFORCE");
	    	model.put("jobType", "Metadata Backup");
			
			return render(request, model, Path.Template.SCHEDULED_JOB_EDIT);
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
		
		String name = request.queryParams("name");
		String description = request.queryParams("description");
		String connectorType = request.queryParams("connectorType");
		String connectorId = request.queryParams("connectorId");
		
		ScheduledJob scheduledJob = new NowellpointClient(new TokenCredentials(token))
				.getScheduledJobResource()
				.createScheduledJob(name, description, connectorType, connectorId);
		
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
		String name = request.queryParams("name");
		String description = request.queryParams("description");
		
		ScheduledJob scheduledJob = new NowellpointClient(new TokenCredentials(token))
				.getScheduledJobResource()
				.updateScheduledJob(id, name, description);
		
		response.redirect(Path.Route.SCHEDULED_JOB_VIEW.replace(":id", scheduledJob.getId()));
		
		return "";
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * editSchedule
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route editSchedule = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		Schedule schedule = new NowellpointClient(new TokenCredentials(token))
				.getScheduledJobResource()
				.getSchedule(id, key);
		
		Map<String, Object> model = getModel();
		model.put("id", id);
		model.put("schedule", schedule);
		model.put("mode", "edit");
		
		return render(request, model, Path.Template.SCHEDULE);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * viewSchedule
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route viewSchedule = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		Schedule schedule = new NowellpointClient(new TokenCredentials(token))
				.getScheduledJobResource()
				.getSchedule(id, key);
		
		Map<String, Object> model = getModel();
		model.put("schedule", schedule);
		model.put("mode", "view");
		
		return render(request, model, Path.Template.SCHEDULE);
	};
}