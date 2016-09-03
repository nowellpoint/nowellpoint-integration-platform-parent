package com.nowellpoint.api.resource;

import java.net.URI;
import java.util.Set;

import javax.inject.Inject;
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

import com.nowellpoint.api.dto.EnvironmentDTO;
import com.nowellpoint.api.dto.Id;
import com.nowellpoint.api.dto.SalesforceConnectorDTO;
import com.nowellpoint.api.dto.ServiceInstanceDTO;
import com.nowellpoint.api.model.SalesforceConnector;
import com.nowellpoint.api.service.SalesforceConnectorService;
import com.nowellpoint.client.sforce.model.Token;

@Path("connectors")
public class SalesforceConnectorResource {
	
	@Inject
	private SalesforceConnectorService salesforceConnectorService;
	
	@Context
	private SecurityContext securityContext;
	
	@Context
	private UriInfo uriInfo;
	
	@GET
	@Path("salesforce")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
		
		Set<SalesforceConnectorDTO> resources = salesforceConnectorService.findAllByOwner();
		
		return Response.ok(resources)
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
		
		SalesforceConnectorDTO resource = salesforceConnectorService.createSalesforceConnector(token);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(SalesforceResource.class)
				.path("/{id}")
				.build(resource.getId());
		
		return Response.created(uri)
				.entity(resource)
				.build(); 
	}
	
	@GET
	@Path("salesforce/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSalesforceConnector(@PathParam(value="id") String id) {		
		SalesforceConnectorDTO salesforceConnector = salesforceConnectorService.findSalesforceConnector( new Id(id) );
		
		if (salesforceConnector == null){
			throw new NotFoundException( String.format( "%s Id: %s does not exist or you do not have access to view", SalesforceConnector.class.getSimpleName(), id ) );
		}
		
		return Response.ok(salesforceConnector).build();
	}
	
	@POST
	@Path("salesforce/{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSalesforceConnector(@PathParam(value="id") String id, @FormParam(value="tag") String tag) {	
		
		SalesforceConnectorDTO resource = salesforceConnectorService.findSalesforceConnector( new Id(id) );
		resource.setTag(tag);
		
		salesforceConnectorService.updateSalesforceConnector(new Id(id), resource);
		
		return Response.ok()
				.entity(resource)
				.build(); 
	}
	
	@DELETE
	@Path("salesforce/{id}")
	public Response deleteSalesforceConnector(@PathParam(value="id") String id) {		

		salesforceConnectorService.deleteSalesforceConnector(new Id(id));

		return Response.noContent()
				.build(); 
	}
	
	@GET
	@Path("salesforce/{id}/environment/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEnvironment(@PathParam(value="id") String id, @PathParam(value="key") String key) {		
		
		EnvironmentDTO resource = salesforceConnectorService.getEnvironment(new Id(id), key);
		
		if (resource == null) {
			throw new NotFoundException(String.format("Environment for key %s was not found",key));
		}
		
		return Response.ok()
				.entity(resource)
				.build(); 
	}

	@POST
	@Path("salesforce/{id}/environment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addEnvironment(@PathParam(value="id") String id, EnvironmentDTO resource) {
		
		salesforceConnectorService.addEnvironment(new Id(id), resource);
		
		return Response.ok()
				.entity(resource)
				.build(); 
	}
	
	@PUT
	@Path("salesforce/{id}/environment/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateEnvironment(@PathParam(value="id") String id, @PathParam(value="key") String key, EnvironmentDTO resource) {
		
		salesforceConnectorService.updateEnvironment(new Id(id), key, resource);
		
		return Response.ok()
				.entity(resource)
				.build(); 
	}
	
	@DELETE
	@Path("salesforce/{id}/environment/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeEnvironment(@PathParam(value="id") String id, @PathParam(value="key") String key) {
		
		salesforceConnectorService.removeEnvironment(new Id(id), key);
		
		return Response.ok()
				.build(); 
	}
	
	@POST
	@Path("salesforce/{id}/environment/{key}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateEnvironment(@PathParam(value="id") String id, @PathParam(value="key") String key, MultivaluedMap<String, String> parameters) {
		
		EnvironmentDTO resource = salesforceConnectorService.updateEnvironment(new Id(id), key, parameters);
		
		return Response.ok()
				.entity(resource)
				.build(); 
	}
	
	@POST
	@Path("salesforce/{id}/service")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addServiceInstance(@PathParam(value="id") String id, @FormParam(value="key") String key) {
		
		ServiceInstanceDTO resource = salesforceConnectorService.addServiceInstance( new Id(id), key);
		
		if ( resource == null ) {
			throw new NotFoundException( String.format( "%s Key: %s does not exist or you do not have access to view", "Service", key ) );
		}
		
		return Response.ok()
				.entity(resource)
				.build(); 	
	}
	
	@GET
	@Path("salesforce/{id}/service/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getServiceInstance(
			@PathParam(value="id") String id,
			@PathParam(value="key") String key) {
		
		ServiceInstanceDTO resource = salesforceConnectorService.getServiceInstance( new Id(id), key );
		
		return Response.ok()
				.entity(resource)
				.build(); 	
	}
	
	@PUT
	@Path("salesforce/{id}/service/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateServiceInstance(
			@PathParam(value="id") String id,
			@PathParam(value="key") String key,
			ServiceInstanceDTO resource) {
		
		salesforceConnectorService.updateServiceInstance( new Id(id), key, resource);
		
		return Response.ok()
				.entity(resource)
				.build(); 	
	}
	
	/**
	 * 
	 * @param id
	 * @param key
	 * @param parameters
	 * @return
	 */
	
	@POST
	@Path("salesforce/{id}/service/{key}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateServiceInstance(@PathParam(value="id") String id, @PathParam(value="key") String key, MultivaluedMap<String, String> parameters) {
		
		ServiceInstanceDTO resource = salesforceConnectorService.updateServiceInstance( new Id(id), key, parameters);
		
		return Response.ok()
				.entity(resource)
				.build(); 	
	}
	
	/**
	 * 
	 * @param id
	 * @param key
	 * @return
	 */
	
	@DELETE
	@Path("salesforce/{id}/service/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeServiceInstance(@PathParam(value="id") String id, @PathParam(value="key") String key) {
		
		SalesforceConnectorDTO resource = salesforceConnectorService.removeServiceInstance( new Id(id), key);
		
		return Response.ok()
				.entity(resource)
				.build(); 
		
	}
}