package com.nowellpoint.api.resource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nowellpoint.aws.data.CacheManager;

@Path("cache")
public class CacheResource {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed("System Administrator")
	public Response getCacheInfo() {
		
		String[] tokens = CacheManager.getCache().info().split("[\\r\\n]+");
		

		Map<String, HashMap<String,String>> info = new HashMap<String, HashMap<String,String>>();
		
		Arrays.asList(tokens).stream().forEach(t -> {
			if (t.startsWith("#")) {

			} else {
				//String[] field = t.split(":");

			}
		});
		
		return Response.ok(info).build();
	}

	@DELETE
    @Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed("System Administrator")
	public Response flushCache() {
		
		CacheManager.getCache().flushAll();
		
		return Response.noContent().build();
	}
}