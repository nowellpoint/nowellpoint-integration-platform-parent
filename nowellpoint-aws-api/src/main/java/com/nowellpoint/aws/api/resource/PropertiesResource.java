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

import org.joda.time.Instant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.model.DynamoDBMapperProvider;
import com.nowellpoint.aws.model.config.Configuration;
import com.nowellpoint.aws.model.config.Properties;

@Path("/properties")
public class PropertiesResource {

	@Context
	private UriInfo uriInfo;
	
	private ObjectMapper objectMapper = new ObjectMapper();

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Properties resource) {
		
		//
		//
		//
		
		String payload = null;
		try {
			payload = objectMapper.writeValueAsString(resource);
		} catch (JsonProcessingException e) {
			throw new WebApplicationException(e);
		}
		
		//
		//
		//
		
		Configuration configuration = new Configuration().withCreatedDate(Instant.now().toDate())
				.withLastModifiedDate(Instant.now().toDate())
				.withPayload(payload);
		
		//
		//
		//
		
		DynamoDBMapperProvider.getDynamoDBMapper().save(configuration);
		
		//
		//
		//
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(PropertiesResource.class)
				.path("/{id}")
				.build(configuration.getId());
		
		//
		//
		//
		
		return Response.created(uri).build();
	}
}