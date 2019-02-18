package com.nowellpoint.client.resource;

import com.nowellpoint.client.Environment;
import com.nowellpoint.client.model.Plan;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.exception.NotFoundException;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

public class PlanResource extends AbstractResource {
	
	public PlanResource(Environment environment) {
		super(environment);
	}
	
	public PlanResource(Token token) {
		super(token);
	}
	
	public Plan get(String id) {
		HttpResponse httpResponse = RestResource.get(token != null ? token.getEnvironmentUrl() : environment.getEnvironmentUrl())
				.path("plans")
				.path(id)
				.execute();
		
		Plan resource = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			resource = httpResponse.getEntity(Plan.class);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
		}
		
		return resource;
	}
}