package com.nowellpoint.api.resource;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.nowellpoint.mongodb.document.MongoDatastore;

@Path("health")
public class HealthCheckResource {
	
	@GET
	@Path("status")
	@PermitAll
	public Response checkHealth() {
		return Response.ok().build();
	}
	
	@GET
	@Path("database")
	@PermitAll
	public Response checkDatabase() {
		return Response.ok(MongoDatastore.checkStatus().toJson()).build();
	}
}