package com.nowellpoint.aws.api.resource;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.validation.constraints.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.api.dto.AccountProfileDTO;
import com.nowellpoint.aws.api.dto.ErrorDTO;
import com.nowellpoint.aws.api.model.AccountProfile;
import com.nowellpoint.aws.api.model.Address;
import com.nowellpoint.aws.api.model.sforce.Lead;
import com.nowellpoint.aws.api.service.AccountProfileService;
import com.nowellpoint.aws.api.service.EmailService;
import com.nowellpoint.aws.api.tasks.AccountProfileSetupTask;
import com.nowellpoint.aws.api.tasks.AccountSetupTask;
import com.nowellpoint.aws.api.tasks.SubmitLeadTask;
import com.nowellpoint.aws.data.MongoDBDatastore;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.model.admin.Properties;

@Path("/signup")
public class SignUpService {
	
	private static final Logger LOGGER = Logger.getLogger(SignUpService.class);
	
	@Inject
	private EmailService emailService;
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@Context
	private UriInfo uriInfo;

	@PermitAll
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
    public Response signUp(
    		@FormParam("leadSource") @NotEmpty(message = "Lead Source must be filled in") String leadSource,
    		@FormParam("firstName") String firstName,
    		@FormParam("lastName") @NotEmpty(message="Last Name must be filled in") String lastName,
    		@FormParam("email") @Email String email,
    		@FormParam("countryCode") @NotEmpty String countryCode,
    		@FormParam("password") @Length(min=8, max=100, message="Password must be between {min} and {max}") @Pattern.List({
    	        @Pattern(regexp = "(?=.*[0-9]).+", message = "Password must contain one digit."),
    	        @Pattern(regexp = "(?=.*[a-z]).+", message = "Password must contain one lowercase letter."),
    	        @Pattern(regexp = "(?=.*[a-z]).+", message = "Password must contain one upper letter."),
    	        @Pattern(regexp = "(?=.*[!@#$%^&*+=?-_()/\"\\.,<>~`;:]).+", message ="Password must contain one special character."),
    	        @Pattern(regexp = "(?=\\S+$).+", message = "Password must contain no whitespace.") }) String password) {
		
		Lead lead = new Lead();
		lead.setLeadSource(leadSource);
		lead.setFirstName(firstName);
		lead.setLastName(lastName);
		lead.setEmail(email);
		lead.setCountryCode(countryCode);
		
		Account account = new Account();
		account.setGivenName(firstName);
		account.setMiddleName(null);
		account.setSurname(lastName);
		account.setEmail("administrator@nowellpoint.com");
		account.setUsername(email);
		account.setPassword(password);
		account.setStatus("UNVERIFIED");
		
		AccountProfileDTO accountProfile = new AccountProfileDTO();
		accountProfile.setCreatedById(System.getProperty(Properties.DEFAULT_SUBJECT));
		accountProfile.setLastModifiedById(System.getProperty(Properties.DEFAULT_SUBJECT));
		accountProfile.setFirstName(firstName);
		accountProfile.setLastName(lastName);
		accountProfile.setEmail(email);
		accountProfile.setUsername(email);
		accountProfile.setIsActive(Boolean.TRUE);
		
		Address address = new Address();
		address.setCountryCode(countryCode);
		
		accountProfile.setAddress(address);
		
		ExecutorService executor = Executors.newFixedThreadPool(3);
		
		Future<Account> accountSetupTask = executor.submit(new AccountSetupTask(account));
		Future<AccountProfileDTO> accountProfileTask = executor.submit(new AccountProfileSetupTask(accountProfile));
		Future<Lead> submitLeadTask = executor.submit(new SubmitLeadTask(lead));
		
		executor.shutdown();
		
		try {
			executor.awaitTermination(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new WebApplicationException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
		}
		
		try {
			
			URI emailVerificationToken = UriBuilder.fromUri(uriInfo.getBaseUri())
					.path(SignUpService.class)
					.path("verify-email")
					.path("{token}")
					.build(accountSetupTask.get().getEmailVerificationToken().getHref().substring(accountSetupTask.get().getEmailVerificationToken().getHref().lastIndexOf("/") + 1));
			
			//accountProfile.setLeadId(submitLeadTask.get().getId());
			accountProfile.setHref(accountSetupTask.get().getHref());
			System.out.println("1");
			//accountProfile.setId(accountProfileTask.get().getId());
			System.out.println("2");
			accountProfile.setEmailVerificationToken(emailVerificationToken.toString());
			System.out.println("3");
			emailService.sendEmailVerification(accountProfileTask.get());
		} catch (InterruptedException | ExecutionException e) {
			throw new WebApplicationException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
		}
		
		accountProfileService.updateAccountProfile(accountProfile);
		
		//MongoDBDatastore.replaceOne(accountProfile);	
		
		Map<String,String> response = new HashMap<String,String>();
		response.put("id", accountProfile.getId().toString());
		//response.put("leadId", accountProfile.getLeadId());
		response.put("href", accountProfile.getHref());
		response.put("emailVerificationToken", accountProfile.getEmailVerificationToken());
		
		return Response.ok(accountProfile).build();
	}
	
	@PermitAll
	@GET
	@Path("verify-email/{token}")
	@Produces(MediaType.TEXT_HTML)
	public Response verifyEmail(@PathParam("token") String token) {
		
		String apiEndpoint = System.getProperty(Properties.STORMPATH_API_ENDPOINT);
		String apiKeyId = System.getProperty(Properties.STORMPATH_API_KEY_ID);
		String apiKeySecret = System.getProperty(Properties.STORMPATH_API_KEY_SECRET);
		
		HttpResponse httpResponse = RestResource.post(apiEndpoint)
				.basicAuthorization(apiKeyId, apiKeySecret)
				.path("accounts")
				.path("emailVerificationTokens")
				.path(token)
				.execute();
		
		ObjectNode response = httpResponse.getEntity(ObjectNode.class);

		if (httpResponse.getStatusCode() != Status.OK.getStatusCode()) {
			LOGGER.error(response.toString());
			ErrorDTO error = new ErrorDTO(response.get("code").asInt(), response.get("developerMessage").asText());
			ResponseBuilder builder = Response.status(httpResponse.getStatusCode());
			builder.entity(error);
			return builder.build();
		}
		
		AccountProfileDTO accountProfile = accountProfileService.findAccountProfileBySubject(response.get("href").asText());
		
		httpResponse = RestResource.get(response.get("href").asText())
				.basicAuthorization(apiKeyId, apiKeySecret)
				.accept(MediaType.APPLICATION_JSON)
				.execute();
		
		Account account = httpResponse.getEntity(Account.class);
		account.setEmail(account.getUsername());
		
		httpResponse = RestResource.post(account.getHref())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.basicAuthorization(apiKeyId, apiKeySecret)
				.body(account)
				.execute();
		
		emailService.sendWelcome(accountProfile);
		
		String html = "<html>Success</html>";
		
		return Response.ok(html).build();
	}
}