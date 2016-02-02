package com.nowellpoint.aws.api.resource;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.EventAction;
import com.nowellpoint.aws.model.EventBuilder;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.sforce.Lead;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

@Path("/lead")
public class LeadResource {

	@Context
	private UriInfo uriInfo;
	
	private DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    public Response submit(Lead resource) {
		
		//
		//
		//
				
		Event event = null;
		try {			
			event = new EventBuilder().withSubjectId(System.getProperty(Properties.DEFAULT_SUBJECT))
					.withEventAction(EventAction.ACTIVITY)
					.withEventSource(uriInfo.getRequestUri())
					.withPropertyStore(System.getenv("PROPERTY_STORE"))
					.withPayload(resource)
					.withType(Lead.class)
					.build();
		} catch (JsonProcessingException e) {
			throw new WebApplicationException(e);
		}
		
		//
		//
		//
		
		mapper.save(event);
		
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