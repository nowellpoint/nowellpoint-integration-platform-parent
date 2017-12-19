package com.nowellpoint.client.resource;

import com.nowellpoint.client.model.Connector;
import com.nowellpoint.client.model.ConnectorList;
import com.nowellpoint.client.model.ConnectorRequest;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.exception.NotFoundException;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

public class ConnectorService extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "connectors";

	public ConnectorService(Token token) {
		super(token);
	}
	
	public Connector get(String id) {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(id)
				.execute();

		Connector resource = null;

		if (httpResponse.getStatusCode() == Status.OK) {
			resource = httpResponse.getEntity(Connector.class);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
		}

		return resource;
	}
	
	public ConnectorList getConnectors() {
		HttpResponse httpResponse = RestResource.get(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.execute();

		ConnectorList resources = null;

		if (httpResponse.getStatusCode() == Status.OK) {
			resources = httpResponse.getEntity(ConnectorList.class);
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
		}

		return resources;
	}
	
	public CreateResult<Connector> create(ConnectorRequest request) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path(RESOURCE_CONTEXT)
				.parameter("name", request.getName())
				.parameter("type", request.getType())
				.parameter("clientId", request.getClientId())
				.parameter("clientSecret", request.getClientSecret())
				.parameter("username", request.getUsername())
				.parameter("password", request.getPassword())
				.execute();
		
		CreateResult<Connector> result = new CreateResultImpl<Connector>(Connector.class, httpResponse);
		
		return result;
	}
	
	public UpdateResult<Connector> update(String connectorId, ConnectorRequest request) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path(RESOURCE_CONTEXT)
				.path(connectorId)
				.parameter("name", request.getName())
				.parameter("clientId", request.getClientId())
				.parameter("clientSecret", request.getClientSecret())
				.parameter("username", request.getUsername())
				.parameter("password", request.getPassword())
				.execute();
		
		UpdateResult<Connector> result = new UpdateResultImpl<Connector>(Connector.class, httpResponse);
		
		return result;
	}
	
	public UpdateResult<Connector> refresh(String connectorId) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path(RESOURCE_CONTEXT)
				.path(connectorId)
				.path("actions")
				.path("refresh")
				.path("invoke")
				.execute();
		
		UpdateResult<Connector> result = new UpdateResultImpl<Connector>(Connector.class, httpResponse);
		
		return result;
	}
	
	public UpdateResult<Connector> disconnect(String connectorId) {
		HttpResponse httpResponse = RestResource.post(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path(RESOURCE_CONTEXT)
				.path(connectorId)
				.path("actions")
				.path("disconnect")
				.path("invoke")
				.execute();
		
		UpdateResult<Connector> result = new UpdateResultImpl<Connector>(Connector.class, httpResponse);
		
		return result;
	}
	
	public DeleteResult delete(String connectorId) {
		HttpResponse httpResponse = RestResource.delete(token.getEnvironmentUrl())
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
				.path(connectorId)
				.execute();
		
		DeleteResult result = new DeleteResultImpl(httpResponse);
		
		return result;
	}
	
	//{id}/actions/{action}/invoke
}