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
import com.nowellpoint.api.rest.domain.SalesforceConnectorOrig;
import com.nowellpoint.api.rest.domain.SalesforceConnectorList;
import com.nowellpoint.api.rest.domain.UpdateSalesforceConnectorRequest;
import com.nowellpoint.api.rest.domain.AbstractUserInfo;
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
		
		SalesforceConnectorOrig salesforceConnectorOrig = salesforceConnectorService.createSalesforceConnector(token);
		
		Meta meta = Meta.builder()
				.id(salesforceConnectorOrig.getId())
				.resourceClass(SalesforceConnectorResource.class)
				.build();
		
		salesforceConnectorOrig.setMeta(meta);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(SalesforceConnectorResource.class)
				.path("/{id}")
				.build(salesforceConnectorOrig.getId());
		
		return Response.created(uri)
				.entity(salesforceConnectorOrig)
				.build(); 
	}
	
	public Response getSalesforceConnector(String id, String expand) {		
		
		SalesforceConnectorOrig salesforceConnectorOrig = salesforceConnectorService.findById( id );
		
		if (salesforceConnectorOrig == null){
			throw new NotFoundException( String.format( "%s Id: %s does not exist or you do not have access to view", SalesforceConnectorOrig.class.getSimpleName(), id ) );
		}
		
		if (Assert.isNotNullOrEmpty(expand)) {
			String[] elements = expand.split(",");
			
			if (Arrays.asList(elements).contains("jobs")) {
				JobList jobList = jobService.queryBySource(salesforceConnectorOrig.getId());
				salesforceConnectorOrig.addJobs(jobList.getItems());
			}
		}
		
		Meta meta = Meta.builder()
				.id(salesforceConnectorOrig.getId())
				.resourceClass(SalesforceConnectorResource.class)
				.build();
		
		salesforceConnectorOrig.setMeta(meta);
		
		return Response.ok(salesforceConnectorOrig).build();
	}
	
	public Response updateSalesforceConnector(String id, String name, String tag, String ownerId) {	
		
		AbstractUserInfo owner = Assert.isEmpty(ownerId) ? null : AbstractUserInfo.of(ownerId);
		AbstractUserInfo lastUpdatedBy = AbstractUserInfo.of(securityContext.getUserPrincipal().getName());
		
		UpdateSalesforceConnectorRequest request = UpdateSalesforceConnectorRequest.builder()
				.name(Assert.isEmpty(name) ? null : name)
				.tag(Assert.isNull(tag) ? Optional.empty() : Optional.of(tag))
				.owner(owner)
				.lastUpdatedBy(lastUpdatedBy)
				.build();
		
		SalesforceConnectorOrig salesforceConnectorOrig = salesforceConnectorService.updateSalesforceConnector(id, request);
		
		Meta meta = Meta.builder()
				.id(salesforceConnectorOrig.getId())
				.resourceClass(SalesforceConnectorResource.class)
				.build();
		
		salesforceConnectorOrig.setMeta(meta);
		
		return Response.ok()
				.entity(salesforceConnectorOrig)
				.build(); 
	}
	
	public Response describeSObject(String id, String sobject) {
		
		SalesforceConnectorOrig salesforceConnectorOrig = salesforceConnectorService.findById( id );
		
		DescribeSobjectResult result = salesforceConnectorService.describeSobject(salesforceConnectorOrig, sobject);
		
		return Response.ok(result)
				.build(); 
	}
	
	public Response deleteSalesforceConnector(String id) {	
		
		SalesforceConnectorOrig salesforceConnectorOrig = salesforceConnectorService.findById( id );

		salesforceConnectorService.deleteSalesforceConnector(salesforceConnectorOrig);

		return Response.ok()
				.build(); 
	}
	
	public Response invokeAction(String id, String action) {
		
		SalesforceConnectorOrig salesforceConnectorOrig = salesforceConnectorService.findById(id);
		
		if ("build".equalsIgnoreCase(action)) {
			salesforceConnectorService.build(salesforceConnectorOrig);
		} else if ("test".equalsIgnoreCase(action)) {
			salesforceConnectorService.test(salesforceConnectorOrig);
		} else if ("metadata-backup".equalsIgnoreCase(action)) {
			salesforceConnectorService.metadataBackup(salesforceConnectorOrig);
		} else {
			throw new BadRequestException(String.format("Invalid action: %s", action));
		}
		
		return Response.ok()
				.entity(salesforceConnectorOrig)
				.build(); 
	}
}