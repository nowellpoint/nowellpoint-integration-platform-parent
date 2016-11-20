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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.api.model.domain.AccountProfile;
import com.nowellpoint.api.model.domain.Project;
import com.nowellpoint.api.service.AccountProfileService;
import com.nowellpoint.api.service.ProjectService;

@Path("/project")
public class ProjectResource {
	
	@Inject
	private ProjectService projectService;
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@Context
	private UriInfo uriInfo;
	
	@Context
	private SecurityContext securityContext;
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
		
		Set<Project> projects = projectService.findAllByOwner();
		
		return Response.ok(projects)
				.build();
    }
	
	@GET
	@Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getProject(@PathParam("id") String id) {

		Project project = projectService.findProject( id );
		
		return Response.ok(project)
				.build();
	}
	
	@DELETE
	@Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response deleteProject(@PathParam("id") String id) {
		
		projectService.deleteProject( id );
		
		return Response.noContent()
				.build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response createProject(Project project) {
		
		AccountProfile owner = accountProfileService.findAccountProfileByHref(project.getOwner().getHref());	
		
		project.setOwner(owner);
		
		projectService.createProject(project);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(ProjectResource.class)
				.path("/{id}")
				.build(project.getId());
		
		return Response.created(uri)
				.entity(project)
				.build();
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateProject(@PathParam("id") String id, Project project) {
		
		projectService.updateProject( id, project );
		
		return Response.ok(project)
				.build();
	}
}