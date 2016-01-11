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

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.EventAction;
import com.nowellpoint.aws.model.EventBuilder;
import com.nowellpoint.aws.model.admin.Configuration;
import com.nowellpoint.aws.model.data.Identity;
import com.nowellpoint.aws.provider.ConfigurationProvider;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

@Path("/identity")
public class IdentityResource {

	@Context
	private UriInfo uriInfo;
	
	private DynamoDBMapper mapper;
	
	private Configuration configuration;
	
	public IdentityResource() {
		DynamoDBMapperProvider mapperProvider = new DynamoDBMapperProvider();
		mapper = mapperProvider.getDynamoDBMapper();
		configuration = ConfigurationProvider.getConfiguration();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signUp(Identity resource) {
		
		//
		//
		//
				
		Event event = null;
		try {			
			event = new EventBuilder().withAccountId(configuration.getDefaultAccountId())
					.withConfigurationId(configuration.getId())
					.withEventAction(EventAction.SIGN_UP)
					.withEventSource(uriInfo.getRequestUri())
					.withKmsKeyId(configuration.getKmsKeyId())
					.withOrganizationId(configuration.getDefaultOrganizationId())
					.withPayload(resource)
					.withType(Identity.class)
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
				.path(IdentityResource.class)
				.path("/{id}")
				.build(event.getId());
		
		//
		//
		//
		
		return Response.created(uri).build();
	}
}