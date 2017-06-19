package com.nowellpoint.client.resource;

import com.nowellpoint.client.model.Dashboard;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.exception.NotFoundException;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

public class DashboardResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "dashboards";

	public DashboardResource(Token token) {
		super(token);
	}

	public Dashboard get() {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.execute();
		
		Dashboard resource = null;
    	
    	if (httpResponse.getStatusCode() == Status.OK) {
    		resource = httpResponse.getEntity(Dashboard.class);
    	} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
    	}
    	
    	return resource;
	}
}