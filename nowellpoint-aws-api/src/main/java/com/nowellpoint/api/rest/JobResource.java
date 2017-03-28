package com.nowellpoint.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("jobs")
public interface JobResource {
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllByOwner();
	
	@GET
	@Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getJob(@PathParam("id") String id);
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createJob(
			@FormParam("dayOfMonth") String dayOfMonth,
			@FormParam("dayOfWeek") String dayOfWeek,
			@FormParam("description") String description,
			@FormParam("hours") String hours,
			@FormParam("jobTypeId") String jobTypeId,
			@FormParam("end") String end,
			@FormParam("minutes") String minutes,
			@FormParam("month") String month,
			@FormParam("notificationEmail") String notificationEmail,
			@FormParam("scheduleOption") String scheduleOption,
			@FormParam("seconds") String seconds,
			@FormParam("start") String start,
			@FormParam("timeZone") String timeZone,
			@FormParam("year") String year);
	
	@POST
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateJob(@PathParam("id") String id,
			@FormParam("dayOfMonth") String dayOfMonth,
			@FormParam("dayOfWeek") String dayOfWeek,
			@FormParam("description") String description,
			@FormParam("hours") String hours,
			@FormParam("jobName") String jobName,
			@FormParam("end") String end,
			@FormParam("minutes") String minutes,
			@FormParam("month") String month,
			@FormParam("notificationEmail") String notificationEmail,
			@FormParam("scheduleOption") String scheduleOption,
			@FormParam("seconds") String seconds,
			@FormParam("start") String start,
			@FormParam("timeZone") String timeZone,
			@FormParam("year") String year);
	
	@POST
	@Path("{id}/actions/{action}/invoke")
	@Produces(MediaType.APPLICATION_JSON)
	public Response invokeAction(@PathParam(value="id") String id, @PathParam(value="action") String action);
}