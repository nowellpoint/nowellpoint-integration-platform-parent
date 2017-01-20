package com.nowellpoint.api.resource;

import java.net.URI;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.hibernate.validator.constraints.NotEmpty;

import com.nowellpoint.api.model.domain.Instance;
import com.nowellpoint.api.model.domain.Meta;
import com.nowellpoint.api.model.domain.SObjectDetail;
import com.nowellpoint.api.model.domain.SalesforceConnector;
import com.nowellpoint.api.model.domain.SalesforceConnectorList;
import com.nowellpoint.api.service.SObjectDetailService;
import com.nowellpoint.api.service.SalesforceConnectorService;
import com.nowellpoint.client.sforce.model.Token;

@Path("salesforce-connectors")
public class SalesforceConnectorResource {
	
	@Inject
	private SalesforceConnectorService salesforceConnectorService;
	
	@Inject
	private SObjectDetailService sobjectDetailService;
	
	@Context
	private SecurityContext securityContext;
	
	@Context
	private UriInfo uriInfo;
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllByOwner() {
		SalesforceConnectorList resources = salesforceConnectorService.findAllByOwner(securityContext.getUserPrincipal().getName());
		return Response.ok(resources).build();
    }
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createSalesforceConnector(@FormParam("id") @NotEmpty(message = "Missing Token Id") String id,
			@FormParam("instanceUrl") @NotEmpty(message = "Missing Instance Url") String instanceUrl,
			@FormParam("accessToken") @NotEmpty(message = "Missing Access Token") String accessToken,
			@FormParam("refreshToken") @NotEmpty(message = "Missing RefreshToken") String refreshToken) {
		
		Token token = new Token();
		token.setId(id);
		token.setInstanceUrl(instanceUrl);
		token.setAccessToken(accessToken);
		token.setRefreshToken(refreshToken);
		
		SalesforceConnector salesforceConnector = salesforceConnectorService.createSalesforceConnector(token);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(SalesforceResource.class)
				.path("/{id}")
				.build(salesforceConnector.getId());
		
		return Response.created(uri)
				.entity(salesforceConnector)
				.build(); 
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSalesforceConnector(@PathParam(value="id") String id) {		
		
		SalesforceConnector salesforceConnector = salesforceConnectorService.findById( id );
		
		if (salesforceConnector == null){
			throw new NotFoundException( String.format( "%s Id: %s does not exist or you do not have access to view", SalesforceConnector.class.getSimpleName(), id ) );
		}
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(SalesforceResource.class)
				.path("/{id}")
				.build(salesforceConnector.getId());
		
		Meta meta = new Meta();
		meta.setHref(uri.toString());
		
		salesforceConnector.setMeta(meta);
		
		return Response.ok(salesforceConnector).build();
	}
	
	@POST
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSalesforceConnector(@PathParam(value="id") String id, 
			@FormParam(value="name") String name, 
			@FormParam(value="tag") String tag) {	
		
		SalesforceConnector salesforceConnector = salesforceConnectorService.findById( id );
		salesforceConnector.setName(name);
		salesforceConnector.setTag(tag);
		
		salesforceConnectorService.updateSalesforceConnector( id, salesforceConnector);
		
		return Response.ok()
				.entity(salesforceConnector)
				.build(); 
	}
	
	@DELETE
	@Path("{id}")
	public Response deleteSalesforceConnector(@PathParam(value="id") String id) {		

		salesforceConnectorService.deleteSalesforceConnector(id);

		return Response.ok()
				.build(); 
	}
	
	@GET
	@Path("{id}/instances")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInstances(@PathParam(value="id") String id) {	
		
		Set<Instance> instances = salesforceConnectorService.getInstances(id);
		
		return Response.ok()
				.entity(instances)
				.build(); 
	}
	
	@GET
	@Path("{id}/instance/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInstance(@PathParam(value="id") String id, @PathParam(value="key") String key) {		
		
		Instance instance = salesforceConnectorService.getInstance(id, key);
		
		if (instance == null) {
			throw new NotFoundException(String.format("Environment for key %s was not found",key));
		}
		
		return Response.ok()
				.entity(instance)
				.build(); 
	}

	@POST
	@Path("{id}/instance")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addInstance(@PathParam(value="id") String id, Instance resource) {
		
		salesforceConnectorService.addInstance(id, resource);
		
		return Response.ok()
				.entity(resource)
				.build(); 
	}
	
	@PUT
	@Path("{id}/instance/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateInstance(@PathParam(value="id") String id, @PathParam(value="key") String key, Instance instance) {
		
		salesforceConnectorService.updateInstance(id, key, instance);
		
		return Response.ok()
				.entity(instance)
				.build(); 
	}
	
	@DELETE
	@Path("{id}/instance/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeInstance(@PathParam(value="id") String id, @PathParam(value="key") String key) {
		
		salesforceConnectorService.removeInstance(id, key);
		
		return Response.ok()
				.build(); 
	}
	
	@POST
	@Path("{id}/instance/{key}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateInstance(@PathParam(value="id") String id, @PathParam(value="key") String key, MultivaluedMap<String, String> parameters) {
		
		Instance instance = salesforceConnectorService.updateInstance(id, key, parameters);
		
		return Response.ok()
				.entity(instance)
				.build(); 
	}
	
	@GET
	@Path("{id}/instance/{key}/sobject/{sobjectName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSObjectDetails(@PathParam(value="id") String id, @PathParam(value="key") String key, @PathParam(value="sobjectName") String sobjectName) {		
		
		SObjectDetail resource = sobjectDetailService.findByName(key, sobjectName);
		
		if (resource == null) {
			throw new NotFoundException(String.format("SObject for name %s was not found", sobjectName));
		}
		
		return Response.ok()
				.entity(resource)
				.build(); 
	}
	
	@POST
	@Path("{id}/instance/{key}/actions/{action}/invoke")
	@Produces(MediaType.APPLICATION_JSON)
	public Response invokeAction(@PathParam(value="id") String id, @PathParam(value="key") String key, @PathParam(value="action") String action) {
		
		Instance instance = null;
		
		if ("build".equalsIgnoreCase(action)) {
			instance = salesforceConnectorService.buildEnvironment(id, key);
		} else if ("test".equalsIgnoreCase(action)) {
			instance = salesforceConnectorService.testConnection(id, key);
		} else {
			throw new BadRequestException(String.format("Invalid action: %s", action));
		}
		
		return Response.ok()
				.entity(instance)
				.build(); 
	}
}