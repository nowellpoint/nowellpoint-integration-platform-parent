package com.nowellpoint.client;

import java.util.List;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
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
		
		ScheduledJob scheduledJob = httpResponse.getEntity(ScheduledJob.class);
		
		return scheduledJob;
	}
	
	public ScheduledJob getScheduledJob(String id) {
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.path(id)
				.execute();
		
		ScheduledJob scheduledJob = httpResponse.getEntity(ScheduledJob.class);
		
		return scheduledJob;
	}
}