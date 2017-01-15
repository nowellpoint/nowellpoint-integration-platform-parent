package com.nowellpoint.client.resource;

import java.util.List;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.GetPlansRequest;
import com.nowellpoint.client.model.GetResult;
import com.nowellpoint.client.model.Plan;
import com.nowellpoint.client.model.exception.NotFoundException;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;

public class PlanResource extends AbstractResource {
	
	public PlanResource(String environmentUrl) {
		super(environmentUrl);
	}
	
	public Plan get(String id) {
		HttpResponse httpResponse = RestResource.get(environmentUrl)
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
	
	public GetResult<List<Plan>> getPlans(GetPlansRequest getPlansRequest) {
		HttpResponse httpResponse = RestResource.get(environmentUrl)
				.path("plans")
				.queryParameter("localeSidKey", getPlansRequest.getLocaleSidKey())
				.queryParameter("languageLocaleKey", getPlansRequest.getLanguageSidKey())
				.execute();
		
		GetResult<List<Plan>> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			List<Plan> plans = httpResponse.getEntityList(Plan.class);
			result = new GetResultImpl<List<Plan>>(plans);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new GetResultImpl<List<Plan>>(error);
		}
		
		return result;
	}
}