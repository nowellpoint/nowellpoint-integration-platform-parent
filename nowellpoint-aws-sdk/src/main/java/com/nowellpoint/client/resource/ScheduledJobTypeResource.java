package com.nowellpoint.client.resource;

import com.nowellpoint.client.model.ScheduledJobType;
import com.nowellpoint.client.model.ScheduledJobTypeList;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.exception.NotFoundException;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

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

	public ScheduledJobTypeList getScheduledJobTypesByLanguage(String language) {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.queryParameter("languageSidKey", language)
				.execute();
		
		ScheduledJobTypeList resources = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			resources = httpResponse.getEntity(ScheduledJobTypeList.class);
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
		}
		
		return resources;
	}
}