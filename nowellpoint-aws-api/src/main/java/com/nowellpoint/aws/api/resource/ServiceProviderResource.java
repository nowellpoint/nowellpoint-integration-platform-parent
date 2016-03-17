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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.aws.api.dto.IdentityDTO;
import com.nowellpoint.aws.api.dto.ServiceProviderDTO;
import com.nowellpoint.aws.api.dto.ServiceProviderInstanceDTO;
import com.nowellpoint.aws.api.service.IdentityService;
import com.nowellpoint.aws.api.service.SalesforceService;
import com.nowellpoint.aws.api.service.ServiceProviderService;

@Path("/providers")
public class ServiceProviderResource {
	
	@Context
	private UriInfo uriInfo;
	
	@Context
	private HttpServletRequest servletRequest;
	
	@Inject
	private ServiceProviderService serviceProviderService;
	
	@Inject
	private IdentityService identityService;
	
	@Inject
	private SalesforceService salesforceService;
	
	@Context 
	private SecurityContext securityContext;
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
		String subject = securityContext.getUserPrincipal().getName();
		
		Set<ServiceProviderInstanceDTO> resources = serviceProviderService.getAll(subject);
		
		return Response.ok(resources).build();
    }

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response createServiceProvider(ServiceProviderInstanceDTO resource) {
		String subject = securityContext.getUserPrincipal().getName();
		
		IdentityDTO owner = identityService.findIdentityBySubject(resource.getOwner().getHref());	
		
		resource.setOwner(owner);
		
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
		String subject = securityContext.getUserPrincipal().getName();
		
		serviceProviderService.deleteServiceProvider(id, subject, uriInfo.getBaseUri());
		
		return Response.noContent().build();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getServiceProvider(@PathParam("id") String id) {
		String subject = securityContext.getUserPrincipal().getName();
		
		ServiceProviderInstanceDTO resource = serviceProviderService.getServiceProvider(id, subject);
		
		return Response.ok(resource)
				.build();
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateServiceProvider(@PathParam("id") String id, ServiceProviderInstanceDTO resource) {
		String subject = securityContext.getUserPrincipal().getName();
		
		resource.setId(id);
		
		serviceProviderService.updateServiceProvider(subject, resource, uriInfo.getBaseUri());
		
		return Response.ok(resource).build();
	}
	
	@GET
	@Path("/salesforce")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSalesforceProvider(@QueryParam(value="code") String code) {
		String subject = securityContext.getUserPrincipal().getName();
		
		ServiceProviderDTO provider = salesforceService.getAsServiceProvider(subject, code);
		
		IdentityDTO owner = new IdentityDTO();
		owner.setHref(subject);
		
		ServiceProviderInstanceDTO resource = new ServiceProviderInstanceDTO();
		resource.setAccount(provider.getAccount());
		resource.setInstanceId(provider.getInstanceId());
		resource.setInstanceName(provider.getInstanceName());
		resource.setInstanceUrl(provider.getInstanceUrl());
		resource.setIsActive(Boolean.FALSE);
		resource.setName(provider.getName());
		resource.setType(provider.getType());
		resource.setPrice(0.00);
		resource.setOwner(owner);
		
		serviceProviderService.createServiceProvider(subject, resource, uriInfo.getBaseUri());
		
		return Response.ok(resource).build();
	}
}