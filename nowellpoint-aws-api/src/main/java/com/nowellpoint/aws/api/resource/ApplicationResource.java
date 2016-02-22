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

import com.nowellpoint.aws.api.dto.ApplicationDTO;
import com.nowellpoint.aws.api.service.ApplicationService;
import com.nowellpoint.aws.api.util.HttpServletRequestUtil;

@Path("/application")
public class ApplicationResource {
	
	@Inject
	private ApplicationService applicationService;

	@Context
	private UriInfo uriInfo;
	
	@Context
	private HttpServletRequest servletRequest;
	
	/**
	 * 
	 * @return
	 */
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		Set<ApplicationDTO> resources = applicationService.getAll(subject);
		
		return Response.ok(resources).build();
    }
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	@GET
	@Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getApplication(@PathParam("id") String id) {
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		ApplicationDTO resource = applicationService.getApplication( id, subject );
		
		return Response.ok(resource).build();
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	@DELETE
	@Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response deleteApplication(@PathParam("id") String id) {
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		applicationService.deleteApplication(id, subject, uriInfo.getBaseUri());
		
		return Response.noContent().build();
	}
	
	/**
	 * 
	 * @param resource
	 * @return
	 */
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response createApplication(ApplicationDTO resource) {
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		applicationService.createApplication(subject, resource, uriInfo.getBaseUri());
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(ApplicationResource.class)
				.path("/{id}")
				.build(resource.getId());
		
		return Response.created(uri)
				.entity(resource)
				.build();
	}
	
	/**
	 * 
	 * @param resource
	 * @return
	 */
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateApplication(ApplicationDTO resource) {
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		applicationService.update(subject, resource, uriInfo.getBaseUri());
		
		return Response.ok(resource).build();
	}
}