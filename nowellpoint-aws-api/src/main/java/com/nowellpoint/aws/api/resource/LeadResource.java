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

import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.model.Lead;
import com.nowellpoint.aws.model.data.EventStore;

@Path("/lead")
public class LeadResource {

	@Context
	private UriInfo uriInfo;
	
	private EventStore eventStore = new EventStore();

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Lead lead) {
		
		//
		//
		//
		
		lead.setId(new ObjectId().toString());
		
		//
		//
		//
				
		String payload = null;
		try {			
			payload = new ObjectMapper().writeValueAsString(lead);
		} catch (JsonProcessingException e) {
			throw new WebApplicationException(e);
		}
		
		//
		//
		//
		
		String organizationId = System.getenv("DEFAULT_ORGANIZATION_ID");
		String userId = System.getenv("DEFAULT_USER_ID");
		
		//
		//
		//
		
		eventStore.processEvent(Lead.class, organizationId, userId, payload);
		
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