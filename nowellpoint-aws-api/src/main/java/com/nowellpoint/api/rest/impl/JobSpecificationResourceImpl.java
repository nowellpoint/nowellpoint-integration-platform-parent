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
			String connectorId,
			String name,
			String notificationEmail,
			String description) {
		
		JobSpecification jobSpecification = JobSpecification.builder()
				.withCreatedBy(securityContext.getUserPrincipal().getName())
				.withDescription(description)
				.withName(name)
				.withJobType("")
				.withLastUpdatedBy(securityContext.getUserPrincipal().getName())
				.withNotificationEmail(notificationEmail)
				.withOwner(securityContext.getUserPrincipal().getName())
				.build();
		
		jobSpecificationService.createJobSpecification(jobSpecification);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(JobSpecificationResource.class)
				.path("/{id}")
				.build(jobSpecification.getId());
		
		return Response.created(uri)
				.entity(jobSpecification)
				.build();	
	}
	
	@Override
	public Response updateJobSpecification(
			String id,
			String name,
			String notificationEmail,
			String description) {
		
		JobSpecification jobSpecification = JobSpecification.builder()
				.withDescription(description)
				.withName(name)
				.withJobType("")
				.withLastUpdatedBy(securityContext.getUserPrincipal().getName())
				.withNotificationEmail(notificationEmail)
				.build();
		
		jobSpecificationService.createJobSpecification(jobSpecification);
		
		return Response.ok()
				.entity(jobSpecification)
				.build();	
	}
}