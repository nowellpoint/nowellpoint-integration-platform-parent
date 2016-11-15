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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.api.model.domain.ScheduledJobType;
import com.nowellpoint.api.service.ScheduledJobTypeService;

@Path("scheduled-job-types")
public class ScheduledJobTypeResource {
	
	@Context
	private UriInfo uriInfo;
	
	@Context 
	private SecurityContext securityContext;
	
	@Inject
	private ScheduledJobTypeService scheduledJobTypeService;
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByLanguage(@QueryParam("languageSidKey") String languageSidKey) {
		Set<ScheduledJobType> scheduledJobTypes = scheduledJobTypeService.findByLanguage(languageSidKey);
		return Response.ok(scheduledJobTypes).build();
    }
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getServiceProvider(@PathParam("id") String id) {		
		ScheduledJobType scheduledJobType = scheduledJobTypeService.findScheduedJobTypeById( id );		
		return Response.ok(scheduledJobType)
				.build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response createServiceProvider(ScheduledJobType scheduledJobType) {
		
		scheduledJobTypeService.createScheduledJobType(scheduledJobType);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(ScheduledJobTypeResource.class)
				.path("/{id}")
				.build(scheduledJobType.getId());
		
		return Response.created(uri)
				.entity(scheduledJobType)
				.build();
	}
	
	@DELETE
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteServiceProvider(@PathParam("id") String id) {
		scheduledJobTypeService.deleteScheduledJobType(id);		
		return Response.noContent().build();
	}
	
	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateServiceProvider(@PathParam("id") String id, ScheduledJobType scheduledJobType) {
		scheduledJobTypeService.updateScheduledJobType(id, scheduledJobType);
		return Response.ok(scheduledJobType).build();
	}
}