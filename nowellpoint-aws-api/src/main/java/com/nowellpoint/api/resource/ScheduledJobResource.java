package com.nowellpoint.api.resource;

import static com.nowellpoint.util.Assert.isNotNullOrEmpty;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.logging.Logger;

import com.nowellpoint.api.rest.domain.RunHistory;
import com.nowellpoint.api.rest.domain.ScheduledJob;
import com.nowellpoint.api.rest.domain.ScheduledJobList;
import com.nowellpoint.api.service.ScheduledJobService;

@Path("scheduled-jobs")
public class ScheduledJobResource {
	
	private static final Logger LOGGER = Logger.getLogger(ScheduledJobResource.class);
	
	@Inject
	private ScheduledJobService scheduledJobService;
	
	@Context
	private HttpServletRequest httpServletRequest;

	@Context
	private UriInfo uriInfo;
	
	@Context
	private SecurityContext securityContext;
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllByOwner() {
		ScheduledJobList resources = scheduledJobService.findByOwner(securityContext.getUserPrincipal().getName());
		return Response.ok(resources).build();
    }
	
	@GET
	@Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getScheduledJob(@PathParam("id") String id) {
		ScheduledJob scheduledJob = scheduledJobService.findById( id );
		
		if (scheduledJob == null) {
			throw new NotFoundException( String.format( "%s Id: %s does not exist or you do not have access to view", ScheduledJob.class.getSimpleName(), id ) );
		}
		
		return Response.ok(scheduledJob).build();
		
	}
	
	@DELETE
	@Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response deleteScheduledJob(@PathParam("id") String id) {
		scheduledJobService.deleteScheduledJob( id );
		return Response.noContent().build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createScheduledJob(@FormParam("scheduledJobTypeId") @NotEmpty String scheduledJobTypeId) {
		
		ScheduledJob scheduledJob = scheduledJobService.createScheduledJob(scheduledJobTypeId);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(ScheduledJobResource.class)
				.path("/{id}")
				.build(scheduledJob.getId());
		
		return Response.created(uri)
				.entity(scheduledJob)
				.build();	
	}
	
//	@POST
//	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response createScheduledJob(
//			@FormParam("jobTypeId") @NotEmpty String jobTypeId,
//			@FormParam("notificationEmail") String notificationEmail,
//			@FormParam("description") String description,
//			@FormParam("connectorId") @NotEmpty String connectorId,
//			@FormParam("environmentKey") String environmentKey,
//			@FormParam("scheduleDate") @NotEmpty String scheduleDate) {
//		
//		ScheduledJob scheduledJob = new ScheduledJob();
//		scheduledJob.setNotificationEmail(notificationEmail);
//		scheduledJob.setConnectorId(connectorId);
//		scheduledJob.setEnvironmentKey(environmentKey);
//		scheduledJob.setJobTypeId(jobTypeId);
//		scheduledJob.setDescription(description);
//
//		try {
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"); 
//			scheduledJob.setScheduleDate(sdf.parse(scheduleDate));
//		} catch (Exception e) {
//			LOGGER.warn(httpServletRequest.getRequestURI() + " " + e.getMessage());
//			throw new BadRequestException(e.getMessage());
//		}
//		
//		scheduledJobService.createScheduledJob(scheduledJob);
//		
//		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
//				.path(ScheduledJobResource.class)
//				.path("/{id}")
//				.build(scheduledJob.getId());
//		
//		return Response.created(uri)
//				.entity(scheduledJob)
//				.build();	
//	}
	
	@POST
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateScheduledJob(@PathParam("id") String id,
			@FormParam("notificationEmail") String notificationEmail,
			@FormParam("description") String description,
			@FormParam("connectorId") String connectorId,
			@FormParam("environmentKey") String environmentKey,
			@FormParam("scheduleDate") String scheduleDate) {
		
		ScheduledJob scheduledJob = new ScheduledJob();
		scheduledJob.setNotificationEmail(notificationEmail);
		scheduledJob.setDescription(description);
		scheduledJob.setConnectorId(isNotNullOrEmpty(connectorId) ? connectorId : null);
		scheduledJob.setEnvironmentKey(isNotNullOrEmpty(environmentKey) ? environmentKey : null);

		if (isNotNullOrEmpty(scheduleDate)) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"); 
				scheduledJob.setScheduleDate(sdf.parse(scheduleDate));
			} catch (Exception e) {
				LOGGER.warn(e.getMessage());
				throw new BadRequestException(e.getMessage());
			}
		}
		
		scheduledJobService.updateScheduledJob(id, scheduledJob);
		
		return Response.ok()
				.entity(scheduledJob)
				.build();	
	}
	
	@POST
	@Path("{id}/actions/{action}/invoke")
	@Produces(MediaType.APPLICATION_JSON)
	public Response invokeAction(@PathParam(value="id") String id, @PathParam(value="action") String action) {
		
		ScheduledJob scheduledJob = null;
		
		if ("terminate".equalsIgnoreCase(action)) {
			scheduledJob = scheduledJobService.terminateScheduledJob(id);
		} else if ("start".equalsIgnoreCase(action)) {
			scheduledJob = scheduledJobService.startScheduledJob(id);
		} else if ("stop".equalsIgnoreCase(action)) {
			scheduledJob = scheduledJobService.stopScheduledJob(id);
		} else {
			throw new BadRequestException(String.format("Invalid action: %s", action));
		}
		
		return Response.ok()
				.entity(scheduledJob)
				.build(); 
	}
	
	@GET
	@Path("{id}/run-history/{fireInstanceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRunHistory(@PathParam("id") String id, @PathParam("fireInstanceId") String fireInstanceId) {
		
		RunHistory resource = scheduledJobService.findRunHistory(id, fireInstanceId);
		
		if (resource == null) {
			throw new NotFoundException(String.format("Run History for Scheduled Job: %s with Instance Id: %s was not found", id, fireInstanceId));
		}
		
		return Response
				.ok(resource)
				.build();
	}
	
	@GET
	@Path("{id}/run-history/{fireInstanceId}/file/{filename}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getFile(@PathParam("id") String id, @PathParam("fireInstanceId") String fireInstanceId, @PathParam("filename") String filename) {

		String content = null;
		
		try {
			content = scheduledJobService.getFile(id, fireInstanceId, filename);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new BadRequestException(e.getMessage());
		}
		
		return Response.ok()
				.header("Content-Disposition", String.format("attachment; filename=\"%s.json\"", filename))
				.entity(content)
				.build();	
	}
}