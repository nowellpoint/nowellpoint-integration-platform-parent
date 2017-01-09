package com.nowellpoint.client.resource;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.Environment;
import com.nowellpoint.client.model.Application;
import com.nowellpoint.client.model.NowellpointServiceException;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.exception.NotFoundException;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;

public class ApplicationResource extends AbstractResource {
	
	public ApplicationResource(Environment environment, Token token) {
		super(environment, token);
	}
	
	public Application getApplication(String id) {
		HttpResponse httpResponse = RestResource.get(environment.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.path("applications")
				.path(id)
				.execute();
		
		Application application = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			application = httpResponse.getEntity(Application.class);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
    	}
		
		return application;
	}

	public void deleteApplication(String id) {
		HttpResponse httpResponse = RestResource.delete(environment.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.path("application")
				.path(id)
				.execute();
		
		if (httpResponse.getStatusCode() != Status.OK) {
			throw new NowellpointServiceException(httpResponse.getStatusCode(), httpResponse.getAsString());
		}
	}
}
