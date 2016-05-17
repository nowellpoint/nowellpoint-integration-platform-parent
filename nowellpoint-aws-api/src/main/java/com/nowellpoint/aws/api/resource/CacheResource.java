package com.nowellpoint.aws.api.resource;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nowellpoint.aws.data.CacheManager;

@Path("cache")
public class CacheResource {

	@DELETE
    @Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed("System Administrator")
	public Response flushCache() {
		
		CacheManager.getCache().flushAll();
		
		return Response.noContent().build();
	}
}