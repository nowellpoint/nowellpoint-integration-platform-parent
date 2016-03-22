package com.nowellpoint.aws.api.resource;

import java.net.URI;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.aws.api.dto.IdentityDTO;
import com.nowellpoint.aws.api.dto.ServiceInstanceDTO;
import com.nowellpoint.aws.api.service.IdentityService;
import com.nowellpoint.aws.api.service.ServiceInstanceService;

@Path("/services")
public class ServiceInstanceResource {
	
	@Context
	private UriInfo uriInfo;
	
	@Context 
	private SecurityContext securityContext;
	
	@Inject
	private ServiceInstanceService serviceInstanceService;
	
	@Inject
	private IdentityService identityService;
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
		String subject = securityContext.getUserPrincipal().getName();
		
		Set<ServiceInstanceDTO> resources = serviceInstanceService.getAll(subject);
		
		return Response.ok(resources)
				.build();
    }
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response createServiceInstance(ServiceInstanceDTO resource) {
		String subject = securityContext.getUserPrincipal().getName();
		
		IdentityDTO owner = identityService.findIdentityBySubject(resource.getOwner().getHref());	
		
		resource.setOwner(owner);
		
		resource.setSubject(subject);
		resource.setEventSource(uriInfo.getBaseUri());
		
		serviceInstanceService.createServiceInstance(resource);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(ServiceInstanceResource.class)
				.path("/{id}")
				.build(resource.getId());
		
		return Response.created(uri)
				.entity(resource)
				.build();
	}

}
