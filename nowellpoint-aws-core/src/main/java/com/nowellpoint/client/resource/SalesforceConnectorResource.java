package com.nowellpoint.client.resource;

import java.util.List;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.Environment;
import com.nowellpoint.client.model.NowellpointServiceException;

public class SalesforceConnectorResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "connectors";
	
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
				.path(RESOURCE_CONTEXT)
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
	
	public List<Environment> getEnvironments(String id) {
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
    			.path("salesforce")
    			.path(id)
    			.path("environments")
    			.execute();
		
		List<Environment> environments = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			environments = httpResponse.getEntityList(Environment.class);
		} else {
			throw new NowellpointServiceException(httpResponse.getStatusCode(), httpResponse.getAsString());
		}
		
		return environments;
	}
	
	public Environment getEnvironment(String id, String key) {
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
    			.path("salesforce")
    			.path(id)
    			.path("environment")
    			.path(key)
    			.execute();
		
		Environment environment = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			environment = httpResponse.getEntity(Environment.class);
		} else {
			throw new NowellpointServiceException(httpResponse.getStatusCode(), httpResponse.getAsString());
		}
		
		return environment;
	}
}