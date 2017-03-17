package com.nowellpoint.www.app.view;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Map;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.JobSpecification;
import com.nowellpoint.client.model.JobSpecificationList;
import com.nowellpoint.client.model.JobSpecificationRequest;
import com.nowellpoint.client.model.JobType;
import com.nowellpoint.client.model.JobTypeList;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.SalesforceConnectorList;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class JobSpecificationController extends AbstractStaticController {
	
	public static class Template {
		public static final String JOB_SPECIFICATION_LIST = String.format(APPLICATION_CONTEXT, "job-specification-list.html");
		public static final String JOB_SPECIFICATION_SELECT = String.format(APPLICATION_CONTEXT, "job-specifiction-create.html");
		public static final String JOB_SPECIFICATION_EDIT = String.format(APPLICATION_CONTEXT, "job-specification-edit.html");
		public static final String JOB_SPECIFICATION_VIEW = String.format(APPLICATION_CONTEXT, "job-specification-view.html");
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String listJobSpecifications(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		JobSpecificationList list = new NowellpointClient(token)
				.jobSpecification()
				.getJobSpecifications();
		
		Map<String, Object> model = getModel();
		model.put("jobSpecificationList", list.getItems());

		return render(JobSpecificationController.class, configuration, request, response, model, Template.JOB_SPECIFICATION_LIST);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String listJobTypes(Configuration configuration, Request request, Response response) {

		Token token = getToken(request);
		
		Identity identity = getIdentity(request);
		
		JobTypeList jobTypeList = new NowellpointClient(token)
				.scheduledJobType()
				.getScheduledJobTypesByLanguage(identity.getLanguageSidKey());
		
		Map<String, Object> model = getModel();
		model.put("step", "select-type");
    	model.put("jobTypeList", jobTypeList.getItems());
    	model.put("salesforceConnectorsList", Collections.emptyList());
    	
    	return render(JobSpecificationController.class, configuration, request, response, model, Template.JOB_SPECIFICATION_SELECT);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String selectSource(Configuration configuration, Request request, Response response) {

		Token token = getToken(request);
		
		String jobTypeId = request.queryParams("jobTypeId");
		
		JobType jobType = new NowellpointClient(token)
				.scheduledJobType()
				.get(jobTypeId);
		
		Map<String, Object> model = getModel();
		model.put("step", "select-source");
		model.put("jobType", jobType);
		
		if ("SALESFORCE_METADATA_BACKUP".equals(jobType.getCode())) {
			
			SalesforceConnectorList salesforceConnectors = new NowellpointClient(token)
					.salesforceConnector()
					.getSalesforceConnectors();

	    	model.put("salesforceConnectorsList", salesforceConnectors.getItems());
		}
		
    	return render(JobSpecificationController.class, configuration, request, response, model, Template.JOB_SPECIFICATION_SELECT);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String selectTarget(Configuration configuration, Request request, Response response) {

		Token token = getToken(request);
		
		String jobTypeId = request.queryParams("jobTypeId");
		String connectorId = request.queryParams("connectorId");
		
		JobType jobType = new NowellpointClient(token)
				.scheduledJobType()
				.get(jobTypeId);
		
		Map<String, Object> model = getModel();
		model.put("step", "select-target");
		model.put("jobType", jobType);
		
		if ("SALESFORCE_METADATA_BACKUP".equals(jobType.getCode())) {
			
			SalesforceConnector salesforceConnector = new NowellpointClient(token)
					.salesforceConnector()
					.get(connectorId);
			
			model.put("salesforceConnector", salesforceConnector);
		}
		
    	return render(JobSpecificationController.class, configuration, request, response, model, Template.JOB_SPECIFICATION_SELECT);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String reviewJobSpecification(Configuration configuration, Request request, Response response) throws IOException {
		
		Token token = getToken(request);
		
		String jobTypeId = request.queryParams("jobTypeId");
		String connectorId = request.queryParams("connectorId");
		
		JobType jobType = new NowellpointClient(token)
				.scheduledJobType()
				.get(jobTypeId);
		
		Map<String, Object> model = getModel();
		//model.put("step", "review");
		model.put("jobType", jobType);
		
		if ("SALESFORCE_METADATA_BACKUP".equals(jobType.getCode())) {
			
			SalesforceConnector salesforceConnector = new NowellpointClient(token)
					.salesforceConnector()
					.get(connectorId);
			
			model.put("salesforceConnector", salesforceConnector);
		}
		
    	return render(JobSpecificationController.class, configuration, request, response, model, Template.JOB_SPECIFICATION_SELECT);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 * 
	 */
	
	public static String createJobSpecification(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		String jobTypeId = request.queryParams("jobTypeId");
		String connectorId = request.queryParams("connectorId");
		String name = request.queryParams("name");
		String notificationEmail = request.queryParams("notificationEmail");
		String description = request.queryParams("description");
		
		JobSpecificationRequest createjobSpecificationRequest = new JobSpecificationRequest()
				.withName(name)
				.withConnectorId(connectorId)
				.withNotificationEmail(notificationEmail)
				.withDescription(description)
				.withJobTypeId(jobTypeId);
		
		CreateResult<JobSpecification> createjobSpecificationResult = new NowellpointClient(token)
				.jobSpecification()
				.create(createjobSpecificationRequest);
		
		if (! createjobSpecificationResult.isSuccess()) {
			
			JobSpecification jobSpecification = new JobSpecification();
			jobSpecification.setNotificationEmail(notificationEmail);
			jobSpecification.setDescription(description);
			
			Map<String, Object> model = getModel();
			model.put("step", "set-schedule");
			model.put("scheduledJob", jobSpecification);
			model.put("action", Path.Route.JOB_SPECIFICATION_CREATE);
			model.put("errorMessage", createjobSpecificationResult.getErrorMessage());
			return render(JobSpecificationController.class, configuration, request, response, model, Template.JOB_SPECIFICATION_SELECT);
		}
		
		response.redirect(Path.Route.JOB_SPECIFICATION_VIEW.replace(":id", createjobSpecificationResult.getTarget().getId()));
		
		return "";
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String viewJobSpecifications(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		JobSpecification jobSpecification = new NowellpointClient(token)
				.jobSpecification()
				.get(id);
		
		String createdByHref = Path.Route.ACCOUNT_PROFILE.replace(":id", jobSpecification.getCreatedBy().getId());
		String lastModifiedByHref = Path.Route.ACCOUNT_PROFILE.replace(":id", jobSpecification.getLastUpdatedBy().getId());
		
		Map<String, Object> model = getModel();
		model.put("jobSpecification", jobSpecification);
		model.put("createdByHref", createdByHref);
		model.put("lastModifiedByHref", lastModifiedByHref);
		model.put("connectorHref", jobSpecification.getConnector() != null ? Path.Route.CONNECTORS_SALESFORCE_VIEW.replace(":id", jobSpecification.getConnector().getId()) : null);
		model.put("action", Path.Route.JOB_SPECIFICATION_UPDATE.replace(":id", id));
		model.put("successMessage", null); //getValue(token, "success.message"));
		
		if (model.get("successMessage") != null) {
			//removeValue(token, "success.message");
		}
		
		return render(JobSpecificationController.class, configuration, request, response, model, Template.JOB_SPECIFICATION_VIEW);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String editJobSpecification(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		String view = request.queryParams("view");
		
		Token token = getToken(request);
		
		JobSpecification jobSpecification = new NowellpointClient(token)
				.jobSpecification()
				.get(id);
		
		Map<String, Object> model = getModel();
		model.put("jobSpecification", jobSpecification);
		model.put("mode", "edit");
		model.put("action", Path.Route.JOB_SPECIFICATION_UPDATE.replace(":id", id));
		
		if (view != null && view.equals("1")) {
			model.put("cancel", Path.Route.JOB_SPECIFICATION_LIST);
		} else {
			model.put("cancel", Path.Route.JOB_SPECIFICATION_VIEW.replace(":id", id));
		}
		
		return render(JobSpecificationController.class, configuration, request, response, model, Template.JOB_SPECIFICATION_EDIT);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 * @throws ParseException
	 */
	
	public static String updateJobSpecification(Configuration configuration, Request request, Response response) throws ParseException {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		String notificationEmail = request.queryParams("notificationEmail");
		String description = request.queryParams("description");
		
		JobSpecificationRequest jobSpecificationRequest = new JobSpecificationRequest()
				.withId(id)
				.withNotificationEmail(notificationEmail)
				.withDescription(description);

		UpdateResult<JobSpecification> updateResult = new NowellpointClient(token)
				.jobSpecification() 
				.update(jobSpecificationRequest);
		
		if (! updateResult.isSuccess()) {
			
			JobSpecification jobSpecification = new NowellpointClient(token)
					.jobSpecification()
					.get(id);
			
			String view = request.queryParams("view");
			
			Map<String, Object> model = getModel();
			model.put("jobSpecification", jobSpecification);
			model.put("mode", "edit");
			model.put("action", Path.Route.JOB_SPECIFICATION_UPDATE.replace(":id", id));
			model.put("errorMessage", updateResult.getErrorMessage());
			if (view != null && view.equals("1")) {
				model.put("cancel", Path.Route.JOB_SPECIFICATION_LIST);
			} else {
				model.put("cancel", Path.Route.JOB_SPECIFICATION_VIEW.replace(":id", id));
			}
			return render(JobSpecificationController.class, configuration, request, response, model, Template.JOB_SPECIFICATION_EDIT);
		}
		
		response.redirect(Path.Route.JOB_SPECIFICATION_VIEW.replace(":id", updateResult.getTarget().getId()));
		
		return "";
	}
}