package com.nowellpoint.api.rest.impl;

import java.net.URI;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.api.rest.JobSpecificationResource;
import com.nowellpoint.api.rest.domain.JobSpecification;
import com.nowellpoint.api.rest.domain.JobSpecificationList;
import com.nowellpoint.api.service.JobSpecificationService;

public class JobSpecificationResourceImpl implements JobSpecificationResource {
	
	@Inject
	private JobSpecificationService jobSpecificationService;
	
	@Context
	private HttpServletRequest httpServletRequest;

	@Context
	private UriInfo uriInfo;
	
	@Context
	private SecurityContext securityContext;
	
	@Override
    public Response findAllByOwner() {
		JobSpecificationList list = jobSpecificationService.findByOwner(securityContext.getUserPrincipal().getName());
		return Response.ok(list).build();
    }
	
	@Override
	public Response getJobSpecification(String id) {
		JobSpecification jobSpecification = jobSpecificationService.findById( id );
		
		if (jobSpecification == null) {
			throw new NotFoundException( String.format( "%s Id: %s does not exist or you do not have access to view", JobSpecification.class.getSimpleName(), id ) );
		}
		
		return Response.ok(jobSpecification).build();
		
	}
	
	@Override
	public Response deleteJobSpecification(String id) {
		jobSpecificationService.deleteJobSpecification( id );
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
		
		JobSpecification jobSchedule = jobSpecificationService.createJobSpecification(
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
		
		JobSpecification jobSchedule = jobSpecificationService.updateJobSpecification(
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
}