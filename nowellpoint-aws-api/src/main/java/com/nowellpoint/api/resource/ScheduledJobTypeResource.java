package com.nowellpoint.api.resource;

import java.net.URI;

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

import com.nowellpoint.api.rest.domain.JobType;
import com.nowellpoint.api.rest.domain.JobTypeList;
import com.nowellpoint.api.service.JobTypeService;

@Path("scheduled-job-types")
public class ScheduledJobTypeResource {
	
	@Context
	private UriInfo uriInfo;
	
	@Context 
	private SecurityContext securityContext;
	
	@Inject
	private JobTypeService jobTypeService;
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByLanguage(@QueryParam("languageSidKey") String languageSidKey) {
		JobTypeList resource = jobTypeService.findByLanguage(languageSidKey);
		return Response.ok(resource).build();
    }
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getServiceProvider(@PathParam("id") String id) {		
		JobType jobType = jobTypeService.findById( id );		
		return Response.ok(jobType)
				.build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response createServiceProvider(JobType jobType) {
		
		jobTypeService.createScheduledJobType(jobType);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(ScheduledJobTypeResource.class)
				.path("/{id}")
				.build(jobType.getId());
		
		return Response.created(uri)
				.entity(jobType)
				.build();
	}
	
	@DELETE
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteServiceProvider(@PathParam("id") String id) {
		jobTypeService.deleteScheduledJobType(id);		
		return Response.noContent().build();
	}
	
	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateServiceProvider(@PathParam("id") String id, JobType jobType) {
		jobTypeService.updateScheduledJobType(id, jobType);
		return Response.ok(jobType).build();
	}
}