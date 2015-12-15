package com.nowellpoint.aws.api.resource;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nowellpoint.aws.model.Lead;

@Path("/lead")
public class LeadResource {

	@Context
	private UriInfo uriInfo;
	

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response capture(Lead lead) {
		
		//
		//
		//
		
		try {
			lead.save();
		} catch (JsonProcessingException e) {
			throw new WebApplicationException(e);
		}
		
		//
		//
		//
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(LeadResource.class)
				.path("/{id}")
				.build(lead.getId());
		
		//
		//
		//
		
		return Response.created(uri).build();
	}
}