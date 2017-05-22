package com.nowellpoint.client.resource;

import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.Job;
import com.nowellpoint.client.model.JobExecution;
import com.nowellpoint.client.model.JobList;
import com.nowellpoint.client.model.JobRequest;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.exception.NotFoundException;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

public class JobResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "jobs";
	//private static final String START = "start";
	
	public JobResource(Token token) {
		super(token);
	}
	
	public JobList getJobs() {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.header("Content-Type", MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
    			.execute();
		
		JobList resources = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			resources = httpResponse.getEntity(JobList.class);
    	} else {
    		throw new ServiceUnavailableException(httpResponse.getAsString());
    	}
    	
    	return resources;
	}

	public Job get(String id) {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
    			.path(id)
    			.execute();
		
		Job resource = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			resource = httpResponse.getEntity(Job.class);
    	} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
    	}
    	
    	return resource;
	}
	
	public UpdateResult<Job> run(String id) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
    			.path(id)
    			.path("actions")
    			.path("run")
    			.path("invoke")
    			.execute();
		
		UpdateResult<Job> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			Job resource = httpResponse.getEntity(Job.class);
			result = new UpdateResultImpl<Job>(resource);
    	} else {
    		Error error = httpResponse.getEntity(Error.class);
    		result = new UpdateResultImpl<Job>(error);
			throw new NotFoundException(httpResponse.getAsString());
		} 
    	
    	return result;
	}
	
	public UpdateResult<Job> testWebHookUrl(String id) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
    			.path(id)
    			.path("actions")
    			.path("test-webhook-url")
    			.path("invoke")
    			.execute();
		
		UpdateResult<Job> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			Job resource = httpResponse.getEntity(Job.class);
			result = new UpdateResultImpl<Job>(resource);
    	} else {
    		Error error = httpResponse.getEntity(Error.class);
    		result = new UpdateResultImpl<Job>(error);
			throw new NotFoundException(httpResponse.getAsString());
		} 
    	
    	return result;
		
	}
	
	public CreateResult<Job> create(JobRequest request) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.parameter("connectorId", request.getConnectorId())
				.parameter("jobTypeId", request.getJobTypeId())
    			.parameter("dayOfMonth", request.getDayOfMonth())
    			.parameter("dayOfWeek", request.getDayOfWeek())
    			.parameter("runAt", request.getRunAt())
    			.parameter("description", request.getDescription())
    			.parameter("hours", request.getHours())
    			.parameter("endAt", request.getEndAt())
    			.parameter("minutes", request.getMinutes())
    			.parameter("month", request.getMonth())
    			.parameter("notificationEmail", request.getNotificationEmail())
    			.parameter("slackWebhookUrl", request.getSlackWebhookUrl())
    			.parameter("scheduleOption", request.getScheduleOption())
    			.parameter("seconds", request.getSeconds())
    			.parameter("timeZone", request.getTimeZone())
    			.parameter("year", request.getYear())
    			.parameter("timeUnit", request.getTimeUnit())
    			.parameter("timeInterval", String.valueOf(request.getTimeInterval()))
    			.parameter("startAt", request.getStartAt())
    			.execute();
		
		CreateResult<Job> result = null;
    	
    	if (httpResponse.getStatusCode() == Status.CREATED) {
    		Job resource = httpResponse.getEntity(Job.class);
    		result = new CreateResultImpl<Job>(resource);
    	} else {
    		Error error = httpResponse.getEntity(Error.class);
    		result = new CreateResultImpl<Job>(error);
    	}
    	
    	return result;
	}
	
	public String downloadOutputFile(String id, String filename) {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.TEXT_PLAIN)
				.path(RESOURCE_CONTEXT)
    			.path(id)
    			.path("download")
    			.queryParameter("filename", filename)
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
	
	public JobExecutionResource jobExecution() {
		return new JobExecutionResource(token);
	}
	
	public class JobExecutionResource extends AbstractResource {

		public JobExecutionResource(Token token) {
			super(token);
		}
		
		public JobExecution get(String id, String fireInstanceId) {
			HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
					.bearerAuthorization(token.getAccessToken())
					.accept(MediaType.APPLICATION_JSON)
					.path(RESOURCE_CONTEXT)
	    			.path(id)
	    			.path("job-executions")
	    			.path(fireInstanceId)
	    			.execute();
			
			JobExecution resource = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				resource = httpResponse.getEntity(JobExecution.class);
	    	} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
				throw new NotFoundException(httpResponse.getAsString());
			} else {
				throw new ServiceUnavailableException(httpResponse.getAsString());
	    	}
	    	
	    	return resource;
		}
	}
}