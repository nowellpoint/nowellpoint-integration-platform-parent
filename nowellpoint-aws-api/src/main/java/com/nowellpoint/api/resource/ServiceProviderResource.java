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

import com.nowellpoint.api.model.dto.Id;
import com.nowellpoint.api.model.dto.ServiceProvider;
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
		
		Set<ServiceProvider> serviceProviders = serviceProviderService.getAllActive(localeSidKey, languageLocaleKey);
		
		return Response.ok(serviceProviders).build();
    }
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getServiceProvider(@PathParam("id") String id) {		
		ServiceProvider serviceProvider = serviceProviderService.getServiceProvider( new Id( id ) );		
		
		return Response.ok(serviceProvider)
				.build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response createServiceProvider(ServiceProvider serviceProvider) {
		
		serviceProviderService.createServiceProvider(serviceProvider);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(ServiceProviderResource.class)
				.path("/{id}")
				.build(serviceProvider.getId());
		
		return Response.created(uri)
				.entity(serviceProvider)
				.build();
	}
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteServiceProvider(@PathParam("id") String id) {
		serviceProviderService.deleteServiceProvider(new Id( id ));		
		
		return Response.noContent().build();
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateServiceProvider(@PathParam("id") String id, ServiceProvider serviceProvider) {

		serviceProvider.setId(id);
		
		serviceProviderService.updateServiceProvider(serviceProvider);
		
		return Response.ok(serviceProvider).build();
	}
}