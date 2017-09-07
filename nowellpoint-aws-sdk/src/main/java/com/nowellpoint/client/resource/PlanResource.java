package com.nowellpoint.client.resource;

import com.nowellpoint.client.Environment;
import com.nowellpoint.client.model.GetPlansRequest;
import com.nowellpoint.client.model.Plan;
import com.nowellpoint.client.model.PlanList;
import com.nowellpoint.client.model.exception.NotFoundException;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

public class PlanResource extends AbstractResource {
	
	public PlanResource(Environment environment) {
		super(environment);
	}
	
	public Plan get(String id) {
		HttpResponse httpResponse = RestResource.get(environment.getEnvironmentUrl())
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
	
	public PlanList getPlans(GetPlansRequest request) {
		HttpResponse httpResponse = RestResource.get(environment.getEnvironmentUrl())
				.path("plans")
				.queryParameter("locale", request.getLocale())
				.queryParameter("language", request.getLanguage())
				.execute();
		
		PlanList resources = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			resources = httpResponse.getEntity(PlanList.class);
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
		}
		
		return resources;
	}
}