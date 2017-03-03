package com.nowellpoint.client.resource;

import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.JobSpecification;
import com.nowellpoint.client.model.JobSpecificationList;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.exception.NotFoundException;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;
import com.nowellpoint.client.model.JobSpecificationRequest;
import com.nowellpoint.client.model.Token;

public class JobSpecificationResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "job-specifications";
	
	public JobSpecificationResource(Token token) {
		super(token);
	}

	public JobSpecificationList getJobSpecifications() {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.execute();
		
		JobSpecificationList resources = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			resources = httpResponse.getEntity(JobSpecificationList.class);
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
		}
		
		return resources;
	}
	
	public CreateResult<JobSpecification> create(JobSpecificationRequest jobScheduleRequest) {		
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.parameter("jobTypeId", jobScheduleRequest.getJobTypeId())
				.parameter("connectorId", jobScheduleRequest.getConnectorId())
				.parameter("instanceKey", jobScheduleRequest.getInstanceKey())
				.parameter("notificationEmail", jobScheduleRequest.getNotificationEmail())
				.parameter("description", jobScheduleRequest.getDescription())
				.execute();
		
		CreateResult<JobSpecification> result = null;
		
		if (httpResponse.getStatusCode() == Status.CREATED) {
			JobSpecification resource = httpResponse.getEntity(JobSpecification.class);
			result = new CreateResultImpl<JobSpecification>(resource);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new CreateResultImpl<JobSpecification>(error);
		}
		
		return result;
	}
	
	/**
	 * @param jobScheduleRequest
	 * @return
	 */
	
	public UpdateResult<JobSpecification> update(JobSpecificationRequest jobScheduleRequest) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(jobScheduleRequest.getId())
				.parameter("notificationEmail", jobScheduleRequest.getNotificationEmail())
				.parameter("description", jobScheduleRequest.getDescription())
				.execute();
		
		UpdateResult<JobSpecification> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			JobSpecification jobSchedule = httpResponse.getEntity(JobSpecification.class);
			result = new UpdateResultImpl<JobSpecification>(jobSchedule);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new UpdateResultImpl<JobSpecification>(error);
		}
		
		return result;
	}
	
	public JobSpecification get(String id) {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.path(id)
				.execute();
		
		JobSpecification resource = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			resource = httpResponse.getEntity(JobSpecification.class);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
    	}
		
		return resource;
	}
	
	public DeleteResult delete(String id) {
		HttpResponse httpResponse = RestResource.delete(token.getEnvironmentUrl())
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