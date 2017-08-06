package com.nowellpoint.api.rest;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
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
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegistration(@PathParam("id") String id);
	
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
    public Response createRegistration(
    		@FormParam("firstName") String firstName,
    		@FormParam("lastName") String lastName,
    		@FormParam("email") @Email String email,
    		@FormParam("countryCode") String countryCode,
    		@FormParam("domain") String domain,
    		@FormParam("planId") String planId);
	
	@PermitAll
	@POST
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
    public Response updateRegistration(
    		@PathParam("id") String id, 
    		@FormParam("domain") String domain,
    		@FormParam("planId") String planId);
	
	@PermitAll
	@POST
	@Path("{id}/password")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
    public Response setPassword(
    		@FormParam("password") String password, 
    		@FormParam("confirmPassword") String confirmPassword);
	
}