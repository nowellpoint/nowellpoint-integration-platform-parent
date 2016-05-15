package com.nowellpoint.aws.api.resource;

import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.aws.api.dto.PropertyDTO;
import com.nowellpoint.aws.api.service.PropertyService;

@Path("/properties")
public class PropertyResource {
	
	@Inject
	private PropertyService propertyService;
	
	@Context
	private UriInfo uriInfo;
	
	@Context
	private SecurityContext securityContext;
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
		String subject = securityContext.getUserPrincipal().getName();
		
		Set<PropertyDTO> resources = propertyService.getProperties();
		
		return Response.ok(resources).build();
    }

}
