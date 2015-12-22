package com.nowellpoint.aws.api.resource;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.aws.model.Setup;
import com.nowellpoint.aws.model.data.EventStore;

@Path("/configuration")
public class ConfigurationResource {

	@Context
	private UriInfo uriInfo;
	
	private EventStore eventStore = new EventStore();

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Setup setup) {
		
		//
		//
		//
		
		eventStore.saveConfiguration(setup);
		
		//
		//
		//
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(ConfigurationResource.class)
				.path("/{id}")
				.build(setup.getId());
		
		//
		//
		//
		
		return Response.created(uri).build();
	}
}