package com.nowellpoint.api.resource;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.wildfly.swarm.monitor.Health;
import org.wildfly.swarm.monitor.HealthStatus;

@Path("health")
public class HealthCheckResource {
	
	@GET
	@Path("status")
	@PermitAll
	@Health(inheritSecurity = false)
	public HealthStatus checkHealth() {
		return HealthStatus.up();
	}
}