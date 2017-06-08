package com.nowellpoint.api.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("job-operator")
public interface JobOperatorResource {
	
	@POST
	@Path("{action}/invoke")
    @Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed("System Administrator")
	public Response invokeAction(@PathParam(value = "action") String action);
}