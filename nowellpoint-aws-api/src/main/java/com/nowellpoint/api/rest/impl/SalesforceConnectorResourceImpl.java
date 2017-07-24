package com.nowellpoint.api.rest.impl;

import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.api.rest.SalesforceConnectorResource;
import com.nowellpoint.api.rest.domain.JobList;
import com.nowellpoint.api.rest.domain.Meta;
import com.nowellpoint.api.rest.domain.SalesforceConnector;
import com.nowellpoint.api.rest.domain.SalesforceConnectorList;
import com.nowellpoint.api.rest.domain.UpdateSalesforceConnectorRequest;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.service.JobService;
import com.nowellpoint.api.service.SalesforceConnectorService;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;
import com.nowellpoint.util.Assert;

public class SalesforceConnectorResourceImpl implements SalesforceConnectorResource {
	
	@Inject
	private SalesforceConnectorService salesforceConnectorService;
	
	@Inject
	private JobService jobService;
	
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
		
		Meta meta = Meta.builder()
				.href(uri.toString())
				.build();
		
		salesforceConnector.setMeta(meta);
		
		return Response.created(uri)
				.entity(salesforceConnector)
				.build(); 
	}
	
	public Response getSalesforceConnector(String id, String expand) {		
		
		SalesforceConnector salesforceConnector = salesforceConnectorService.findById( id );
		
		if (salesforceConnector == null){
			throw new NotFoundException( String.format( "%s Id: %s does not exist or you do not have access to view", SalesforceConnector.class.getSimpleName(), id ) );
		}
		
		if (Assert.isNotNullOrEmpty(expand)) {
			String[] elements = expand.split(",");
			
			if (Arrays.asList(elements).contains("jobs")) {
				JobList jobList = jobService.queryBySource(salesforceConnector.getId());
				salesforceConnector.addJobs(jobList.getItems());
			}
		}
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(SalesforceConnectorResource.class)
				.path("/{id}")
				.build(salesforceConnector.getId());
		
		Meta meta = Meta.builder()
				.href(uri.toString())
				.build();
		
		salesforceConnector.setMeta(meta);
		
		return Response.ok(salesforceConnector).build();
	}
	
	public Response updateSalesforceConnector(String id, String name, String tag, String ownerId) {	
		
		UserInfo owner = Assert.isEmpty(ownerId) ? null : UserInfo.of(ownerId);
		UserInfo lastUpdatedBy = UserInfo.of(securityContext.getUserPrincipal().getName());
		
		UpdateSalesforceConnectorRequest request = UpdateSalesforceConnectorRequest.builder()
				.name(Assert.isEmpty(name) ? null : name)
				.tag(Assert.isNull(tag) ? Optional.empty() : Optional.of(tag))
				.owner(owner)
				.lastUpdatedBy(lastUpdatedBy)
				.build();
		
		SalesforceConnector salesforceConnector = salesforceConnectorService.updateSalesforceConnector(id, request);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(SalesforceConnectorResource.class)
				.path("/{id}")
				.build(salesforceConnector.getId());
		
		Meta meta = Meta.builder()
				.href(uri.toString())
				.build();
		
		salesforceConnector.setMeta(meta);
		
		return Response.ok()
				.entity(salesforceConnector)
				.build(); 
	}
	
	public Response describeSObject(String id, String sobject) {
		
		SalesforceConnector salesforceConnector = salesforceConnectorService.findById( id );
		
		DescribeSobjectResult result = salesforceConnectorService.describeSobject(salesforceConnector, sobject);
		
		return Response.ok(result)
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