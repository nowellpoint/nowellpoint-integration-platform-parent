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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.aws.api.service.ProjectService;
import com.nowellpoint.aws.api.util.HttpServletRequestUtil;
import com.nowellpoint.aws.model.data.Project;

@Path("/project")
public class ProjectResource {
	
	@Inject
	private ProjectService projectService;
	
	@Context
	private UriInfo uriInfo;
	
	@Context
	private HttpServletRequest servletRequest;
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
		
		//
		//
		//
		
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		//
		//
		//
		
		Set<Project> resources = projectService.getAll(subject);
		
		//
		//
		//
		
		return Response.ok(resources)
				.build();
    }
	
	@GET
	@Path("/{projectId}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getProject(@PathParam("projectId") String projectId) {
		
		//
		//
		//
		
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		//
		//
		//
		
		Project resource = projectService.get(projectId, subject);
		
		//
		//
		//
		
		return Response.ok(resource)
				.build();
	}
	
	
	@DELETE
	@Path("/{projectId}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response deleteProject(@PathParam("projectId") String projectId) {
		
		//
		//
		//
		
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		//
		//
		//
		
		projectService.delete(projectId, subject, uriInfo.getBaseUri());
		
		//
		//
		//
		
		return Response.noContent()
				.build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response createProject(Project resource) {
		
		//
		//
		//
		
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		//
		//
		//
		
		projectService.create(subject, resource, uriInfo.getBaseUri());
		
		//
		//
		//
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(ProjectResource.class)
				.path("/{id}")
				.build(resource.getId());
		
		//
		//
		//
		
		return Response.created(uri)
				.entity(resource)
				.build();
	}
	
	@PUT
	@Path("/{projectId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateProject(@PathParam("projectId") String projectId, Project resource) {
		
		//
		//
		//
		
		resource.setId(projectId);
		
		//
		//
		//
		
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		//
		//
		//
		
		projectService.update(subject, resource, uriInfo.getBaseUri());
		
		//
		//
		//
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(ProjectResource.class)
				.path("/{id}")
				.build(resource.getId());
		
		//
		//
		//
		
		return Response.ok(resource)
				.build();
	}
	
	@PUT
	@Path("/{projectId}/{subjectId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response shareProject(@PathParam("projectId") String projectId, @PathParam("subjectId") String subjectId) {
		return null;
	}
	
	@PUT
	@Path("/{projectId}/{subjectId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response restrictProject(@PathParam("projectId") String projectId, @PathParam("subjectId") String subjectId) {
		return null;
	}
}