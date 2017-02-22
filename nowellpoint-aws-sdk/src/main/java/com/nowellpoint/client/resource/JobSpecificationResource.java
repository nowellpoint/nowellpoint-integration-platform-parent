package com.nowellpoint.client.resource;

import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.RunHistory;
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
	private static final String START = "start";
	private static final String STOP = "stop";
	private static final String TERMINATE = "terminate";
	
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
				.parameter("start", formatDate(jobScheduleRequest.getStart()))
				.parameter("end", formatDate(jobScheduleRequest.getEnd()))
				.parameter("timeZone", jobScheduleRequest.getTimeZone())
				.parameter("seconds", jobScheduleRequest.getSeconds())
				.parameter("minutes", jobScheduleRequest.getMinutes())
				.parameter("hours", jobScheduleRequest.getHours())
				.parameter("dayOfMonth", jobScheduleRequest.getDayOfMonth())
				.parameter("month", jobScheduleRequest.getMonth())
				.parameter("dayOfWeek", jobScheduleRequest.getDayOfWeek())
				.parameter("year", jobScheduleRequest.getYear())
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
				.parameter("start", formatDate(jobScheduleRequest.getStart()))
				.parameter("end", formatDate(jobScheduleRequest.getEnd()))
				.parameter("timeZone", jobScheduleRequest.getTimeZone())
				.parameter("seconds", jobScheduleRequest.getSeconds())
				.parameter("minutes", jobScheduleRequest.getMinutes())
				.parameter("hours", jobScheduleRequest.getHours())
				.parameter("dayOfMonth", jobScheduleRequest.getDayOfMonth())
				.parameter("month", jobScheduleRequest.getMonth())
				.parameter("dayOfWeek", jobScheduleRequest.getDayOfWeek())
				.parameter("year", jobScheduleRequest.getYear())
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
	
	public UpdateResult<JobSpecification> stop(String id) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(id)
				.path("actions")
				.path(STOP)
				.path("invoke")
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
	
	public UpdateResult<JobSpecification> terminate(String id) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(id)
				.path("actions")
				.path(TERMINATE)
				.path("invoke")
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
	
	public UpdateResult<JobSpecification> start(String id) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(id)
				.path("actions")
				.path(START)
				.path("invoke")
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
	
	public RunHistoryResource runHistory() {
		return new RunHistoryResource(token);
	}
	
	public class RunHistoryResource extends AbstractResource {

		public RunHistoryResource(Token token) {
			super(token);
		}
		
		public RunHistory get(String scheduledJobId, String fireInstanceId) {
			HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
					.bearerAuthorization(token.getAccessToken())
					.path(RESOURCE_CONTEXT)
					.path(scheduledJobId)
					.path("run-history")
					.path(fireInstanceId)
					.execute();
			
			RunHistory resource = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				resource = httpResponse.getEntity(RunHistory.class);
			} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
				throw new NotFoundException(httpResponse.getAsString());
			} else {
				throw new ServiceUnavailableException(httpResponse.getAsString());
			}
			
			return resource;
		}
		
		public String getFile(String scheduledJobId, String fireInstanceId, String filename) {
			HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
					.bearerAuthorization(token.getAccessToken())
					.path(RESOURCE_CONTEXT)
					.path(scheduledJobId)
					.path("run-history")
					.path(fireInstanceId)
					.path("file")
					.path(filename)
					.execute();
			
			String resource = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				resource = httpResponse.getAsString();
			} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
				throw new NotFoundException(httpResponse.getAsString());
			} else {
				throw new ServiceUnavailableException(httpResponse.getAsString());
			}
			
			return resource;
		}
	}
}