package com.nowellpoint.client.resource;

import java.util.List;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.client.model.CreateScheduledJobRequest;
import com.nowellpoint.client.model.NowellpointServiceException;
import com.nowellpoint.client.model.ScheduledJob;
import com.nowellpoint.client.model.UpdateScheduledJobRequest;

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
	
	public ScheduledJob createScheduledJob(CreateScheduledJobRequest createScheduledJobRequest) {		
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
		
		ScheduledJob scheduledJob = null;
		
		if (httpResponse.getStatusCode() == Status.CREATED) {
			scheduledJob = httpResponse.getEntity(ScheduledJob.class);
		} else {
			throw new NowellpointServiceException(httpResponse.getStatusCode(), httpResponse.getAsString());
		}
		
		return scheduledJob;
	}
	
	public ScheduledJob updateScheduledJob(UpdateScheduledJobRequest updateScheduledJobRequest) {
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
		
		ScheduledJob scheduledJob = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			scheduledJob = httpResponse.getEntity(ScheduledJob.class);
		} else {
			throw new NowellpointServiceException(httpResponse.getStatusCode(), httpResponse.getAsString());
		}
		
		return scheduledJob;
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
	
	public void deleteScheduledJob(String id) {
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.path(id)
				.execute();
		
		if (httpResponse.getStatusCode() != Status.NO_CONTENT) {
			throw new NowellpointServiceException(httpResponse.getStatusCode(), httpResponse.getAsString());
		}
	}
}