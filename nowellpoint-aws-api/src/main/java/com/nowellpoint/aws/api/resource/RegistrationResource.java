package com.nowellpoint.aws.api.resource;

import static com.nowellpoint.aws.api.data.EventHandler.persist;

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

import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nowellpoint.aws.model.Registration;

@Path("/registration")
public class RegistrationResource {
	
	@Context
	private UriInfo uriInfo;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(Registration registration) {
		
		//
		//
		//
		
		registration.setId(new ObjectId().toString());
		
		//
		//
		//
		
		try {
			persist(registration);
		} catch (JsonProcessingException e) {
			throw new WebApplicationException(e);
		}
		
		//
		//
		//
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(RegistrationResource.class)
				.path("/{id}")
				.build(registration.getId());
		
		//
		//
		//
		
		return Response.created(uri).build();
    }
}