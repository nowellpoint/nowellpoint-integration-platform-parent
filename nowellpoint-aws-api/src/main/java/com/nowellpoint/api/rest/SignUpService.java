package com.nowellpoint.api.rest;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.validator.constraints.Email;

@Path("/signup")
public interface SignUpService {
	
	@PermitAll
	@POST
	@Path("verify-email/{emailVerificationToken}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response verifyEmail(@PathParam("emailVerificationToken") String emailVerificationToken);
	
	@PermitAll
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
    public Response signUp(
    		@FormParam("firstName") String firstName,
    		@FormParam("lastName") String lastName,
    		@FormParam("email") @Email String email,
    		@FormParam("countryCode") String countryCode,
    		@FormParam("planId") String planId);
	
	@PermitAll
	@POST
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
    public Response addSite(@PathParam("id") String id, @FormParam("siteName") String siteName);
	
	@PermitAll
	@POST
	@Path("{id}/password")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
    public Response setPassword(
    		@FormParam("password") String password, 
    		@FormParam("confirmPassword") String confirmPassword);
	
}