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

import com.nowellpoint.aws.api.dto.ProjectDTO;
import com.nowellpoint.aws.api.service.ProjectService;
import com.nowellpoint.aws.api.util.HttpServletRequestUtil;

@Path("/project")
public class ProjectResource {
	
	@Inject
	private ProjectService projectService;
	
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
		
		Set<ProjectDTO> resources = projectService.getAll(subject);
		
		return Response.ok(resources)
				.build();
    }
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	@GET
	@Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getProject(@PathParam("id") String id) {
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		ProjectDTO resource = projectService.getProject( subject, id );
		
		return Response.ok(resource)
				.build();
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	@DELETE
	@Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response deleteProject(@PathParam("id") String id) {
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		projectService.deleteProject(id, subject, uriInfo.getBaseUri());
		
		return Response.noContent()
				.build();
	}
	
	/**
	 * 
	 * @param resource
	 * @return
	 */
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response createProject(ProjectDTO resource) {
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		projectService.createProject(subject, resource, uriInfo.getBaseUri());
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(ProjectResource.class)
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
	public Response updateProject(ProjectDTO resource) {
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		projectService.updateProject(subject, resource, uriInfo.getBaseUri());
		
		return Response.ok(resource)
				.build();
	}
	
	/**
	 * 
	 * @param id
	 * @param subjectId
	 * @return
	 */
	
	@PUT
	@Path("/{id}/{subjectId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response shareProject(@PathParam("id") String id, @PathParam("subjectId") String subjectId) {
		return null;
	}
	
	/**
	 * 
	 * @param id
	 * @param subjectId
	 * @return
	 */
	
	@PUT
	@Path("/{id}/{subjectId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response restrictProject(@PathParam("id") String id, @PathParam("subjectId") String subjectId) {
		return null;
	}
}