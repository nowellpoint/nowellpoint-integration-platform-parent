package com.nowellpoint.api.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.annotations.Api;

@Path("identity")
@Api(value = "/identity")
public interface IdentityResource {
	
	@GET
	@Path("{organizationId}/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIdentity(@PathParam("organizationId") String organizationId, @PathParam("userId") String userId);
}