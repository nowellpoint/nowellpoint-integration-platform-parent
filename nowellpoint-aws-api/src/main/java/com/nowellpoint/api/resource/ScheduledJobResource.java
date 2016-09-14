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

import com.nowellpoint.api.model.dto.Id;
import com.nowellpoint.api.model.dto.Schedule;
import com.nowellpoint.api.model.dto.ScheduledJob;
import com.nowellpoint.api.service.ScheduledJobService;

@Path("scheduled-jobs")
public class ScheduledJobResource {
	
	@Inject
	private ScheduledJobService scheduledJobService;

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
    public Response findAllByOwner() {
		Set<ScheduledJob> resources = scheduledJobService.findAllByOwner();
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
	public Response getScheduledJob(@PathParam("id") String id) {
		ScheduledJob scheduledJob = scheduledJobService.findScheduledJobById( new Id( id ) );
		
		if (scheduledJob == null) {
			throw new NotFoundException( String.format( "%s Id: %s does not exist or you do not have access to view", ScheduledJob.class.getSimpleName(), id ) );
		}
		
		return Response.ok(scheduledJob).build();
		
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	@DELETE
	@Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response deleteScheduledJob(@PathParam("id") String id) {
		scheduledJobService.deleteScheduledJob( new Id(id));
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
	public Response createScheduledJob(
			@FormParam("description") String description,
			@FormParam("connectorId") @NotEmpty String connectorId,
			@FormParam("connectorType") @NotEmpty String connectorType) {
		
		ScheduledJob scheduledJob = new ScheduledJob();
		scheduledJob.getConnector().setCode(connectorType);
		scheduledJob.getConnector().setId(connectorId);
		scheduledJob.setDescription(description);
		
		scheduledJobService.createScheduledJob(scheduledJob);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(ScheduledJobResource.class)
				.path("/{id}")
				.build(scheduledJob.getId());
		
		return Response.created(uri)
				.entity(scheduledJob)
				.build();	
	}
	
	/**
	 * 
	 * @param id
	 * @param name
	 * @param description
	 * @return
	 */
	
	@POST
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateScheduledJob(@PathParam("id") String id,
			@FormParam("name") @NotEmpty String name,
			@FormParam("description") String description) {
		
		ScheduledJob scheduledJob = new ScheduledJob();
		scheduledJob.setDescription(description);
		
		scheduledJobService.updateScheduledJob(new Id(id), scheduledJob);
		
		return Response.ok()
				.entity(scheduledJob)
				.build();	
	}
}