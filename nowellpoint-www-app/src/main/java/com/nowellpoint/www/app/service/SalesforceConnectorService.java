package com.nowellpoint.www.app.service;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.www.app.model.SalesforceConnector;

public class SalesforceConnectorService {

	public SalesforceConnector getSalesforceConnector(GetSalesforceConnectorRequest request) {
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
				.bearerAuthorization(request.getAccessToken())
				.path("connectors")
    			.path("salesforce")
    			.path(request.getId())
    			.execute();
		
		SalesforceConnector salesforceConnector = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			salesforceConnector = httpResponse.getEntity(SalesforceConnector.class);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
			throw new BadRequestException(httpResponse.getAsString());
		}
    	
    	return salesforceConnector;
	}
}