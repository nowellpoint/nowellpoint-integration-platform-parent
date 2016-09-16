package com.nowellpoint.client;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.PostRequest;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.client.model.NowellpointServiceException;
import com.nowellpoint.client.model.ScheduledJob;

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
	
	public ScheduledJob createScheduledJob(String environmentKey, String description, String jobTypeId, String connectorId, Date scheduleDate, Date scheduleTime) {
		PostRequest postRequest = RestResource.post(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT);
				
		if (Optional.ofNullable(environmentKey).isPresent()) {
			postRequest.parameter("environmentKey", environmentKey);
		}
		
		if (Optional.ofNullable(description).isPresent()) {
			postRequest.parameter("description", description);
		}
		
		if (Optional.ofNullable(connectorId).isPresent()) {
			postRequest.parameter("connectorId", connectorId);
		}
		
		if (Optional.ofNullable(jobTypeId).isPresent()) {
			postRequest.parameter("jobTypeId", jobTypeId);
		}
		
		if (Optional.ofNullable(connectorId).isPresent()) {
			postRequest.parameter("scheduleDate", scheduleDate != null ? dateFormat.format(scheduleDate) : null);
		}
		
		if (Optional.ofNullable(connectorId).isPresent()) {
			postRequest.parameter("scheduleTime", scheduleTime != null ? timeFormat.format(scheduleTime) : null);
		}
		
		HttpResponse httpResponse = postRequest.execute();
		
		ScheduledJob scheduledJob = null;
		
		if (httpResponse.getStatusCode() == Status.CREATED) {
			scheduledJob = httpResponse.getEntity(ScheduledJob.class);
		} else {
			throw new NowellpointServiceException(httpResponse.getStatusCode(), httpResponse.getAsString());
		}
		
		return scheduledJob;
	}
	
	public ScheduledJob updateScheduledJob(String id, String environmentKey, String description, String connectorId, Date scheduleDate, Date scheduleTime) {
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(id)
				.parameter("environmentKey", environmentKey)
				.parameter("description", description)
				.parameter("connectorId", connectorId)
				.parameter("scheduleDate", scheduleDate != null ? dateFormat.format(scheduleDate) : null)
				.parameter("scheduleTime", scheduleTime != null ? timeFormat.format(scheduleTime) : null)
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
}