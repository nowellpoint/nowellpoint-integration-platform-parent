package com.nowellpoint.api.rest.impl;

import javax.ws.rs.core.Response;

import com.nowellpoint.api.rest.HealthCheckResource;

public class HealthCheckResourceImpl implements HealthCheckResource {
	
	@Override
	public Response checkHealth() {
		return Response.ok().build();
	}
	
	@Override
	public Response checkDatabase() {
		return Response.ok().build();
	}
}