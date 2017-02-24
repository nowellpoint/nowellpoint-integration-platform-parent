package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.ConnectorInfo;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Instance;
import com.nowellpoint.client.model.InstanceInfo;
import com.nowellpoint.client.model.JobSpecificationList;
import com.nowellpoint.client.model.JobSpecification;
import com.nowellpoint.client.model.JobSpecificationRequest;
import com.nowellpoint.client.model.JobType;
import com.nowellpoint.client.model.JobTypeInfo;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.SalesforceConnectorList;
import com.nowellpoint.client.model.JobTypeList;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.Path;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class JobSpecificationController extends AbstractStaticController {
	
	private static final Logger LOGGER = Logger.getLogger(JobSpecificationController.class.getName());
	
	public static class Template {
		public static final String JOB_SPECIFICATION_LIST = String.format(APPLICATION_CONTEXT, "job-specification-list.html");
		public static final String JOB_SPECIFICATION_SELECT = String.format(APPLICATION_CONTEXT, "job-specifiction-create.html");
		public static final String JOB_SPECIFICATION_EDIT = String.format(APPLICATION_CONTEXT, "job-specification-edit.html");
		public static final String JOB_SPECIFICATION_VIEW = String.format(APPLICATION_CONTEXT, "job-specification-view.html");
	}
	
	public static void configureRoutes(Configuration configuration) {
        get(Path.Route.JOB_SPECIFICATION_LIST, (request, response) -> listJobSpecifications(configuration, request, response));
        get(Path.Route.JOB_SPECIFICATION_SELECT_TYPE, (request, response) -> listJobTypes(configuration, request, response));
        get(Path.Route.JOB_SPECIFICATION_SELECT_CONNECTOR, (request, response) -> selectConnector(configuration, request, response));
        get(Path.Route.JOB_SPECIFICATION_SELECT_ENVIRONMENT, (request, response) -> selectEnvironment(configuration, request, response));
        get(Path.Route.JOB_SPECIFICATION_SET_SCHEDULE, (request, response) -> setSchedule(configuration, request, response));
        post(Path.Route.JOB_SPECIFICATION_CREATE, (request, response) -> createScheduledJob(configuration, request, response));
        get(Path.Route.JOB_SPECIFICATION_VIEW, (request, response) -> viewJobSpecifications(configuration, request, response));
        get(Path.Route.JOB_SPECIFICATION_EDIT, (request, response) -> editJobSpecification(configuration, request, response));
        post(Path.Route.JOB_SPECIFICATION_UPDATE, (request, response) -> updateScheduledJob(configuration, request, response));
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String listJobSpecifications(Configuration configuration, Request request, Response response) {
		
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
	
	private static String listJobTypes(Configuration configuration, Request request, Response response) {

		Token token = getToken(request);
		
		Identity identity = getIdentity(request);
		
		JobTypeList jobTypeList = new NowellpointClient(token)
				.scheduledJobType()
				.getScheduledJobTypesByLanguage(identity.getLanguageSidKey());
		
		Map<String, Object> model = getModel();
		model.put("step", "select-type");
    	model.put("jobTypeList", jobTypeList.getItems());
    	
    	return render(JobSpecificationController.class, configuration, request, response, model, Template.JOB_SPECIFICATION_SELECT);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 * @throws JsonProcessingException
	 */
	
	private static String selectConnector(Configuration configuration, Request request, Response response) throws JsonProcessingException {

		Token token = getToken(request);
		
		String jobTypeId = request.queryParams("jobTypeId");
		
		JobType jobType = new NowellpointClient(token)
				.scheduledJobType()
				.get(jobTypeId);
		
		Map<String, Object> model = getModel();
		model.put("step", "select-connector");
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
	 * @throws IOException
	 */
	
	private static String selectEnvironment(Configuration configuration, Request request, Response response) throws IOException {
		
		Token token = getToken(request);
		
		String jobTypeId = request.queryParams("jobTypeId");
		String connectorId = request.queryParams("connectorId");
		
		JobType jobType = new NowellpointClient(token)
				.scheduledJobType()
				.get(jobTypeId);
		
		Map<String, Object> model = getModel();
		model.put("step", "select-environment");
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
	 * @throws IOException
	 */
	
	private static String setSchedule(Configuration configuration, Request request, Response response) throws IOException {
		
		Token token = getToken(request);
		
		String jobTypeId = request.queryParams("jobTypeId");
		String connectorId = request.queryParams("connectorId");
		String instanceKey = request.queryParams("instanceKey");
		
		JobType jobType = new NowellpointClient(token)
				.scheduledJobType()
				.get(jobTypeId);
		
		Map<String, Object> model = getModel();
		model.put("step", "set-schedule");		
		
		JobTypeInfo jobTypeInfo = new JobTypeInfo();
		jobTypeInfo.setCode(jobType.getCode());
		jobTypeInfo.setConnectorType(jobType.getConnectorType());
		jobTypeInfo.setDescription(jobType.getDescription());
		jobTypeInfo.setId(jobType.getId());
		jobTypeInfo.setName(jobType.getName());
		
		JobSpecification jobSpecification = new JobSpecification();
		jobSpecification.setStart(new Date());
		jobSpecification.setJobType(jobTypeInfo);
		
		if ("SALESFORCE_METADATA_BACKUP".equals(jobType.getCode())) {
			
			SalesforceConnector salesforceConnector = new NowellpointClient(token)
					.salesforceConnector()
					.get(connectorId);
			
			ConnectorInfo connectorInfo = new ConnectorInfo();
			connectorInfo.setId(salesforceConnector.getId());
			connectorInfo.setName(salesforceConnector.getName());
			connectorInfo.setOrganizationName(salesforceConnector.getOrganization().getName());
			connectorInfo.setServerName(salesforceConnector.getOrganization().getInstanceName());
			
			Optional<Instance> optional = salesforceConnector.getInstances()
					.stream()
					.filter(i -> i.getKey().equals(instanceKey))
					.findFirst();
			
			if (optional.isPresent()) {
				
				Instance instance = optional.get();
				
				InstanceInfo instanceInfo = new InstanceInfo();
				instanceInfo.setApiVersion(instance.getApiVersion());
				instanceInfo.setIsSandbox(instance.getIsSandbox());
				instanceInfo.setKey(instance.getKey());
				instanceInfo.setName(instance.getName());
				instanceInfo.setServiceEndpoint(instance.getServiceEndpoint());
				
				connectorInfo.setInstance(instanceInfo);
				
				System.out.println(new ObjectMapper().writeValueAsString(instanceInfo));
				
				jobSpecification.setConnector(connectorInfo);
				jobSpecification.setNotificationEmail(instance.getEmail());

			}
		}

		model.put("jobSpecification", jobSpecification);
		model.put("action", Path.Route.JOB_SPECIFICATION_CREATE);
		
		return render(JobSpecificationController.class, configuration, request, response, model, Template.JOB_SPECIFICATION_SELECT);
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
	
	private static String createScheduledJob(Configuration configuration, Request request, Response response) throws JsonParseException, JsonMappingException, IOException, ParseException {
		
		Token token = getToken(request);
		
		String jobTypeId = request.queryParams("jobTypeId");
		String connectorId = request.queryParams("connectorId");
		String instanceKey = request.queryParams("instanceKey");
		String notificationEmail = request.queryParams("notificationEmail");
		String description = request.queryParams("description");
		String scheduleDate = request.queryParams("scheduleDate");
		String seconds = request.queryParams("seconds");
		String minutes = request.queryParams("minutes");
		String hours = request.queryParams("hours");
		String dayOfMonth = request.queryParams("dayOfMonth");
		String month = request.queryParams("month");
		String dayOfWeek = request.queryParams("dayOfWeek");
		String year = request.queryParams("year");
		
		try {
			
			new SimpleDateFormat("yyyy-MM-dd").parse(scheduleDate);
			
		} catch (ParseException e) {
			LOGGER.error(e.getMessage());
			
			JobSpecification jobSpecification = new JobSpecification();
			
			Map<String, Object> model = getModel();
			model.put("step", "set-schedule");
			model.put("scheduledJob", jobSpecification);
			model.put("action", Path.Route.JOB_SPECIFICATION_CREATE);
			model.put("title", getLabel(JobSpecificationController.class, request, "schedule.job"));
			model.put("errorMessage", MessageProvider.getMessage(getLocale(request), "unparseable.date.time"));
			return render(JobSpecificationController.class, configuration, request, response, model, Template.JOB_SPECIFICATION_SELECT);
		}
		
		JobSpecificationRequest createjobSpecificationRequest = new JobSpecificationRequest()
				.withConnectorId(connectorId)
				.withNotificationEmail(notificationEmail)
				.withDescription(description)
				.withInstanceKey(instanceKey)
				.withJobTypeId(jobTypeId)
				.withStart(new SimpleDateFormat("yyyy-MM-dd").parse(scheduleDate))
				.withSeconds(seconds)
				.withMinutes(minutes)
				.withHours(hours)
				.withDayOfMonth(dayOfMonth)
				.withMonth(month)
				.withDayOfWeek(dayOfWeek)
				.withYear(year);
		
		CreateResult<JobSpecification> createjobSpecificationResult = new NowellpointClient(token)
				.jobSpecification()
				.create(createjobSpecificationRequest);
		
		if (! createjobSpecificationResult.isSuccess()) {
			
			JobSpecification jobSpecification = new JobSpecification();
			jobSpecification.setNotificationEmail(notificationEmail);
			jobSpecification.setDescription(description);
			jobSpecification.setStart(new SimpleDateFormat("yyyy-MM-dd").parse(scheduleDate));
			
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
	
	private static String viewJobSpecifications(Configuration configuration, Request request, Response response) {
		
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
	
	private static String editJobSpecification(Configuration configuration, Request request, Response response) {
		
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
	
	private static String updateScheduledJob(Configuration configuration, Request request, Response response) throws ParseException {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		String notificationEmail = request.queryParams("notificationEmail");
		String description = request.queryParams("description");
		String scheduleDate = request.queryParams("scheduleDate");
		String seconds = request.queryParams("seconds");
		String minutes = request.queryParams("minutes");
		String hours = request.queryParams("hours");
		String dayOfMonth = request.queryParams("dayOfMonth");
		String month = request.queryParams("month");
		String dayOfWeek = request.queryParams("dayOfWeek");
		String year = request.queryParams("year");
		
		JobSpecificationRequest jobSpecificationRequest = new JobSpecificationRequest()
				.withId(id)
				.withNotificationEmail(notificationEmail)
				.withDescription(description)
				.withStart(new SimpleDateFormat("yyyy-MM-dd").parse(scheduleDate))
				.withSeconds(seconds)
				.withMinutes(minutes)
				.withHours(hours)
				.withDayOfMonth(dayOfMonth)
				.withMonth(month)
				.withDayOfWeek(dayOfWeek)
				.withYear(year);

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