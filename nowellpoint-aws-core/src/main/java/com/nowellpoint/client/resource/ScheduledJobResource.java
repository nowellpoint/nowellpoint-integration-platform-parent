package com.nowellpoint.client.resource;

import java.util.List;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.model.CreateScheduledJobRequest;
import com.nowellpoint.client.model.CreateScheduledJobResult;
import com.nowellpoint.client.model.DeleteScheduledJobRequest;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.NowellpointServiceException;
import com.nowellpoint.client.model.ScheduledJob;
import com.nowellpoint.client.model.UpdateScheduledJobRequest;
import com.nowellpoint.client.model.UpdateScheduledJobResult;
import com.nowellpoint.client.model.idp.Token;

public class ScheduledJobResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "scheduled-jobs";
	
	public ScheduledJobResource(Token token) {
		super(token);
	}

	public List<ScheduledJob> getScheduledJobs() {
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.execute();
		
		List<ScheduledJob> scheduledJobs = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			scheduledJobs = httpResponse.getEntityList(ScheduledJob.class);
		} else {
			throw new NowellpointServiceException(httpResponse.getStatusCode(), httpResponse.getAsString());
		}
		
		return scheduledJobs;
	}
	
	public CreateScheduledJobResult createScheduledJob(CreateScheduledJobRequest createScheduledJobRequest) {		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.parameter("environmentKey", createScheduledJobRequest.getEnvironmentKey())
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
	
	public UpdateScheduledJobResult updateScheduledJob(UpdateScheduledJobRequest updateScheduledJobRequest) {
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(updateScheduledJobRequest.getId())
				.parameter("environmentKey", updateScheduledJobRequest.getEnvironmentKey())
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
	
	public UpdateScheduledJobResult stopScheduledJob(String id) {
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
	
	public UpdateScheduledJobResult startScheduledJob(String id) {
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(id)
				.parameter("status", "Scheduled")
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
	
	public ScheduledJob getScheduledJob(String id) {
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.path(id)
				.execute();
		
		ScheduledJob scheduledJob = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			scheduledJob = httpResponse.getEntity(ScheduledJob.class);
		} else {
			throw new NowellpointServiceException(httpResponse.getStatusCode(), httpResponse.getAsString());
		}
		
		return scheduledJob;
	}
	
	public void deleteScheduledJob(DeleteScheduledJobRequest deleteScheduledJobRequest) {
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.path(deleteScheduledJobRequest.getId())
				.execute();
		
		if (httpResponse.getStatusCode() != Status.NO_CONTENT) {
			throw new NowellpointServiceException(httpResponse.getStatusCode(), httpResponse.getAsString());
		}
	}
}