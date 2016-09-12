package com.nowellpoint.client;

import java.util.List;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.NowellpointServiceException;

public class SalesforceConnectorResource extends AbstractResource {
	
	public SalesforceConnectorResource(Token token) {
		super(token);
	}
	
	public List<SalesforceConnector> getSalesforceConnectors() {
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
    			.execute();
		
		List<SalesforceConnector> salesforceConnectors = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			salesforceConnectors = httpResponse.getEntityList(SalesforceConnector.class);
		} else {
			throw new NowellpointServiceException(httpResponse.getStatusCode(), httpResponse.getAsString());
		}
		
		return salesforceConnectors;
	}
	
	public SalesforceConnector getSalesforceConnector(String id) {
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
    			.path(id)
    			.execute();
		
		SalesforceConnector salesforceConnector = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			salesforceConnector = httpResponse.getEntity(SalesforceConnector.class);
		} else {
			throw new NowellpointServiceException(httpResponse.getStatusCode(), httpResponse.getAsString());
		}
    	
    	return salesforceConnector;
	}
}