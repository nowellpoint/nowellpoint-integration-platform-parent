package com.nowellpoint.api.resource;

import java.net.URI;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.hibernate.validator.constraints.NotEmpty;

import com.nowellpoint.api.model.domain.Application;
import com.nowellpoint.api.model.domain.Instance;
import com.nowellpoint.api.service.ApplicationService;

@Path("/applications")
public class ApplicationResource {
	
	@Inject
	private ApplicationService applicationService;

	@Context
	private UriInfo uriInfo;
	
	@Context
	private SecurityContext securityContext;
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllByOwner() {
		
		Set<Application> resources = applicationService.findAllByOwner();
		
		return Response.ok(resources).build();
    }
	
	@GET
	@Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getApplication(@PathParam("id") String id) {
		
		Application application = applicationService.findApplication( id );
		
		if (application == null) {
			throw new NotFoundException( String.format( "%s Id: %s does not exist or you do not have access to view", Application.class.getSimpleName(), id ) );
		}
		
		return Response.ok(application).build();
	}
	
	@DELETE
	@Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response deleteApplication(@PathParam("id") String id) {
		
		applicationService.deleteApplication( id );
		
		return Response.noContent().build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createApplication(
			@FormParam("description") @NotEmpty String description,
			@FormParam("connectorId") @NotEmpty String connectorId,
			@FormParam("name") @NotEmpty String name,
			@FormParam("importSandboxes") Boolean importSandboxes,
			@FormParam("importServices") Boolean importServices) {
		
		Application application = new Application();
		application.setName(name);
		application.setDescription(description);
		
		applicationService.createApplication(application, connectorId, importSandboxes, importServices);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(ApplicationResource.class)
				.path("/{id}")
				.build(application.getId());
		
		return Response.created(uri)
				.entity(application)
				.build();	
	}
	
	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateApplication(
			@PathParam("id") String id,
			@FormParam("name") @NotEmpty String name,
			@FormParam("description") @NotEmpty String description) {
		
		Application application = new Application();
		application.setName(name);
		application.setDescription(description);
		
		applicationService.updateApplication( id, application );
		
		return Response.ok()
				.entity(application)
				.build();	
	}
	
	@GET
	@Path("{id}/environment/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEnvironment(@PathParam(value="id") String id, @PathParam(value="key") String key) {		
		
		Instance instance = applicationService.getEnvironment( id, key);
		
		if (instance == null) {
			throw new NotFoundException(String.format("Environment for key %s was not found",key));
		}
		
		return Response.ok()
				.entity(instance)
				.build(); 
	}
	
	@POST
	@Path("{id}/environment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addEnvironment(@PathParam(value="id") String id, Instance instance) {
		
		applicationService.addEnvironment( id, instance);
		
		return Response.ok()
				.entity(instance)
				.build(); 
	}
	
	@PUT
	@Path("{id}/environment/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateEnvironment(@PathParam(value="id") String id, @PathParam(value="key") String key, Instance instance) {
		
		applicationService.updateEnvironment( id, key, instance);
		
		return Response.ok()
				.entity(instance)
				.build(); 
	}
	
	@POST
	@Path("{id}/environment/{key}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateEnvironment(@PathParam(value="id") String id, @PathParam(value="key") String key, MultivaluedMap<String, String> parameters) {
		
		Instance instance = applicationService.updateEnvironment( id, key, parameters);
		
		return Response.ok()
				.entity(instance)
				.build(); 
	}
	
	@DELETE
	@Path("{id}/environment/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeEnvironment(@PathParam(value="id") String id, @PathParam(value="key") String key) {
		
		applicationService.removeEnvironment( id, key);
		
		return Response.ok()
				.build(); 
	}
}