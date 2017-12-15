package com.nowellpoint.api.rest.impl;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.nowellpoint.api.rest.ConnectorResource;
import com.nowellpoint.api.rest.domain.Connector;
import com.nowellpoint.api.rest.domain.ConnectorList;
import com.nowellpoint.api.rest.domain.ConnectorRequest;
import com.nowellpoint.api.service.ConnectorService;

public class ConnectorResourceImpl implements ConnectorResource {
	
	@Inject
	private ConnectorService connectorService;
	
	@Override
	public Response getConnectors() {
		ConnectorList connectorList = connectorService.getConnectors();
		return Response.ok(connectorList)
				.build();
	}

	@Override
	public Response getConnector(String id) {
		Connector connector = connectorService.findById(id);
		return Response.ok(connector)
				.build();
	}

	@Override
	public Response createConnector(String type, String name, String clientId, String clientSecret, String username, String password) {
		
		ConnectorRequest payload = ConnectorRequest.builder()
				.name(name)
				.type(type)
				.clientId(clientId)
				.clientSecret(clientSecret)
				.username(username)
				.password(password)
				.build();
		
		Connector connector = connectorService.createConnector(payload);
		
		URI uri = UriBuilder.fromUri(connector.getMeta().getHref())
				.path(ConnectorResource.class)
				.path("/{id}")
				.build(connector.getId());
		
		return Response.created(uri)
				.entity(connector)
				.build();
	}

	@Override
	public Response updateConnector(String id, String name, String clientId, String clientSecret, String username, String password) {
		
		System.out.println(name.isEmpty());
		System.out.println(clientId == null);
		System.out.println(clientSecret == null);
		
		ConnectorRequest payload = ConnectorRequest.builder()
				.name(name)
				.clientId(clientId)
				.clientSecret(clientSecret)
				.username(username)
				.password(password)
				.build();
		
		Connector connector = connectorService.updateConnector(id, payload);
		
		return Response.ok(connector)
				.build();
	}
	
	@Override
	public Response deleteConnector(String id) {
		connectorService.deleteConnector(id);
		return Response.ok()
				.build();
	}
	
	@Override
	public Response invokeAction(String id, String action) {
		
		Connector connector = connectorService.findById(id);
		
		if ("refresh".equalsIgnoreCase(action)) {
			//salesforceConnectorService.build(salesforceConnectorOrig);
		} else if ("disconnect".equalsIgnoreCase(action)) {
			//salesforceConnectorService.test(salesforceConnectorOrig);
		} else {
			throw new BadRequestException(String.format("Invalid action: %s", action));
		}
		
		return Response.ok(connector)
				.build();
		
	}
}