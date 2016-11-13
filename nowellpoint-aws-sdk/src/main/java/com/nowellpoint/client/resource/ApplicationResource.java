package com.nowellpoint.client.resource;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.model.Application;
import com.nowellpoint.client.model.NowellpointServiceException;
import com.nowellpoint.client.model.Token;

public class ApplicationResource extends AbstractResource {
	
	public ApplicationResource(Token token) {
		super(token);
	}
	
	public Application getApplication(String id) {
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("applications")
				.path(id)
				.execute();
		
		Application application = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			application = httpResponse.getEntity(Application.class);
		} else {
			throw new NowellpointServiceException(httpResponse.getStatusCode(), httpResponse.getAsString());
		}
		
		return application;
	}

	public void deleteApplication(String id) {
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("application")
				.path(id)
				.execute();
		
		if (httpResponse.getStatusCode() != Status.OK) {
			throw new NowellpointServiceException(httpResponse.getStatusCode(), httpResponse.getAsString());
		}
	}
}
