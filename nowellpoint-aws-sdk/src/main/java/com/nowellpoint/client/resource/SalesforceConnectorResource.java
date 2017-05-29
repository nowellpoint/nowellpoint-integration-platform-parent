package com.nowellpoint.client.resource;

import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.CreateSalesforceConnectorRequest;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.SalesforceConnectorList;
import com.nowellpoint.client.model.SalesforceConnectorRequest;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.exception.NotFoundException;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

public class SalesforceConnectorResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "salesforce-connectors";
	
	public SalesforceConnectorResource(Token token) {
		super(token);
	}
	
	public CreateResult<SalesforceConnector> create(CreateSalesforceConnectorRequest request) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
    			.parameter("id", request.getId())
    			.parameter("instanceUrl", request.getInstanceUrl())
    			.parameter("accessToken", request.getAccessToken())
    			.parameter("refreshToken", request.getRefreshToken())
    			.execute();
		
		CreateResult<SalesforceConnector> result = null;
    	
    	if (httpResponse.getStatusCode() == Status.CREATED) {
    		SalesforceConnector resource = httpResponse.getEntity(SalesforceConnector.class);
    		result = new CreateResultImpl<SalesforceConnector>(resource);
    	} else {
    		Error error = httpResponse.getEntity(Error.class);
    		result = new CreateResultImpl<SalesforceConnector>(error);
    	}
    	
    	return result;
	}
	
	public SalesforceConnectorList getSalesforceConnectors() {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
    			.execute();
		
		SalesforceConnectorList resources = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			resources = httpResponse.getEntity(SalesforceConnectorList.class);
    	} else {
    		throw new ServiceUnavailableException(httpResponse.getAsString());
    	}
    	
    	return resources;
	}
	
	public SalesforceConnector get(String id) {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.header("Content-Type", MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
    			.path(id)
    			.execute();
		
		SalesforceConnector resource = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			resource = httpResponse.getEntity(SalesforceConnector.class);
    	} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
    	}
    	
    	return resource;
	}
	
	public UpdateResult<SalesforceConnector> update(String id, SalesforceConnectorRequest salesforceConnectorRequest) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
    			.path(id)
    			.parameter("tag", salesforceConnectorRequest.getTag())
    			.parameter("name", salesforceConnectorRequest.getName())
    			.parameter("ownerId", salesforceConnectorRequest.getOwnerId())
    			.execute();
		
		UpdateResult<SalesforceConnector> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			SalesforceConnector resource = httpResponse.getEntity(SalesforceConnector.class);
			result = new UpdateResultImpl<SalesforceConnector>(resource);  
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
			Error error = httpResponse.getEntity(Error.class);
			result = new UpdateResultImpl<SalesforceConnector>(error);
		}
		
		return result;
	}
	
	public UpdateResult<SalesforceConnector> test(String id) {
		return invokeAction(id, "test");
	}
	
	public UpdateResult<SalesforceConnector> build(String id) {
		return invokeAction(id, "build");
	}
	
	public UpdateResult<SalesforceConnector> metadataBackup(String id) {
		return invokeAction(id, "metadata-backup");
	}
	
	public DeleteResult delete(String id) {
		HttpResponse httpResponse = RestResource.delete(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
    			.path(id)
    			.execute();
		
		DeleteResult deleteResult = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			deleteResult = new DeleteResultImpl();
		} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
			Error error = httpResponse.getEntity(Error.class);
			deleteResult = new DeleteResultImpl(error);
		}
		
		return deleteResult;
	}
	
	private UpdateResult<SalesforceConnector> invokeAction(String id, String action) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
    			.path(id)
    			.path("actions")
    			.path(action)
    			.path("invoke")
    			.execute();
		
		UpdateResult<SalesforceConnector> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			SalesforceConnector resource = httpResponse.getEntity(SalesforceConnector.class);
			result = new UpdateResultImpl<SalesforceConnector>(resource);  
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
			Error error = httpResponse.getEntity(Error.class);
			result = new UpdateResultImpl<SalesforceConnector>(error);
		}
		
		return result;
	}
}