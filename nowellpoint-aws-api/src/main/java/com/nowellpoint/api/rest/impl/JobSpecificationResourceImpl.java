package com.nowellpoint.api.rest.impl;

import java.io.IOException;
import java.net.URI;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;

import com.nowellpoint.api.rest.JobSpecificationResource;
import com.nowellpoint.api.rest.domain.JobSpecification;
import com.nowellpoint.api.rest.domain.JobSpecificationList;
import com.nowellpoint.api.rest.domain.RunHistory;
import com.nowellpoint.api.service.JobSpecificationService;

public class JobSpecificationResourceImpl implements JobSpecificationResource {
	
	private static final Logger LOGGER = Logger.getLogger(JobSpecificationResourceImpl.class);
	
	@Inject
	private JobSpecificationService jobScheduleService;
	
	@Context
	private HttpServletRequest httpServletRequest;

	@Context
	private UriInfo uriInfo;
	
	@Context
	private SecurityContext securityContext;
	
	@Override
    public Response findAllByOwner() {
		JobSpecificationList resources = jobScheduleService.findByOwner(securityContext.getUserPrincipal().getName());
		return Response.ok(resources).build();
    }
	
	@Override
	public Response getScheduledJob(String id) {
		JobSpecification jobSchedule = jobScheduleService.findById( id );
		
		if (jobSchedule == null) {
			throw new NotFoundException( String.format( "%s Id: %s does not exist or you do not have access to view", JobSpecification.class.getSimpleName(), id ) );
		}
		
		return Response.ok(jobSchedule).build();
		
	}
	
	@Override
	public Response deleteScheduledJob(String id) {
		jobScheduleService.deleteScheduledJob( id );
		return Response.noContent().build();
	}
	
	@Override
	public Response createJobSpecification(
			String jobTypeId,
			String instanceKey,
			String notificationEmail,
			String description,
			String connectorId,
			String start,
			String end,
			String timeZone,
			String seconds,
			String minutes,
			String hours,
			String dayOfMonth,
			String month,
			String dayOfWeek,
			String year) {
		
		JobSpecification jobSchedule = jobScheduleService.createJobSchedule(
				jobTypeId, 
				connectorId, 
				instanceKey, 
				start,
				end,
				timeZone,
				seconds,
				minutes,
				hours,
				dayOfMonth,
				month,
				dayOfWeek,
				year,
				notificationEmail,
				description);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(JobSpecificationResource.class)
				.path("/{id}")
				.build(jobSchedule.getId());
		
		return Response.created(uri)
				.entity(jobSchedule)
				.build();	
	}
	
	@Override
	public Response updateJobSpecification(
			String id,
			String notificationEmail,
			String description,
			String start,
			String end,
			String timeZone,
			String seconds,
			String minutes,
			String hours,
			String dayOfMonth,
			String month,
			String dayOfWeek,
			String year) {
		
		JobSpecification jobSchedule = jobScheduleService.updateScheduledJob(
				id, 
				start, 
				end,
				timeZone,
				seconds, 
				minutes, 
				hours, 
				dayOfMonth, 
				month, 
				dayOfWeek, 
				year,
				notificationEmail,
				description);
		
		return Response.ok()
				.entity(jobSchedule)
				.build();	
	}
	
	@Override
	public Response invokeAction(String id, String action) {
		
		JobSpecification jobSchedule = null;
		
		if ("terminate".equalsIgnoreCase(action)) {
			jobSchedule = jobScheduleService.terminateScheduledJob(id);
		} else if ("start".equalsIgnoreCase(action)) {
			jobSchedule = jobScheduleService.startScheduledJob(id);
		} else if ("stop".equalsIgnoreCase(action)) {
			jobSchedule = jobScheduleService.stopScheduledJob(id);
		} else {
			throw new BadRequestException(String.format("Invalid action: %s", action));
		}
		
		return Response.ok()
				.entity(jobSchedule)
				.build(); 
	}
	
	@Override
	public Response getRunHistory(String id, String fireInstanceId) {
		
		RunHistory resource = jobScheduleService.findRunHistory(id, fireInstanceId);
		
		if (resource == null) {
			throw new NotFoundException(String.format("Run History for Scheduled Job: %s with Instance Id: %s was not found", id, fireInstanceId));
		}
		
		return Response
				.ok(resource)
				.build();
	}
	
	@Override
	public Response getFile(String id, String fireInstanceId, String filename) {

		String content = null;
		
		try {
			content = jobScheduleService.getFile(id, fireInstanceId, filename);
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