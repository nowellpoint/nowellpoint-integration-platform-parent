package com.nowellpoint.api.resource;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("health")
public class HealthCheckResource {
	
	@GET
	@Path("status")
	@PermitAll
	public Response checkHealth() {
		return Response.ok().build();
	}
}