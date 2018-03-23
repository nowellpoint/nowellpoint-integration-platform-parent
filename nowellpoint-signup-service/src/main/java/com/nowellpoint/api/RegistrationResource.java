package com.nowellpoint.api;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.model.RegistrationRequest;

@Path("/registrations")
public interface RegistrationResource {
	
	@PermitAll
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRegistration(@PathParam("id") String id);
	
	@PermitAll
	@POST
	@Path("verify-email/{emailVerificationToken}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response verifyEmail(@PathParam("emailVerificationToken") String emailVerificationToken);
	
	@PermitAll
	@POST
	@Path("{id}/verify-email")
	public Response resendVerificationEmail(@PathParam("id") String id);
	
	@PermitAll
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public Response createRegistration(RegistrationRequest request);
	
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
	@Path("{id}/provision")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
    public Response provision(
    		@PathParam("id") String id, 
    		@FormParam("cardholderName") String cardholderName,
			@FormParam("expirationMonth") String expirationMonth,
			@FormParam("expirationYear") String expirationYear,
			@FormParam("cardNumber") String cardNumber,
			@FormParam("cvv") String cvv);
	
	@PermitAll
	@POST
	@Path("{id}/password")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
    public Response setPassword(
    		@FormParam("password") String password, 
    		@FormParam("confirmPassword") String confirmPassword);
	
	@PermitAll
	@DELETE
	@Path("{id}")
    public Response deleteRegistration(@PathParam("id") String id);
	
}