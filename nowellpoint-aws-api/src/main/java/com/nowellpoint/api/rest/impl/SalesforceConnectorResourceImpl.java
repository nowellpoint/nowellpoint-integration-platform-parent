package com.nowellpoint.api.rest.impl;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.api.rest.SalesforceConnectorResource;
import com.nowellpoint.api.rest.domain.Meta;
import com.nowellpoint.api.rest.domain.SalesforceConnector;
import com.nowellpoint.api.rest.domain.SalesforceConnectorList;
import com.nowellpoint.api.service.SalesforceConnectorService;
import com.nowellpoint.client.sforce.model.Token;

public class SalesforceConnectorResourceImpl implements SalesforceConnectorResource {
	
	@Inject
	private SalesforceConnectorService salesforceConnectorService;
	
	@Context
	private SecurityContext securityContext;
	
	@Context
	private UriInfo uriInfo;
	
    public Response findAllByOwner() {
		SalesforceConnectorList salesforceConnectorList = salesforceConnectorService.findAllByOwner(securityContext.getUserPrincipal().getName());
		return Response.ok(salesforceConnectorList).build();
    }
	
	public Response createSalesforceConnector(String id, String instanceUrl, String accessToken, String refreshToken) {
		
		Token token = new Token();
		token.setId(id);
		token.setInstanceUrl(instanceUrl);
		token.setAccessToken(accessToken);
		token.setRefreshToken(refreshToken);
		
		SalesforceConnector salesforceConnector = salesforceConnectorService.createSalesforceConnector(token);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(SalesforceConnectorResource.class)
				.path("/{id}")
				.build(salesforceConnector.getId());
		
		Meta meta = new Meta();
		meta.setHref(uri.toString());
		
		salesforceConnector.setMeta(meta);
		
		return Response.created(uri)
				.entity(salesforceConnector)
				.build(); 
	}
	
	public Response getSalesforceConnector(String id) {		
		
		SalesforceConnector salesforceConnector = salesforceConnectorService.findById( id );
		
		if (salesforceConnector == null){
			throw new NotFoundException( String.format( "%s Id: %s does not exist or you do not have access to view", SalesforceConnector.class.getSimpleName(), id ) );
		}
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(SalesforceConnectorResource.class)
				.path("/{id}")
				.build(salesforceConnector.getId());
		
		Meta meta = new Meta();
		meta.setHref(uri.toString());
		
		salesforceConnector.setMeta(meta);
		
		return Response.ok(salesforceConnector).build();
	}
	
	public Response updateSalesforceConnector(String id, String name, String tag, String ownerId) {	
		
		SalesforceConnector salesforceConnector = salesforceConnectorService.updateSalesforceConnector(id, name, tag, ownerId);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(SalesforceConnectorResource.class)
				.path("/{id}")
				.build(salesforceConnector.getId());
		
		Meta meta = new Meta();
		meta.setHref(uri.toString());
		
		salesforceConnector.setMeta(meta);
		
		return Response.ok()
				.entity(salesforceConnector)
				.build(); 
	}
	
	public Response deleteSalesforceConnector(String id) {	
		
		SalesforceConnector salesforceConnector = salesforceConnectorService.findById( id );

		salesforceConnectorService.deleteSalesforceConnector(salesforceConnector);

		return Response.ok()
				.build(); 
	}
	
	public Response invokeAction(String id, String action) {
		
		SalesforceConnector salesforceConnector = salesforceConnectorService.findById(id);
		
		if ("build".equalsIgnoreCase(action)) {
			salesforceConnectorService.build(salesforceConnector);
		} else if ("test".equalsIgnoreCase(action)) {
			salesforceConnectorService.test(salesforceConnector);
		} else if ("metadata-backup".equalsIgnoreCase(action)) {
			salesforceConnectorService.metadataBackup(salesforceConnector);
		} else {
			throw new BadRequestException(String.format("Invalid action: %s", action));
		}
		
		return Response.ok()
				.entity(salesforceConnector)
				.build(); 
	}
}