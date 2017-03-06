package com.nowellpoint.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("job-specifications")
public interface JobSpecificationResource {
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllByOwner();
	
	@GET
	@Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getJobSpecification(@PathParam("id") String id);
	
	@DELETE
	@Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response deleteJobSpecification(@PathParam("id") String id);
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createJobSpecification(
			@FormParam("jobTypeId") String jobTypeId,
			@FormParam("connectorId") String connectorId,
			@FormParam("name") String name,
			@FormParam("notificationEmail") String notificationEmail,
			@FormParam("description") String description);
	
	@POST
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateJobSpecification(@PathParam("id") String id,
			@FormParam("name") String name,
			@FormParam("notificationEmail") String notificationEmail,
			@FormParam("description") String description);
}