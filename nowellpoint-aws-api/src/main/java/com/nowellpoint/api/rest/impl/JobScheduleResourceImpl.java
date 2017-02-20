package com.nowellpoint.api.rest.impl;

import static com.nowellpoint.util.Assert.isNotNullOrEmpty;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

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

import com.nowellpoint.api.rest.JobScheduleResource;
import com.nowellpoint.api.rest.domain.JobSchedule;
import com.nowellpoint.api.rest.domain.JobScheduleList;
import com.nowellpoint.api.rest.domain.RunHistory;
import com.nowellpoint.api.service.JobScheduleService;

public class JobScheduleResourceImpl implements JobScheduleResource {
	
	private static final Logger LOGGER = Logger.getLogger(JobScheduleResourceImpl.class);
	
	@Inject
	private JobScheduleService jobScheduleService;
	
	@Context
	private HttpServletRequest httpServletRequest;

	@Context
	private UriInfo uriInfo;
	
	@Context
	private SecurityContext securityContext;
	
	@Override
    public Response findAllByOwner() {
		JobScheduleList resources = jobScheduleService.findByOwner(securityContext.getUserPrincipal().getName());
		return Response.ok(resources).build();
    }
	
	@Override
	public Response getScheduledJob(String id) {
		JobSchedule jobSchedule = jobScheduleService.findById( id );
		
		if (jobSchedule == null) {
			throw new NotFoundException( String.format( "%s Id: %s does not exist or you do not have access to view", JobSchedule.class.getSimpleName(), id ) );
		}
		
		return Response.ok(jobSchedule).build();
		
	}
	
	@Override
	public Response deleteScheduledJob(String id) {
		jobScheduleService.deleteScheduledJob( id );
		return Response.noContent().build();
	}
	
	@Override
	public Response createScheduledJob(
			String jobTypeId,
			String instanceKey,
			String notificationEmail,
			String description,
			String connectorId,
			String scheduleDate,
			String seconds,
			String minutes,
			String hours,
			String dayOfMonth,
			String month,
			String dayOfWeek,
			String year) {
		
		Date parsedDate = null;
		
		if (isNotNullOrEmpty(scheduleDate)) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
				parsedDate = sdf.parse(scheduleDate);
			} catch (Exception e) {
				LOGGER.warn(e.getMessage());
				throw new BadRequestException(e.getMessage());
			}
		}
		
		JobSchedule jobSchedule = jobScheduleService.createJobSchedule(
				jobTypeId, 
				connectorId, 
				instanceKey, 
				parsedDate,
				seconds,
				minutes,
				hours,
				dayOfMonth,
				month,
				dayOfWeek,
				year);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(JobScheduleResource.class)
				.path("/{id}")
				.build(jobSchedule.getId());
		
		return Response.created(uri)
				.entity(jobSchedule)
				.build();	
	}
	
	@Override
	public Response updateScheduledJob(
			String id,
			String notificationEmail,
			String description,
			String scheduleDate,
			String seconds,
			String minutes,
			String hours,
			String dayOfMonth,
			String month,
			String dayOfWeek,
			String year) {
		
		JobSchedule jobSchedule = new JobSchedule();
		jobSchedule.setNotificationEmail(notificationEmail);
		jobSchedule.setDescription(description);
		jobSchedule.setSeconds(seconds);
		jobSchedule.setMinutes(minutes);
		jobSchedule.setHours(hours);
		jobSchedule.setDayOfMonth(dayOfMonth);
		jobSchedule.setMonth(month);
		jobSchedule.setDayOfWeek(dayOfWeek);
		jobSchedule.setYear(year);

		if (isNotNullOrEmpty(scheduleDate)) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
				jobSchedule.setScheduleDate(sdf.parse(scheduleDate));
			} catch (Exception e) {
				LOGGER.warn(e.getMessage());
				throw new BadRequestException(e.getMessage());
			}
		}
		
		jobScheduleService.updateScheduledJob(id, jobSchedule);
		
		return Response.ok()
				.entity(jobSchedule)
				.build();	
	}
	
	@Override
	public Response invokeAction(String id, String action) {
		
		JobSchedule jobSchedule = null;
		
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