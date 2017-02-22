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

import org.hibernate.validator.constraints.NotEmpty;

@Path("job-specifications")
public interface JobSpecificationResource {
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllByOwner();
	
	@GET
	@Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getScheduledJob(@PathParam("id") String id);
	
	@DELETE
	@Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response deleteScheduledJob(@PathParam("id") String id);
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createJobSpecification(
			@FormParam("jobTypeId") @NotEmpty String jobTypeId,
			@FormParam("instanceKey") @NotEmpty String instanceKey,
			@FormParam("notificationEmail") String notificationEmail,
			@FormParam("description") String description,
			@FormParam("connectorId") @NotEmpty String connectorId,
			@FormParam("start") String start,
			@FormParam("end") String end,
			@FormParam("timeZone") String timeZone,
			@FormParam("seconds") String seconds,
			@FormParam("minutes") String minutes,
			@FormParam("hours") String hours,
			@FormParam("dayOfMonth") String dayOfMonth,
			@FormParam("month") String month,
			@FormParam("dayOfWeek") String dayOfWeek,
			@FormParam("year") String year);
	
	@POST
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateJobSpecification(@PathParam("id") String id,
			@FormParam("notificationEmail") String notificationEmail,
			@FormParam("description") String description,
			@FormParam("start") String start,
			@FormParam("end") String end,
			@FormParam("timeZone") String timeZone,
			@FormParam("seconds") String seconds,
			@FormParam("minutes") String minutes,
			@FormParam("hours") String hours,
			@FormParam("dayOfMonth") String dayOfMonth,
			@FormParam("month") String month,
			@FormParam("dayOfWeek") String dayOfWeek,
			@FormParam("year") String year);
	
	/**
	 * 
	 * @param id
	 * @param action
	 * @return
	 */
	
	@POST
	@Path("{id}/actions/{action}/invoke")
	@Produces(MediaType.APPLICATION_JSON)
	public Response invokeAction(@PathParam(value="id") String id, @PathParam(value="action") String action);
	
	@GET
	@Path("{id}/run-history/{fireInstanceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRunHistory(@PathParam("id") String id, @PathParam("fireInstanceId") String fireInstanceId);
	
	@GET
	@Path("{id}/run-history/{fireInstanceId}/file/{filename}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getFile(@PathParam("id") String id, @PathParam("fireInstanceId") String fireInstanceId, @PathParam("filename") String filename);
}