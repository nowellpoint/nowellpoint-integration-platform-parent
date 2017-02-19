package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Instance;
import com.nowellpoint.client.model.JobScheduleList;
import com.nowellpoint.client.model.JobSchedule;
import com.nowellpoint.client.model.JobScheduleRequest;
import com.nowellpoint.client.model.RunHistory;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.SalesforceConnectorList;
import com.nowellpoint.client.model.ScheduledJobType;
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
		
		List<ScheduledJobType> scheduledJobTypes = new NowellpointClient(token)
				.scheduledJobType()
				.getScheduledJobTypesByLanguage(identity.getLanguageSidKey())
				.getItems();
		
		Map<String, Object> model = getModel();
		model.put("step", "select-type");
    	model.put("scheduledJobTypeList", scheduledJobTypes);
    	model.put("title", getLabel(JobScheduleController.class, request, "select.scheduled.job.type"));
    	
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
		
		String scheduledJobTypeId = request.queryParams("scheduledJobTypeId");
		
		JobScheduleRequest createScheduledJobRequest = new JobScheduleRequest()
				.withScheduledJobTypeId(scheduledJobTypeId);
		
		CreateResult<JobSchedule> createScheduledJobResult = new NowellpointClient(token)
				.jobSchedule()
				.create(createScheduledJobRequest);
		
		JobSchedule jobSchedule = createScheduledJobResult.getTarget();
		
		Map<String, Object> model = getModel();
		model.put("step", "select-connector");
		model.put("scheduledJob", jobSchedule);
    	model.put("title", getLabel(JobScheduleController.class, request, "select.connector"));
		
		if ("SALESFORCE_METADATA_BACKUP".equals(jobSchedule.getJobType().getCode())) {
			
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
		
		String connectorId = request.queryParams("connector-id");
		
		JobSchedule jobSchedule = new JobSchedule(); //objectMapper.readValue(getValue(token, id), ScheduledJob.class);
		jobSchedule.setConnectorId(connectorId);
		
		//putValue(token, scheduledJob.getId(), objectMapper.writeValueAsString(scheduledJob));
		
		Map<String, Object> model = getModel();
		model.put("step", "select-environment");
		model.put("scheduledJob", jobSchedule);
		model.put("title", getLabel(JobScheduleController.class, request, "select.environment"));
		
		if ("SALESFORCE_METADATA_BACKUP".equals(jobSchedule.getJobType().getCode())) {
			
			SalesforceConnector salesforceConnector = new NowellpointClient(token)
					.salesforceConnector()
					.get(connectorId);
			
			List<Instance> instances = salesforceConnector.getInstances();
			
			model.put("environments", instances);
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
		
		String environmentKey = request.queryParams("environment-key");
		
		JobSchedule jobSchedule = new JobSchedule(); //objectMapper.readValue(getValue(token, id), ScheduledJob.class);
		
		Instance instance = new NowellpointClient(token)
				.salesforceConnector()
				.instance()
				.get(jobSchedule.getConnectorId(), environmentKey);
		
		jobSchedule.setNotificationEmail(instance.getEmail());
		jobSchedule.setEnvironmentKey(instance.getKey());
		jobSchedule.setEnvironmentName(instance.getEnvironmentName());
		
		//putValue(token, scheduledJob.getId(), objectMapper.writeValueAsString(scheduledJob));
			
		Map<String, Object> model = getModel();
		model.put("step", "set-schedule");
		model.put("scheduledJob", jobSchedule);
		model.put("action", Path.Route.SCHEDULED_JOB_CREATE);
		model.put("title", getLabel(JobScheduleController.class, request, "schedule.job"));
		
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
		
		String notificationEmail = request.queryParams("notificationEmail");
		String description = request.queryParams("description");
		String scheduleDate = request.queryParams("scheduleDate");
		String scheduleTime = request.queryParams("scheduleTime");
		
		try {
			
			new SimpleDateFormat("yyyy-MM-dd").parse(scheduleDate);
			new SimpleDateFormat("HH:mm").parse(scheduleTime);
			
		} catch (ParseException e) {
			LOGGER.error(e.getMessage());
			
			JobSchedule jobSchedule = new JobSchedule(); //objectMapper.readValue(getValue(token, id), ScheduledJob.class);
			
			Map<String, Object> model = getModel();
			model.put("step", "set-schedule");
			model.put("scheduledJob", jobSchedule);
			model.put("action", Path.Route.SCHEDULED_JOB_CREATE);
			model.put("title", getLabel(JobScheduleController.class, request, "schedule.job"));
			model.put("errorMessage", MessageProvider.getMessage(getLocale(request), "unparseable.date.time"));
			return render(JobScheduleController.class, configuration, request, response, model, Template.SCHEDULED_JOB_SELECT);
		}

		JobSchedule jobSchedule = new JobSchedule(); //objectMapper.readValue(getValue(token, id), ScheduledJob.class);
		
		JobScheduleRequest createScheduledJobRequest = new JobScheduleRequest()
				.withConnectorId(jobSchedule.getConnectorId())
				.withNotificationEmail(notificationEmail)
				.withDescription(description)
				.withEnvironmentKey(jobSchedule.getEnvironmentKey())
				.withScheduledJobTypeId(jobSchedule.getId())
				.withScheduleDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", getLocale(request)).parse(scheduleDate.concat("T").concat(scheduleTime).concat(":00")));
		
		CreateResult<JobSchedule> createScheduledJobResult = new NowellpointClient(token)
				.jobSchedule()
				.create(createScheduledJobRequest);
		
		if (! createScheduledJobResult.isSuccess()) {
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"); 
			
			jobSchedule.setNotificationEmail(notificationEmail);
			jobSchedule.setDescription(description);
			jobSchedule.setScheduleDate(sdf.parse(scheduleDate.concat("T").concat(scheduleTime).concat(":00.SSSZ")));
			
			Map<String, Object> model = getModel();
			model.put("step", "set-schedule");
			model.put("scheduledJob", jobSchedule);
			model.put("action", Path.Route.SCHEDULED_JOB_CREATE);
			model.put("title", getLabel(JobScheduleController.class, request, "schedule.job"));
			model.put("errorMessage", createScheduledJobResult.getErrorMessage());
			return render(JobScheduleController.class, configuration, request, response, model, Template.SCHEDULED_JOB_SELECT);
		}
		
		//removeValue(token, id);
		
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
		model.put("connectorHref", jobSchedule.getConnectorId() != null ? Path.Route.CONNECTORS_SALESFORCE_VIEW.replace(":id", jobSchedule.getConnectorId()) : null);
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
		String environmentKey = request.queryParams("environmentKey");
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
			
			JobSchedule jobSchedule = new NowellpointClient(token)
					.jobSchedule()
					.get(id);
			
			String view = request.queryParams("view");
			
			Map<String, Object> model = getModel();
			model.put("scheduledJob", jobSchedule);
			model.put("mode", "edit");
			model.put("action", Path.Route.SCHEDULED_JOB_UPDATE.replace(":id", id));
			model.put("errorMessage", MessageProvider.getMessage(getLocale(request), "unparseable.date.time"));
			if (view != null && view.equals("1")) {
				model.put("cancel", Path.Route.SCHEDULED_JOBS_LIST);
			} else {
				model.put("cancel", Path.Route.SCHEDULED_JOB_VIEW.replace(":id", id));
			}
			return render(JobScheduleController.class, configuration, request, response, model, Template.SCHEDULED_JOB_EDIT);
		}	
		
		JobScheduleRequest jobScheduleRequest = new JobScheduleRequest()
				.withId(id)
				.withNotificationEmail(notificationEmail)
				.withDescription(description)
				.withEnvironmentKey(environmentKey)
				.withScheduleDate(new SimpleDateFormat("yyyy-MM-dd").parse(scheduleDate))
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
			model.put("scheduledJob", jobSchedule);
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