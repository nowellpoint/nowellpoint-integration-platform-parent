package com.nowellpoint.client.resource;

import java.util.List;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.GetResult;
import com.nowellpoint.client.model.ScheduledJobType;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.exception.NotFoundException;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;

public class ScheduledJobTypeResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "scheduled-job-types";

	public ScheduledJobTypeResource(Token token) {
		super(token);
	}
	
	public ScheduledJobType get(String id) {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.path(id)
				.execute();
		
		ScheduledJobType resource = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			resource = httpResponse.getEntity(ScheduledJobType.class);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
    	}
		
		return resource;
	}

	public GetResult<List<ScheduledJobType>> getScheduledJobTypesByLanguage(String language) {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.queryParameter("languageSidKey", language)
				.execute();
		
		GetResult<List<ScheduledJobType>> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			List<ScheduledJobType> resources = httpResponse.getEntityList(ScheduledJobType.class);
			result = new GetResultImpl<List<ScheduledJobType>>(resources);
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new GetResultImpl<List<ScheduledJobType>>(error);
		}
		
		return result;
	}
}