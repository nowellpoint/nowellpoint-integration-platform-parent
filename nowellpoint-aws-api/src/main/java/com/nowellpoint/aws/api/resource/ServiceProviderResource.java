package com.nowellpoint.aws.api.resource;

import java.net.URI;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.aws.api.dto.ServiceProviderDTO;
import com.nowellpoint.aws.api.service.ServiceProviderService;
import com.nowellpoint.aws.api.util.HttpServletRequestUtil;

@Path("/provider")
public class ServiceProviderResource {
	
	@Context
	private UriInfo uriInfo;
	
	@Context
	private HttpServletRequest servletRequest;
	
	@Inject
	private ServiceProviderService serviceProviderService;
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		Set<ServiceProviderDTO> resources = serviceProviderService.getAll(subject);
		
		return Response.ok(resources).build();
    }

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response createServiceProvider(ServiceProviderDTO resource) {
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		serviceProviderService.createServiceProvider(subject, resource, uriInfo.getBaseUri());
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(ApplicationResource.class)
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
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		serviceProviderService.deleteServiceProvider(id, subject, uriInfo.getBaseUri());
		
		return Response.noContent().build();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getServiceProvider(@PathParam("id") String id) {
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		ServiceProviderDTO resource = serviceProviderService.getServiceProvider(id, subject);
		
		return Response.ok(resource)
				.build();
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateServiceProvider(@PathParam("id") String id, ServiceProviderDTO resource) {
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		resource.setId(id);
		
		serviceProviderService.updateServiceProvider(subject, resource, uriInfo.getBaseUri());
		
		return Response.ok(resource).build();
	}
}