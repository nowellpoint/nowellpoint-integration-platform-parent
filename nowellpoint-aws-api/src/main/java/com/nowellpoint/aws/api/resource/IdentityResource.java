package com.nowellpoint.aws.api.resource;

import java.net.URI;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.aws.api.dto.IdentityDTO;
import com.nowellpoint.aws.api.service.IdentityService;
import com.nowellpoint.aws.api.util.HttpServletRequestUtil;
import com.nowellpoint.aws.data.mongodb.Application;

@Path("/identity")
public class IdentityResource {
	
	@Inject
	private IdentityService identityService;

	@Context
	private UriInfo uriInfo;
	
	@Context
	private HttpServletRequest servletRequest;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createIdentity(IdentityDTO resource) {
		
		//
		//
		//
		
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		//
		//
		//
		
		identityService.create( subject, resource, uriInfo.getBaseUri() );
		
		//
		//
		//
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(IdentityResource.class)
				.path("/{id}")
				.build(resource.getId());
		
		//
		//
		//
		
		return Response.created(uri).build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateIdentity(IdentityDTO resource) {
		
		//
		//
		//
		
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		//
		//
		//
		
		identityService.updateIdentity( subject, resource, uriInfo.getBaseUri() );
		
		//
		//
		//
		
		return Response.ok(resource).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIdentity() {
		
		//
		//
		//
		
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		//
		//
		//
		
		IdentityDTO resource = identityService.findIdentityBySubject( subject );
		
		//
		//
		//
		
		return Response.ok(resource)
				.build();
		
	}
	
	@GET
	@Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIdentity(@PathParam("id") String id) {
		
		//
		//
		//
		
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		//
		//
		//
		
		IdentityDTO resource = identityService.findIdentity( id, subject );
		
		//
		//
		//
		
		return Response.ok(resource)
				.build();
	}
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIdentityBySubject(@QueryParam("subject") String subject) {
		
		//
		//
		//
		
		HttpServletRequestUtil.getSubject(servletRequest);
		
		//
		//
		//
		
		IdentityDTO resource = identityService.findIdentityBySubject( subject );
		
		//
		//
		//
		
		return Response.ok(resource)
				.build();
	}
	
	@POST
	@Path("/{id}/application")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addApplication(@PathParam("id") String id, Application application) {
		
		//
		//
		//
		
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		//
		//
		//
		
		IdentityDTO resource = identityService.findIdentity( id, subject );
		
		//
		//
		//
		
		resource.addApplication( application );
		
		//
		//
		//
		
		identityService.updateIdentity(subject, resource, uriInfo.getBaseUri() );
		
		//
		//
		//
		
		return Response.ok(resource)
				.build();
	}
}