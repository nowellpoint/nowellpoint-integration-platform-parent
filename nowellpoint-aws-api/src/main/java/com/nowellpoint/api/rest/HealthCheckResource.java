package com.nowellpoint.api.rest;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("health")
public interface HealthCheckResource {
	
	@GET
	@Path("status")
	@PermitAll
	public Response checkHealth();
	
	@GET
	@Path("database")
	@PermitAll
	public Response checkDatabase();

}