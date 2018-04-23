package com.nowellpoint.console.api;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("oauth")
public interface TokenResource {
	
	@POST
	@PermitAll
	@Path("token")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response authenticate(@HeaderParam("Authorization") String authorization, @FormParam("grant_type") String grantType);
	
	@DELETE
	@Path("token")
	public Response revokeToken(@HeaderParam("Authorization") String authorization);
}