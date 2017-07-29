package com.nowellpoint.api.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("organizations")
public interface OrganizationResource {

	@GET
	@Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getOrganization(@PathParam("id") String id);
}