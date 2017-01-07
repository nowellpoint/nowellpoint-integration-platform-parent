package com.nowellpoint.client.resource;

import java.util.List;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.Environment;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.GetResult;
import com.nowellpoint.client.model.ScheduledJobType;
import com.nowellpoint.client.model.Token;

public class ScheduledJobTypeResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "scheduled-job-types";

	public ScheduledJobTypeResource(Environment environment, Token token) {
		super(environment, token);
	}
	
	public GetResult<ScheduledJobType> get(String id) {
		HttpResponse httpResponse = RestResource.get(environment.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.path(id)
				.execute();
		
		GetResult<ScheduledJobType> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			ScheduledJobType resource = httpResponse.getEntity(ScheduledJobType.class);
			result = new GetResultImpl<ScheduledJobType>(resource);
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new GetResultImpl<ScheduledJobType>(error);
		}
		
		return result;
	}

	public GetResult<List<ScheduledJobType>> getScheduledJobTypesByLanguage(String language) {
		HttpResponse httpResponse = RestResource.get(environment.getEnvironmentUrl())
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