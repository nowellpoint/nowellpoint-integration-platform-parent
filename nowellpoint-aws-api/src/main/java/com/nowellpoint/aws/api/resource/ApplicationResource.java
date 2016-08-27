package com.nowellpoint.aws.api.resource;

import java.net.URI;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
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

import org.hibernate.validator.constraints.NotEmpty;

import com.nowellpoint.aws.api.dto.AccountProfileDTO;
import com.nowellpoint.aws.api.dto.ApplicationDTO;
import com.nowellpoint.aws.api.dto.Id;
import com.nowellpoint.aws.api.dto.SalesforceConnectorDTO;
import com.nowellpoint.aws.api.dto.ServiceProviderDTO;
import com.nowellpoint.aws.api.model.ServiceInstance;
import com.nowellpoint.aws.api.service.AccountProfileService;
import com.nowellpoint.aws.api.service.ApplicationService;
import com.nowellpoint.aws.api.service.SalesforceConnectorService;
import com.nowellpoint.aws.api.service.ServiceProviderService;

@Path("/applications")
public class ApplicationResource {
	
	@Inject
	private ApplicationService applicationService;
	
	@Inject
	private ServiceProviderService serviceProviderService;
	
	@Inject
	private SalesforceConnectorService salesforceConnectorService;

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
	@Path("/{id}")
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
	@Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response deleteApplication(@PathParam("id") String id) {
		String subject = securityContext.getUserPrincipal().getName();
		
		applicationService.deleteApplication(id, subject, uriInfo.getBaseUri());
		
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
			@FormParam("importSandboxes") @NotEmpty Boolean importSandboxes,
			@FormParam("importServices") @NotEmpty Boolean importServices) {
		
		ApplicationDTO resource = new ApplicationDTO();
		resource.setName(name);
		resource.setDescription(description);
		
		applicationService.createApplication(resource, connectorId, importSandboxes, importServices);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(ApplicationResource.class)
				.path("/{id}")
				.build(resource.getId());
		
		return Response.created(uri)
				.entity(resource)
				.build();	
	}
	
//	@Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//	public Response createApplication(ApplicationDTO resource) {
//		String subject = securityContext.getUserPrincipal().getName();
//		
//		AccountProfileDTO owner = accountProfileService.findAccountProfileBySubject(subject);	
//		
//		resource.setOwner(owner);
//		resource.setSubject(subject);
//		resource.setEventSource(uriInfo.getBaseUri());
//		
//		applicationService.createApplication(resource);
//		
//		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
//				.path(ApplicationResource.class)
//				.path("/{id}")
//				.build(resource.getId());
//		
//		return Response.created(uri)
//				.entity(resource)
//				.build();
//	}
	
	/**
	 * 
	 * @param id
	 * @param name
	 * @return
	 */
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateApplication(
			@PathParam("id") String id,
			@FormParam("name") @NotEmpty String name) {
		
		ApplicationDTO resource = applicationService.findApplication( new Id(id) );
		resource.setId(id);
		resource.setName(name);
		
		applicationService.updateApplication(resource);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(ApplicationResource.class)
				.path("/{id}")
				.build(resource.getId());
		
		return Response.created(uri)
				.entity(resource)
				.build();	
	}
	
//	@PUT
//	@Path("/{id}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response updateApplication(@PathParam("id") String id, ApplicationDTO resource) {
//		String subject = securityContext.getUserPrincipal().getName();
//		
//		resource.setSubject(subject);
//		resource.setId(id);
//		
//		applicationService.updateApplication(resource);
//		
//		return Response.ok(resource).build();
//	}
}