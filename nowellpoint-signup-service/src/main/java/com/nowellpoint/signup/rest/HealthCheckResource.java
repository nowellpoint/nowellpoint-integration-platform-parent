package com.nowellpoint.signup.rest;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.wildfly.swarm.health.Health;
import org.wildfly.swarm.health.HealthStatus;

@Path("health")
public interface HealthCheckResource {
	
	@GET
	@Path("status")
	@PermitAll
	@Health
	public HealthStatus status();

}