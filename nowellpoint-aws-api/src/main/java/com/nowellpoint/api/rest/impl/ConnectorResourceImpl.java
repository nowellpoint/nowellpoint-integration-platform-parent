package com.nowellpoint.api.rest.impl;

import static com.nowellpoint.util.Assert.assertNotNull;

import java.net.URI;
import java.util.Locale;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.nowellpoint.api.rest.ConnectorResource;
import com.nowellpoint.api.rest.domain.Connector;
import com.nowellpoint.api.rest.domain.ConnectorList;
import com.nowellpoint.api.rest.domain.ConnectorRequest;
import com.nowellpoint.api.service.ConnectorService;
import com.nowellpoint.api.util.MessageConstants;
import com.nowellpoint.api.util.MessageProvider;

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
	public Response createConnector(ConnectorRequest request) {
		
		Connector connector = connectorService.createConnector(request);
		
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
		
		assertNotNull(name, MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_MISSING_NAME));
		
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
		
		Connector connector = null;
		
		if ("refresh".equalsIgnoreCase(action)) {
			connector = connectorService.refresh(id);
		} else if ("disconnect".equalsIgnoreCase(action)) {
			connector = connectorService.disconnect(id);
		} else {
			throw new BadRequestException(String.format("Invalid action: %s", action));
		}
		
		return Response.ok(connector)
				.build();
		
	}
	
	@Override
	public Response updateConnectorStatus(String id, ConnectorRequest request) {
		
		Connector connector = null;
		
		if ("connect".equalsIgnoreCase(request.getStatus())) {
			connector = connectorService.connect(id, request);
		} else if ("refresh".equalsIgnoreCase(request.getStatus())) {
			connector = connectorService.refresh(id);
		} else if ("disconnect".equalsIgnoreCase(request.getStatus())) {
			connector = connectorService.disconnect(id);
		} else {
			throw new BadRequestException(String.format("Invalid status: %s", request.getStatus()));
		}
		
		return Response.ok(connector)
				.build();
	}
}