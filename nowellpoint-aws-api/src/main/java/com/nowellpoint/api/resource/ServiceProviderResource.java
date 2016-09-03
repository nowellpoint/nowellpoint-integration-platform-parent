package com.nowellpoint.api.resource;

import java.net.URI;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.api.dto.ServiceProviderDTO;
import com.nowellpoint.api.service.ServiceProviderService;

@Path("/providers")
public class ServiceProviderResource {
	
	@Context
	private UriInfo uriInfo;
	
	@Inject
	private ServiceProviderService serviceProviderService;
	
	@Context 
	private SecurityContext securityContext;
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllActive(
    		@QueryParam("localeSidKey") String localeSidKey, 
    		@QueryParam("languageLocaleKey") String languageLocaleKey) {
		
		Set<ServiceProviderDTO> resources = serviceProviderService.getAllActive(localeSidKey, languageLocaleKey);
		
		return Response.ok(resources).build();
    }
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getServiceProvider(@PathParam("id") String id) {		
		ServiceProviderDTO resource = serviceProviderService.getServiceProvider(id);		
		return Response.ok(resource)
				.build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response createServiceProvider(ServiceProviderDTO resource) {
		
		serviceProviderService.createServiceProvider(resource);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(ServiceProviderResource.class)
				.path("/{id}")
				.build(resource.getId());
		
		return Response.created(uri)
				.entity(resource)
				.build();
	}
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteServiceProvider(@PathParam("id") String id) {
		serviceProviderService.deleteServiceProvider(id);		
		return Response.noContent().build();
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateServiceProvider(@PathParam("id") String id, ServiceProviderDTO resource) {

		resource.setId(id);
		
		serviceProviderService.updateServiceProvider(resource);
		
		return Response.ok(resource).build();
	}
}