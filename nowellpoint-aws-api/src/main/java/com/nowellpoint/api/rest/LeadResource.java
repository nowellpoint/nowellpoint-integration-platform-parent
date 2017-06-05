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
import org.hibernate.validator.constraints.NotEmpty;

@Path("/leads")
public interface LeadResource {
	
	@GET
	@Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLead(@PathParam("id") String id);

	@PermitAll
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createLead(
			@FormParam("leadSource") String leadSource,
    		@FormParam("firstName") String firstName,
    		@FormParam("lastName") @NotEmpty(message="Last Name must be filled in") String lastName,
    		@FormParam("email") @Email @NotEmpty(message="Email must be filled in") String email,
    		@FormParam("phone") String phone,
    		@FormParam("company") String company,
    		@FormParam("message") String message);
}