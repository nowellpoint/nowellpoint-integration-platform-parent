package com.nowellpoint.api.resource;

import static com.nowellpoint.util.Assert.isNotNullOrEmpty;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.TimeZone;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.api.model.dto.Id;
import com.nowellpoint.api.model.dto.ScheduledJob;
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
	
	/**
	 * 
	 * @return
	 */
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllByOwner() {
		System.out.println("get scheduled Jobs");
		Set<ScheduledJob> resources = scheduledJobService.findAllByOwner();
		System.out.println("schedule job count: " + resources.size());
		try {
			System.out.println(new ObjectMapper().writeValueAsString(resources));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		scheduledJobService.deleteScheduledJob( new Id(id) );
		return Response.noContent().build();
	}
	
	/**
	 * 
	 * @param description
	 * @param connectorId
	 * @param environmentKey
	 * @param jobTypeId
	 * @param scheduleDate
	 * @param status
	 * @return
	 */
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createScheduledJob(
			@FormParam("jobTypeId") @NotEmpty String jobTypeId,
			@FormParam("notificationEmail") String notificationEmail,
			@FormParam("description") String description,
			@FormParam("connectorId") @NotEmpty String connectorId,
			@FormParam("environmentKey") String environmentKey,
			@FormParam("scheduleDate") @NotEmpty String scheduleDate,
			@FormParam("status") String status) {
		
		ScheduledJob scheduledJob = new ScheduledJob();
		scheduledJob.setNotificationEmail(notificationEmail);
		scheduledJob.setConnectorId(connectorId);
		scheduledJob.setEnvironmentKey(environmentKey);
		scheduledJob.setJobTypeId(jobTypeId);
		scheduledJob.setDescription(description);
		scheduledJob.setStatus(status);
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"); 
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));  
			scheduledJob.setScheduleDate(sdf.parse(scheduleDate));
		} catch (Exception e) {
			LOGGER.warn(httpServletRequest.getRequestURI() + " " + e.getMessage());
			throw new BadRequestException(e.getMessage());
		}
		
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
	 * @param description
	 * @param connectorId
	 * @param environmentKey
	 * @param scheduleDate
	 * @param status
	 * @return
	 */
	
	@POST
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateScheduledJob(@PathParam("id") String id,
			@FormParam("notificationEmail") String notificationEmail,
			@FormParam("description") String description,
			@FormParam("connectorId") String connectorId,
			@FormParam("environmentKey") String environmentKey,
			@FormParam("scheduleDate") String scheduleDate,
			@FormParam("status") String status) {
		
		ScheduledJob scheduledJob = new ScheduledJob();
		scheduledJob.setNotificationEmail(notificationEmail);
		scheduledJob.setDescription(description);
		scheduledJob.setConnectorId(isNotNullOrEmpty(connectorId) ? connectorId : null);
		scheduledJob.setEnvironmentKey(isNotNullOrEmpty(environmentKey) ? environmentKey : null);
		scheduledJob.setStatus(isNotNullOrEmpty(status) ? status : null);
		if (isNotNullOrEmpty(scheduleDate)) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"); 
				sdf.setTimeZone(TimeZone.getTimeZone("UTC"));  
				scheduledJob.setScheduleDate(sdf.parse(scheduleDate));
			} catch (Exception e) {
				LOGGER.warn(e.getMessage());
				throw new BadRequestException(e.getMessage());
			}
		}
		
		scheduledJobService.updateScheduledJob(new Id(id), scheduledJob);
		
		return Response.ok()
				.entity(scheduledJob)
				.build();	
	}
}