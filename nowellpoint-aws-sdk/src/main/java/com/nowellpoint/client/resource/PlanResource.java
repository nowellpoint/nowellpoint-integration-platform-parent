package com.nowellpoint.client.resource;

import java.util.Collections;
import java.util.List;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.model.GetPlansRequest;
import com.nowellpoint.client.model.Plan;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.exception.NotFoundException;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;

public class PlanResource extends AbstractResource {
	
	public PlanResource(Token token) {
		super(token);
	}
	
	public PlanResource(String environmentUrl) {
		super(environmentUrl);
	}
	
	public Plan get(String id) {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
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
	
	public List<Plan> getPlans(GetPlansRequest getPlansRequest) {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.path("plans")
				.queryParameter("localeSidKey", getPlansRequest.getLocaleSidKey())
				.queryParameter("languageLocaleKey", getPlansRequest.getLanguageSidKey())
				.execute();
		
		List<Plan> resources = Collections.emptyList();
		
		if (httpResponse.getStatusCode() == Status.OK) {
			resources = httpResponse.getEntityList(Plan.class);
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
		}
		
		return resources;
	}
}