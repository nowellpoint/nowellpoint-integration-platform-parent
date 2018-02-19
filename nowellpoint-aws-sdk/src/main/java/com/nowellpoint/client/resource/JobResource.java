
/**
 * 
 * Copyright 2015-2016 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

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
	private static final String RUN = "run";
	private static final String SUBMIT = "submit";
	private static final String STOP = "stop";
	private static final String TERMINATE = "terminate";
	private static final String TEST_WEBHOOK_URL = "test-webhook-url";
	
	public JobResource(Token token) {
		super(token);
	}
	
	public JobList getJobs() {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.header("Content-Type", MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT).execute();

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
	
	public UpdateResult<Job> submit(String id) {
    	return invokeAction(id, SUBMIT);
	}
	
	public UpdateResult<Job> run(String id) {
    	return invokeAction(id, RUN);
	}
	
	public UpdateResult<Job> stop(String id) {
		return invokeAction(id, STOP);
	}
	
	public UpdateResult<Job> terminate(String id) {
		return invokeAction(id, TERMINATE);
	}
	
	public UpdateResult<Job> testWebHookUrl(String id) {
		return invokeAction(id, TEST_WEBHOOK_URL);
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
    			.parameter("timeZone", request.getTimeZone().getID())
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
	
	public UpdateResult<Job> update(String id, JobRequest request) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(id)
    			.parameter("description", request.getDescription())
    			.parameter("notificationEmail", request.getNotificationEmail())
    			.parameter("slackWebhookUrl", request.getSlackWebhookUrl())
    			.execute();
		
		UpdateResult<Job> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			Job resource = httpResponse.getEntity(Job.class);
			result = new UpdateResultImpl<Job>(resource);
    	} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
			Error error = httpResponse.getEntity(Error.class);
			result = new UpdateResultImpl<Job>(error);
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
	
	private UpdateResult<Job> invokeAction(String id, String action) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
    			.path(id)
    			.path("actions")
    			.path(action)
    			.path("invoke")
    			.execute();
		
		UpdateResult<Job> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			Job resource = httpResponse.getEntity(Job.class);
			result = new UpdateResultImpl<Job>(resource);
    	} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
			Error error = httpResponse.getEntity(Error.class);
			result = new UpdateResultImpl<Job>(error);
		}
    	
    	return result;
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