package com.nowellpoint.api.resource;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.nowellpoint.aws.data.CacheManager;

@Path("cache")
public class CacheService {
	
	@Context 
	private SecurityContext securityContext;

	@DELETE
    @Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed("System Administrator")
	public Response flushCache() {
		CacheManager.getCache().flushAll();
		return Response.ok().build();
	}
	
	@GET
	@Path("{key}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getValue(@PathParam("key") String key) {
		String value = CacheManager.getCache().get(key);
		if (value == null) {
			throw new NotFoundException(String.format("Value for key: %s was not found", key));
		}
		return Response.ok(value).build();
	}
	
	@PUT
	@Path("{key}")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response putValue(@PathParam("key") String key, String value) {
		CacheManager.getCache().setex(key, 86400, value);
		return Response.ok().build();
	}
	
	@DELETE
	@Path("{key}")
	public Response removeValue(@PathParam("key") String key) {
		CacheManager.getCache().del(key);
		return Response.ok().build();
	}
}