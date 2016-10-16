package com.nowellpoint.client.resource;

import java.util.List;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.model.CreateScheduledJobRequest;
import com.nowellpoint.client.model.CreateScheduledJobResult;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.GetResult;
import com.nowellpoint.client.model.NotFoundException;
import com.nowellpoint.client.model.ScheduledJob;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.UpdateScheduledJobRequest;
import com.nowellpoint.client.model.UpdateScheduledJobResult;
import com.nowellpoint.client.model.idp.Token;

public class ScheduledJobResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "scheduled-jobs";
	
	public ScheduledJobResource(Token token) {
		super(token);
	}

	public GetResult<List<ScheduledJob>> getScheduledJobs() {
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.execute();
		
		GetResult<List<ScheduledJob>> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			List<ScheduledJob> scheduledJobs = httpResponse.getEntityList(ScheduledJob.class);
			result = new GetResultImpl<List<ScheduledJob>>(scheduledJobs);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new GetResultImpl<List<ScheduledJob>>(error);
		}
		
		return result;
	}
	
	public CreateScheduledJobResult create(CreateScheduledJobRequest createScheduledJobRequest) {		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.parameter("environmentKey", createScheduledJobRequest.getEnvironmentKey())
				.parameter("notificationEmail", createScheduledJobRequest.getNotificationEmail())
				.parameter("description", createScheduledJobRequest.getDescription())
				.parameter("connectorId", createScheduledJobRequest.getConnectorId())
				.parameter("jobTypeId", createScheduledJobRequest.getJobTypeId())
				.parameter("scheduleDate", dateFormat.format(createScheduledJobRequest.getScheduleDate()))
				.execute();
		
		CreateScheduledJobResult createScheduledJobResult = new CreateScheduledJobResult();
		
		if (httpResponse.getStatusCode() == Status.CREATED) {
			ScheduledJob scheduledJob = httpResponse.getEntity(ScheduledJob.class);
			createScheduledJobResult.setScheduledJob(scheduledJob);
			createScheduledJobResult.setIsSuccess(Boolean.TRUE);
		} else {
			Error error = httpResponse.getEntity(Error.class);
			createScheduledJobResult.setError(error.getCode());
			createScheduledJobResult.setErrorMessage(error.getMessage());
			createScheduledJobResult.setIsSuccess(Boolean.FALSE);
		}
		
		return createScheduledJobResult;
	}
	
	public UpdateScheduledJobResult update(UpdateScheduledJobRequest updateScheduledJobRequest) {
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(updateScheduledJobRequest.getId())
				.parameter("environmentKey", updateScheduledJobRequest.getEnvironmentKey())
				.parameter("notificationEmail", updateScheduledJobRequest.getNotificationEmail())
				.parameter("description", updateScheduledJobRequest.getDescription())
				.parameter("connectorId", updateScheduledJobRequest.getConnectorId())
				.parameter("scheduleDate", dateFormat.format(updateScheduledJobRequest.getScheduleDate()))
				.execute();
		
		UpdateScheduledJobResult updateScheduledJobResult = new UpdateScheduledJobResult();
		
		if (httpResponse.getStatusCode() == Status.OK) {
			ScheduledJob scheduledJob = httpResponse.getEntity(ScheduledJob.class);
			updateScheduledJobResult.setScheduledJob(scheduledJob);
			updateScheduledJobResult.setIsSuccess(Boolean.TRUE);
		} else {
			Error error = httpResponse.getEntity(Error.class);
			updateScheduledJobResult.setError(error.getCode());
			updateScheduledJobResult.setErrorMessage(error.getMessage());
			updateScheduledJobResult.setIsSuccess(Boolean.FALSE);
		}
		
		return updateScheduledJobResult;
	}
	
	public UpdateScheduledJobResult stop(String id) {
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(id)
				.parameter("status", "Stopped")
				.execute();
		
		UpdateScheduledJobResult updateScheduledJobResult = new UpdateScheduledJobResult();
		
		if (httpResponse.getStatusCode() == Status.OK) {
			ScheduledJob scheduledJob = httpResponse.getEntity(ScheduledJob.class);
			updateScheduledJobResult.setScheduledJob(scheduledJob);
			updateScheduledJobResult.setIsSuccess(Boolean.TRUE);
		} else {
			Error error = httpResponse.getEntity(Error.class);
			updateScheduledJobResult.setError(error.getCode());
			updateScheduledJobResult.setErrorMessage(error.getMessage());
			updateScheduledJobResult.setIsSuccess(Boolean.FALSE);
		}
		
		return updateScheduledJobResult;
	}
	
	public UpdateResult<ScheduledJob> terminate(String id) {
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(id)
				.parameter("status", "Terminated")
				.execute();
		
		UpdateResult<ScheduledJob> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			ScheduledJob scheduledJob = httpResponse.getEntity(ScheduledJob.class);
			result = new UpdateResultImpl<ScheduledJob>(scheduledJob);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new UpdateResultImpl<ScheduledJob>(error);
		}
		
		return result;
	}
	
	public UpdateResult<ScheduledJob> start(String id) {
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(id)
				.parameter("status", "Scheduled")
				.execute();
		
		UpdateResult<ScheduledJob> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			ScheduledJob scheduledJob = httpResponse.getEntity(ScheduledJob.class);
			result = new UpdateResultImpl<ScheduledJob>(scheduledJob);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new UpdateResultImpl<ScheduledJob>(error);
		}
		
		return result;
	}
	
	public GetResult<ScheduledJob> get(String id) {
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.path(id)
				.execute();
		
		GetResult<ScheduledJob> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			ScheduledJob scheduledJob = httpResponse.getEntity(ScheduledJob.class);
			result = new GetResultImpl<ScheduledJob>(scheduledJob);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new GetResultImpl<ScheduledJob>(error);
		}
		
		return result;
	}
	
	public DeleteResult delete(String id) {
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.path(id)
				.execute();
		
		DeleteResult result = null;
		
		if (httpResponse.getStatusCode() == Status.NO_CONTENT) {
			result = new DeleteResultImpl();
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new DeleteResultImpl(error);
		}
		
		return result;
	}
}