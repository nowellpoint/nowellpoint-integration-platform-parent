package com.nowellpoint.client;

import java.util.List;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.client.model.NowellpointServiceException;
import com.nowellpoint.client.model.Schedule;
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
	
	public ScheduledJob createScheduledJob(String name, String description, String connectorType, String connectorId) {
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.parameter("name", name)
				.parameter("description", description)
				.parameter("connectorId", connectorId)
				.parameter("connectorType", connectorType)
				.execute();
		
		ScheduledJob scheduledJob = null;
		
		if (httpResponse.getStatusCode() == Status.CREATED) {
			scheduledJob = httpResponse.getEntity(ScheduledJob.class);
		} else {
			throw new NowellpointServiceException(httpResponse.getStatusCode(), httpResponse.getAsString());
		}
		
		return scheduledJob;
	}
	
	public ScheduledJob updateScheduledJob(String id, String name, String description) {
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(id)
				.parameter("name", name)
				.parameter("description", description)
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
	
	public Schedule getSchedule(String id, String key) {
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.path(id)
				.path("schedules")
				.path(key)
				.execute();
		
		Schedule schedule = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			schedule = httpResponse.getEntity(Schedule.class);
		} else {
			throw new NowellpointServiceException(httpResponse.getStatusCode(), httpResponse.getAsString());
		}
		
		return schedule;
	}
}