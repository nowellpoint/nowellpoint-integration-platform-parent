package com.nowellpoint.api.rest;

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
	@Path("{id}/actions/{action}/invoke")
	@Produces(MediaType.APPLICATION_JSON)
	public Response invokeAction(@PathParam(value="id") String id, @PathParam(value="action") String action);
}