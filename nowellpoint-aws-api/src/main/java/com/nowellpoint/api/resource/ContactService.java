package com.nowellpoint.api.resource;

import java.util.List;

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
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.nowellpoint.api.model.sforce.Lead;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.util.Properties;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.CreateResult;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.UsernamePasswordGrantRequest;
import com.nowellpoint.client.sforce.model.Error;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Token;

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
		lead.setDescription(description);
		lead.setCompany(company);
		lead.setPhone(phone);
		
		UsernamePasswordGrantRequest request = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setClientId(System.getProperty(Properties.SALESFORCE_CLIENT_ID))
				.setClientSecret(System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
				.setUsername(System.getProperty(Properties.SALESFORCE_USERNAME))
				.setPassword(System.getProperty(Properties.SALESFORCE_PASSWORD))
				.setSecurityToken(System.getProperty(Properties.SALESFORCE_SECURITY_TOKEN))
				.build();
		
		OauthAuthenticationResponse response = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		Token token = response.getToken();
		
		Identity identity = response.getIdentity();
		
		HttpResponse httpResponse = RestResource.post(identity.getUrls().getSobjects())
				.contentType(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
    			.path("Lead")
    			.body(lead)
    			.execute();
		
		if (httpResponse.getStatusCode() == Status.CREATED.getStatusCode()) {
			CreateResult createResult = httpResponse.getEntity(CreateResult.class);
			if (createResult.getSuccess()) {
				return Response.ok(createResult).build();
			} else {
				return Response.status(Status.BAD_REQUEST).entity(createResult.getErrors()).build();
			}
		} else {
			List<Error> errors = httpResponse.getEntityList(Error.class);
			ResponseBuilder builder = Response.status(httpResponse.getStatusCode());
			builder.entity(errors);
			throw new WebApplicationException(builder.build());
		}
	}
}