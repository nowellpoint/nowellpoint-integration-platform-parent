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
import com.nowellpoint.client.model.JobScheduleList;
import com.nowellpoint.client.model.JobSchedule;
import com.nowellpoint.client.model.JobScheduleRequest;
import com.nowellpoint.client.model.JobType;
import com.nowellpoint.client.model.JobTypeInfo;
import com.nowellpoint.client.model.RunHistory;
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

public class JobScheduleController extends AbstractStaticController {
	
	private static final Logger LOGGER = Logger.getLogger(JobScheduleController.class.getName());
	
	public static class Template {
		public static final String SCHEDULED_JOBS_LIST = String.format(APPLICATION_CONTEXT, "scheduled-jobs-list.html");
		public static final String SCHEDULED_JOB_SELECT = String.format(APPLICATION_CONTEXT, "scheduled-job-create.html");
		public static final String SCHEDULED_JOB_EDIT = String.format(APPLICATION_CONTEXT, "scheduled-job-edit.html");
		public static final String SCHEDULED_JOB = String.format(APPLICATION_CONTEXT, "scheduled-job.html");
		public static final String SCHEDULE_JOB_RUN_HISTORY = String.format(APPLICATION_CONTEXT, "scheduled-job-run-detail.html");
		public static final String SCHEDULE = String.format(APPLICATION_CONTEXT, "schedule.html");
	}
	
	public static void configureRoutes(Configuration configuration) {
        get(Path.Route.SCHEDULED_JOBS_LIST, (request, response) -> listJobSchedules(configuration, request, response));
        get(Path.Route.SCHEDULED_JOB_SELECT_TYPE, (request, response) -> listJobTypes(configuration, request, response));
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
	
	private static String listJobSchedules(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		JobScheduleList list = new NowellpointClient(token)
				.jobSchedule()
				.getJobSchedules();
		
		Map<String, Object> model = getModel();
		model.put("jobScheduleList", list.getItems());

		return render(JobScheduleController.class, configuration, request, response, model, Template.SCHEDULED_JOBS_LIST);
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
    	
    	return render(JobScheduleController.class, configuration, request, response, model, Template.SCHEDULED_JOB_SELECT);
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
		
    	return render(JobScheduleController.class, configuration, request, response, model, Template.SCHEDULED_JOB_SELECT);
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
		
    	return render(JobScheduleController.class, configuration, request, response, model, Template.SCHEDULED_JOB_SELECT);
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
		
		JobSchedule jobSchedule = new JobSchedule();
		jobSchedule.setStart(new Date());
		jobSchedule.setJobType(jobTypeInfo);
		
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
				
				jobSchedule.setConnector(connectorInfo);
				jobSchedule.setNotificationEmail(instance.getEmail());

			}
		}

		model.put("jobSchedule", jobSchedule);
		model.put("action", Path.Route.SCHEDULED_JOB_CREATE);
		
		return render(JobScheduleController.class, configuration, request, response, model, Template.SCHEDULED_JOB_SELECT);
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
			
			JobSchedule jobSchedule = new JobSchedule();
			
			Map<String, Object> model = getModel();
			model.put("step", "set-schedule");
			model.put("scheduledJob", jobSchedule);
			model.put("action", Path.Route.SCHEDULED_JOB_CREATE);
			model.put("title", getLabel(JobScheduleController.class, request, "schedule.job"));
			model.put("errorMessage", MessageProvider.getMessage(getLocale(request), "unparseable.date.time"));
			return render(JobScheduleController.class, configuration, request, response, model, Template.SCHEDULED_JOB_SELECT);
		}
		
		JobScheduleRequest createJobScheduleRequest = new JobScheduleRequest()
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
		
		CreateResult<JobSchedule> createJobScheduleResult = new NowellpointClient(token)
				.jobSchedule()
				.create(createJobScheduleRequest);
		
		if (! createJobScheduleResult.isSuccess()) {
			
			JobSchedule jobSchedule = new JobSchedule();
			jobSchedule.setNotificationEmail(notificationEmail);
			jobSchedule.setDescription(description);
			jobSchedule.setStart(new SimpleDateFormat("yyyy-MM-dd").parse(scheduleDate));
			
			Map<String, Object> model = getModel();
			model.put("step", "set-schedule");
			model.put("scheduledJob", jobSchedule);
			model.put("action", Path.Route.SCHEDULED_JOB_CREATE);
			model.put("errorMessage", createJobScheduleResult.getErrorMessage());
			return render(JobScheduleController.class, configuration, request, response, model, Template.SCHEDULED_JOB_SELECT);
		}
		
		response.redirect(Path.Route.SCHEDULED_JOB_VIEW.replace(":id", createJobScheduleResult.getTarget().getId()));
		
		return "";
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String viewScheduledJob(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		JobSchedule jobSchedule = new NowellpointClient(token)
				.jobSchedule()
				.get(id);
		
		String createdByHref = Path.Route.ACCOUNT_PROFILE.replace(":id", jobSchedule.getCreatedBy().getId());
		String lastModifiedByHref = Path.Route.ACCOUNT_PROFILE.replace(":id", jobSchedule.getLastUpdatedBy().getId());
		
		Map<String, Object> model = getModel();
		model.put("jobSchedule", jobSchedule);
		model.put("createdByHref", createdByHref);
		model.put("lastModifiedByHref", lastModifiedByHref);
		model.put("connectorHref", jobSchedule.getConnector() != null ? Path.Route.CONNECTORS_SALESFORCE_VIEW.replace(":id", jobSchedule.getConnector().getId()) : null);
		model.put("action", Path.Route.SCHEDULED_JOB_UPDATE.replace(":id", id));
		model.put("successMessage", null); //getValue(token, "success.message"));
		
		if (model.get("successMessage") != null) {
			//removeValue(token, "success.message");
		}
		
		return render(JobScheduleController.class, configuration, request, response, model, Template.SCHEDULED_JOB);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String editScheduledJob(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		String view = request.queryParams("view");
		
		Token token = getToken(request);
		
		JobSchedule jobSchedule = new NowellpointClient(token)
				.jobSchedule()
				.get(id);
		
		Map<String, Object> model = getModel();
		model.put("jobSchedule", jobSchedule);
		model.put("mode", "edit");
		model.put("action", Path.Route.SCHEDULED_JOB_UPDATE.replace(":id", id));
		
		if (view != null && view.equals("1")) {
			model.put("cancel", Path.Route.SCHEDULED_JOBS_LIST);
		} else {
			model.put("cancel", Path.Route.SCHEDULED_JOB_VIEW.replace(":id", id));
		}
		
		return render(JobScheduleController.class, configuration, request, response, model, Template.SCHEDULED_JOB_EDIT);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String startScheduledJob(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		UpdateResult<JobSchedule> updateResult = new NowellpointClient(token)
				.jobSchedule() 
				.start(id);
		
		//putValue(token, "success.message", MessageProvider.getMessage(getLocale(request), "activate.scheduled.job.success"));
		
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
	
	private static String stopScheduledJob(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		JobSchedule jobSchedule = new NowellpointClient(token)
				.jobSchedule() 
				.stop(id)
				.getTarget();
		
		//putValue(token, "success.message", MessageProvider.getMessage(getLocale(request), "deactivate.scheduled.job.success"));
		
		response.redirect(Path.Route.SCHEDULED_JOB_VIEW.replace(":id", jobSchedule.getId()));
		
		return "";
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String terminateScheduledJob(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		UpdateResult<JobSchedule> updateResult = new NowellpointClient(token)
				.jobSchedule() 
				.terminate(id);
		
		//putValue(token, "success.message", MessageProvider.getMessage(getLocale(request), "terminate.scheduled.job.success"));
		
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
		
		JobScheduleRequest jobScheduleRequest = new JobScheduleRequest()
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

		UpdateResult<JobSchedule> updateResult = new NowellpointClient(token)
				.jobSchedule() 
				.update(jobScheduleRequest);
		
		if (! updateResult.isSuccess()) {
			
			JobSchedule jobSchedule = new NowellpointClient(token)
					.jobSchedule()
					.get(id);
			
			String view = request.queryParams("view");
			
			Map<String, Object> model = getModel();
			model.put("jobSchedule", jobSchedule);
			model.put("mode", "edit");
			model.put("action", Path.Route.SCHEDULED_JOB_UPDATE.replace(":id", id));
			model.put("errorMessage", updateResult.getErrorMessage());
			if (view != null && view.equals("1")) {
				model.put("cancel", Path.Route.SCHEDULED_JOBS_LIST);
			} else {
				model.put("cancel", Path.Route.SCHEDULED_JOB_VIEW.replace(":id", id));
			}
			return render(JobScheduleController.class, configuration, request, response, model, Template.SCHEDULED_JOB_EDIT);
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
	
	private static String getRunHistory(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		String fireInstanceId = request.params(":fireInstanceId");
		
		RunHistory runHistory = new NowellpointClient(token)
				.jobSchedule()
				.runHistory()
				.get(id, fireInstanceId);
		
		Map<String, Object> model = getModel();
		model.put("scheduledJob", new JobSchedule(id));
		model.put("runHistory", runHistory);
		
		return render(JobScheduleController.class, configuration, request, response, model, Template.SCHEDULE_JOB_RUN_HISTORY);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String downloadFile(Configuration configuration, Request request, Response response) {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		String fireInstanceId = request.params(":fireInstanceId");
		String filename = request.params(":filename");
		
		String file = new NowellpointClient(token)
				.jobSchedule()
				.runHistory()
				.getFile(id, fireInstanceId, filename);
		
		return file;
	}
}