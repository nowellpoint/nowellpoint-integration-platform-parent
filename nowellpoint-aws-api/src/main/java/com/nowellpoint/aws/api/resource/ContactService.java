package com.nowellpoint.aws.api.resource;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.nowellpoint.aws.api.model.sforce.Lead;
import com.nowellpoint.aws.api.tasks.SubmitLeadTask;

@Path("/contact")
public class ContactService {

	@Context
	private UriInfo uriInfo;

	@PermitAll
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response contact(
			@FormParam("leadSource") @NotEmpty(message = "Lead Source must be filled in") String leadSource,
    		@FormParam("firstName") String firstName,
    		@FormParam("lastName") @NotEmpty(message="Last Name must be filled in") String lastName,
    		@FormParam("email") @Email @NotEmpty(message="Email must be filled in") String email,
    		@FormParam("phone") String phone,
    		@FormParam("company") String company,
    		@FormParam("description") String description) {
		
		Lead lead = new Lead();
		lead.setLeadSource(leadSource);
		lead.setFirstName(firstName);
		lead.setLastName(lastName);
		lead.setEmail(email);
		lead.setPhone(phone);
		lead.setCompany(company);
		lead.setDescription(description);
		lead.setCountryCode("US");
		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		
		Future<Lead> submitLeadTask = executor.submit(new SubmitLeadTask(lead));
		executor.shutdown();
		try {
			executor.awaitTermination(30, TimeUnit.SECONDS);
			lead = submitLeadTask.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new WebApplicationException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
		}
		
		Map<String,String> response = new HashMap<String,String>();
		response.put("leadId", lead.getId());
		
		return Response.ok(response).build();
	}
}