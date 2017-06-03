package com.nowellpoint.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
	
	@GET
	@Path("{id}/job-executions/{fireInstanceId}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getJobExecution(@PathParam("id") String id, @PathParam("fireInstanceId") String fireInstanceId);
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	public Response getBySourceId(@QueryParam("sourceId") String sourceId);
	
	@GET
	@Path("{id}/download")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getOutputFile(@PathParam("id") String id, @QueryParam("filename") String filename);
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createJob(
			@FormParam("connectorId") String connectorId,
			@FormParam("jobTypeId") String jobTypeId,
			@FormParam("notificationEmail") String notificationEmail,
			@FormParam("slackWebhookUrl") String slackWebhookUrl,
			@FormParam("scheduleOption") String scheduleOption,
			@FormParam("runAt") String runAt,
			@FormParam("dayOfMonth") String dayOfMonth,
			@FormParam("dayOfWeek") String dayOfWeek,
			@FormParam("description") String description,
			@FormParam("hours") String hours,
			@FormParam("endAt") String endAt,
			@FormParam("minutes") String minutes,
			@FormParam("month") String month,
			@FormParam("seconds") String seconds,
			@FormParam("startAt") String startAt,
			@FormParam("timeZone") String timeZone,
			@FormParam("timeUnit") String timeUnit,
			@FormParam("timeInterval") String timeInterval,
			@FormParam("year") String year);
	
	@POST
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateJob(@PathParam("id") String id,
			@FormParam("dayOfMonth") String dayOfMonth,
			@FormParam("dayOfWeek") String dayOfWeek,
			@FormParam("description") String description,
			@FormParam("runAt") String runAt,
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