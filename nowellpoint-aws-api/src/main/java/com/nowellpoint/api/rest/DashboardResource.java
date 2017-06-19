package com.nowellpoint.api.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("dashboards")
public interface DashboardResource {
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDashboard();

}