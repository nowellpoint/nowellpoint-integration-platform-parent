package com.nowellpoint.aws.api.resource;

import java.net.URI;
import java.time.Instant;
import java.util.Date;

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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.model.DynamoDBMapperProvider;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.Lead;

@Path("/lead")
public class LeadResource {

	@Context
	private UriInfo uriInfo;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Lead lead) {
		
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
		
		Event event = new Event().withEventDate(Date.from(Instant.now()))
				.withEventStatus(Event.EventStatus.NEW)
				.withType(Lead.class.getName())
				.withOrganizationId(organizationId)
				.withUserId(userId)
				.withPayload(payload);
		
		DynamoDBMapperProvider.getDynamoDBMapper().save(event);
		
		//
		//
		//
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(LeadResource.class)
				.path("/{id}")
				.build(event.getId());
		
		//
		//
		//
		
		return Response.created(uri).build();
	}
}