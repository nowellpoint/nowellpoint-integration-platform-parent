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
import com.nowellpoint.api.model.domain.SObjectDetail;
import com.nowellpoint.api.model.domain.SalesforceConnector;
import com.nowellpoint.api.service.SObjectDetailService;
import com.nowellpoint.api.service.SalesforceConnectorService;
import com.nowellpoint.client.sforce.model.Token;

@Path("connectors")
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
	@Path("salesforce")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
		
		Set<SalesforceConnector> salesforceConnectors = salesforceConnectorService.findAllByOwner();
		
		return Response.ok(salesforceConnectors)
				.build();
    }
	
	@POST
	@Path("salesforce")
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
	@Path("salesforce/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSalesforceConnector(@PathParam(value="id") String id) {		
		
		SalesforceConnector salesforceConnector = salesforceConnectorService.findSalesforceConnector( id );
		
		if (salesforceConnector == null){
			throw new NotFoundException( String.format( "%s Id: %s does not exist or you do not have access to view", SalesforceConnector.class.getSimpleName(), id ) );
		}
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(SalesforceResource.class)
				.path("/{id}")
				.build(salesforceConnector.getId());
		
		salesforceConnector.setHref(uri.toString());
		
		return Response.ok(salesforceConnector).build();
	}
	
	@POST
	@Path("salesforce/{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSalesforceConnector(@PathParam(value="id") String id, 
			@FormParam(value="name") String name, 
			@FormParam(value="tag") String tag) {	
		
		SalesforceConnector salesforceConnector = salesforceConnectorService.findSalesforceConnector( id );
		salesforceConnector.setName(name);
		salesforceConnector.setTag(tag);
		
		salesforceConnectorService.updateSalesforceConnector( id, salesforceConnector);
		
		return Response.ok()
				.entity(salesforceConnector)
				.build(); 
	}
	
	@DELETE
	@Path("salesforce/{id}")
	public Response deleteSalesforceConnector(@PathParam(value="id") String id) {		

		salesforceConnectorService.deleteSalesforceConnector(id);

		return Response.ok()
				.build(); 
	}
	
	@GET
	@Path("salesforce/{id}/environments")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEnvironments(@PathParam(value="id") String id) {	
		
		Set<Instance> instances = salesforceConnectorService.getInstances(id);
		
		return Response.ok()
				.entity(instances)
				.build(); 
	}
	
	@GET
	@Path("salesforce/{id}/environment/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEnvironment(@PathParam(value="id") String id, @PathParam(value="key") String key) {		
		
		Instance instance = salesforceConnectorService.getEnvironment(id, key);
		
		if (instance == null) {
			throw new NotFoundException(String.format("Environment for key %s was not found",key));
		}
		
		return Response.ok()
				.entity(instance)
				.build(); 
	}

	@POST
	@Path("salesforce/{id}/environment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addEnvironment(@PathParam(value="id") String id, Instance resource) {
		
		salesforceConnectorService.addEnvironment(id, resource);
		
		return Response.ok()
				.entity(resource)
				.build(); 
	}
	
	@PUT
	@Path("salesforce/{id}/environment/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateEnvironment(@PathParam(value="id") String id, @PathParam(value="key") String key, Instance instance) {
		
		salesforceConnectorService.updateEnvironment(id, key, instance);
		
		return Response.ok()
				.entity(instance)
				.build(); 
	}
	
	@DELETE
	@Path("salesforce/{id}/environment/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeEnvironment(@PathParam(value="id") String id, @PathParam(value="key") String key) {
		
		salesforceConnectorService.removeEnvironment(id, key);
		
		return Response.ok()
				.build(); 
	}
	
	@POST
	@Path("salesforce/{id}/environment/{key}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateEnvironment(@PathParam(value="id") String id, @PathParam(value="key") String key, MultivaluedMap<String, String> parameters) {
		
		Instance instance = salesforceConnectorService.updateEnvironment(id, key, parameters);
		
		return Response.ok()
				.entity(instance)
				.build(); 
	}
	
	@GET
	@Path("salesforce/{id}/environment/{key}/sobject/{sobjectName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSObjectDetails(@PathParam(value="id") String id, @PathParam(value="key") String key, @PathParam(value="sobjectName") String sobjectName) {		
		
		SObjectDetail resource = sobjectDetailService.findSObjectDetailByName(key, sobjectName);
		
		if (resource == null) {
			throw new NotFoundException(String.format("SObject for name %s was not found", sobjectName));
		}
		
		return Response.ok()
				.entity(resource)
				.build(); 
	}
	
	@POST
	@Path("salesforce/{id}/environment/{key}/actions/{action}/invoke")
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