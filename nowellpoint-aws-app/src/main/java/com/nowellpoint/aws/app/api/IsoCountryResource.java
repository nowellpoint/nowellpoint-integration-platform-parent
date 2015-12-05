package com.nowellpoint.aws.app.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/iso-country")
public class IsoCountryResource {
	
	@GET
	@Path("/isoCode")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getByIsoCode(@PathParam("isoCode") String isoCode) {
		
		return null;
	}
}