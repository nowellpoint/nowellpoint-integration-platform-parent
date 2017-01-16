package com.nowellpoint.client.resource;

import java.util.Collections;
import java.util.List;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.RunHistory;
import com.nowellpoint.client.model.ScheduledJob;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.exception.NotFoundException;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.client.model.ScheduledJobRequest;
import com.nowellpoint.client.model.Token;

public class ScheduledJobResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "scheduled-jobs";
	private static final String START = "start";
	private static final String STOP = "stop";
	private static final String TERMINATE = "terminate";
	
	public ScheduledJobResource(Token token) {
		super(token);
	}

	public List<ScheduledJob> getScheduledJobs() {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.execute();
		
		List<ScheduledJob> resources = Collections.emptyList();
		
		if (httpResponse.getStatusCode() == Status.OK) {
			resources = httpResponse.getEntityList(ScheduledJob.class);
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
		}
		
		return resources;
	}
	
	public CreateResult<ScheduledJob> create(ScheduledJobRequest scheduledJobRequest) {		
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.parameter("environmentKey", scheduledJobRequest.getEnvironmentKey())
				.parameter("notificationEmail", scheduledJobRequest.getNotificationEmail())
				.parameter("description", scheduledJobRequest.getDescription())
				.parameter("connectorId", scheduledJobRequest.getConnectorId())
				.parameter("jobTypeId", scheduledJobRequest.getJobTypeId())
				.parameter("scheduleDate", dateFormat.format(scheduledJobRequest.getScheduleDate()))
				.execute();
		
		CreateResult<ScheduledJob> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			ScheduledJob resource = httpResponse.getEntity(ScheduledJob.class);
			result = new CreateResultImpl<ScheduledJob>(resource);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new CreateResultImpl<ScheduledJob>(error);
		}
		
		return result;
	}
	
	public UpdateResult<ScheduledJob> update(ScheduledJobRequest scheduledJobRequest) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(scheduledJobRequest.getId())
				.parameter("environmentKey", scheduledJobRequest.getEnvironmentKey())
				.parameter("notificationEmail", scheduledJobRequest.getNotificationEmail())
				.parameter("description", scheduledJobRequest.getDescription())
				.parameter("connectorId", scheduledJobRequest.getConnectorId())
				.parameter("scheduleDate", dateFormat.format(scheduledJobRequest.getScheduleDate()))
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
	
	public UpdateResult<ScheduledJob> stop(String id) {
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
	
	public UpdateResult<ScheduledJob> terminate(String id) {
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
	
	public ScheduledJob get(String id) {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.path(id)
				.execute();
		
		ScheduledJob resource = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			resource = httpResponse.getEntity(ScheduledJob.class);
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