package com.nowellpoint.www.app.view;

import java.util.List;
import java.util.Map;

import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.TokenCredentials;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.ScheduledJob;
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
	 * selectSalesforceConnector
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route selectSalesforceConnector = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		List<SalesforceConnector> salesforceConnectors = new NowellpointClient(new TokenCredentials(token))
				.getSalesforceConnectorResource()
				.getSalesforceConnectors();
		
		Map<String, Object> model = getModel();
    	model.put("salesforceConnectorsList", salesforceConnectors);
		
		return render(request, model, Path.Template.SCHEDULED_JOB_SELECT);
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
		
		String id = request.queryParams("id");
		
		SalesforceConnector salesforceConnector = new NowellpointClient(new TokenCredentials(token))
				.getSalesforceConnectorResource()
				.getSalesforceConnector(id);
		
		Map<String, Object> model = getModel();
		model.put("mode", "new");
    	model.put("connectorId", salesforceConnector.getId());
    	model.put("connectorType", "SALESFORCE");
    	model.put("jobType", "Metadata Backup");
		
		return render(request, model, Path.Template.SCHEDULED_JOB_EDIT);
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
}