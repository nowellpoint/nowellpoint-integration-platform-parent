package com.nowellpoint.aws.api.resource;

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

import com.nowellpoint.aws.api.dto.ApplicationDTO;
import com.nowellpoint.aws.api.dto.EnvironmentDTO;
import com.nowellpoint.aws.api.dto.Id;
import com.nowellpoint.aws.api.dto.ServiceInstanceDTO;
import com.nowellpoint.aws.api.service.ApplicationService;

@Path("/applications")
public class ApplicationResource {
	
	@Inject
	private ApplicationService applicationService;

	@Context
	private UriInfo uriInfo;
	
	@Context
	private SecurityContext securityContext;
	
	/**
	 * 
	 * @return
	 */
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
		String subject = securityContext.getUserPrincipal().getName();
		
		Set<ApplicationDTO> resources = applicationService.getAll(subject);
		
		return Response.ok(resources).build();
    }
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	@GET
	@Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getApplication(@PathParam("id") String id) {
		
		ApplicationDTO resource = applicationService.findApplication( new Id(id) );
		
		return Response.ok(resource).build();
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	@DELETE
	@Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response deleteApplication(@PathParam("id") String id) {
		
		applicationService.deleteApplication( new Id(id));
		
		return Response.noContent().build();
	}
	
	/**
	 * 
	 * @param resource
	 * @return
	 */
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createApplication(
			@FormParam("description") @NotEmpty String description,
			@FormParam("connectorId") @NotEmpty String connectorId,
			@FormParam("name") @NotEmpty String name,
			@FormParam("importSandboxes") Boolean importSandboxes,
			@FormParam("importServices") Boolean importServices) {
		
		ApplicationDTO resource = new ApplicationDTO();
		resource.setName(name);
		resource.setDescription(description);
		
		System.out.println(importServices);
		
		applicationService.createApplication(resource, connectorId, importSandboxes, importServices);
		
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
	 * @param id
	 * @param name
	 * @return
	 */
	
	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateApplication(
			@PathParam("id") String id,
			@FormParam("name") @NotEmpty String name,
			@FormParam("description") @NotEmpty String description) {
		
		ApplicationDTO resource = new ApplicationDTO();
		resource.setName(name);
		resource.setDescription(description);
		
		applicationService.updateApplication(new Id(id), resource);
		
		return Response.ok()
				.entity(resource)
				.build();	
	}
	
	@GET
	@Path("{id}/environment/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEnvironment(@PathParam(value="id") String id, @PathParam(value="key") String key) {		
		
		EnvironmentDTO resource = applicationService.getEnvironment(new Id(id), key);
		
		if (resource == null) {
			throw new NotFoundException(String.format("Environment for key %s was not found",key));
		}
		
		return Response.ok()
				.entity(resource)
				.build(); 
	}
	
	@POST
	@Path("salesforce/{id}/environment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addEnvironment(@PathParam(value="id") String id, EnvironmentDTO resource) {
		
		applicationService.addEnvironment(new Id(id), resource);
		
		return Response.ok()
				.entity(resource)
				.build(); 
	}
	
	@POST
	@Path("{id}/environment/{key}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateEnvironment(@PathParam(value="id") String id, @PathParam(value="key") String key, MultivaluedMap<String, String> parameters) {
		
		EnvironmentDTO resource = applicationService.updateEnvironment(new Id(id), key, parameters);
		
		return Response.ok()
				.entity(resource)
				.build(); 
	}
	
	@DELETE
	@Path("{id}/environment/{key}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeEnvironment(@PathParam(value="id") String id, @PathParam(value="key") String key) {
		
		applicationService.removeEnvironment(new Id(id), key);
		
		return Response.ok()
				.build(); 
	}
	
	@GET
	@Path("{id}/service/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getServiceInstance(
			@PathParam(value="id") String id,
			@PathParam(value="key") String key) {
		
		ServiceInstanceDTO resource = applicationService.getServiceInstance( new Id(id), key );
		
		return Response.ok()
				.entity(resource)
				.build(); 	
	}
	
	@POST
	@Path("{id}/service")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addServiceInstance(@PathParam(value="id") String id, @FormParam(value="key") String key) {
		
		ServiceInstanceDTO resource = applicationService.addServiceInstance( new Id(id), key);
		
		if ( resource == null ) {
			throw new NotFoundException( String.format( "%s Key: %s does not exist or you do not have access to view", "Service", key ) );
		}
		
		return Response.ok()
				.entity(resource)
				.build(); 	
	}
	
	@PUT
	@Path("{id}/service/{key}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateServiceInstance(
			@PathParam(value="id") String id,
			@PathParam(value="key") String key,
			ServiceInstanceDTO resource) {
		
		applicationService.updateServiceInstance( new Id(id), key, resource);
		
		return Response.ok()
				.entity(resource)
				.build(); 	
	}
	
	/**
	 * 
	 * @param id
	 * @param key
	 * @param parameters
	 * @return
	 */
	
	@POST
	@Path("{id}/service/{key}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateServiceInstance(@PathParam(value="id") String id, @PathParam(value="key") String key, MultivaluedMap<String, String> parameters) {
		
		ServiceInstanceDTO resource = applicationService.updateServiceInstance( new Id(id), key, parameters);
		
		return Response.ok()
				.entity(resource)
				.build(); 	
	}
}