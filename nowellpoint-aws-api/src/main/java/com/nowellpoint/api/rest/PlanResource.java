package com.nowellpoint.api.rest;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("plans")
public interface PlanResource {
	
	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllActive(@QueryParam(value="locale") String locale, @QueryParam(value="language") String language);
	
	@GET
	@PermitAll
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findById(@PathParam(value="id") String id);
}