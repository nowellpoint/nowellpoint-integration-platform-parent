package com.nowellpoint.api.resource;

import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.api.model.dto.PropertyDTO;
import com.nowellpoint.api.service.PropertyService;

@Path("/properties")
public class PropertyResource {
	
	@Inject
	private PropertyService propertyService;
	
	@Context
	private UriInfo uriInfo;
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed("System Administrator")
    public Response findAll() {
		
		Set<PropertyDTO> resources = propertyService.getProperties();
		
		return Response.ok(resources).build();
    }
}