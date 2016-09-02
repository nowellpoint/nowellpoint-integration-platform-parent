package com.nowellpoint.aws.api.resource;

import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.nowellpoint.aws.api.dto.AccountProfileDTO;
import com.nowellpoint.aws.api.dto.ErrorDTO;
import com.nowellpoint.aws.api.model.AccountProfile;
import com.nowellpoint.aws.api.model.sforce.Lead;
import com.nowellpoint.aws.api.service.AccountProfileService;
import com.nowellpoint.aws.api.service.EmailService;
import com.nowellpoint.aws.api.service.IdentityProviderService;
import com.nowellpoint.aws.api.tasks.AccountProfileSetupRequest;
import com.nowellpoint.aws.api.tasks.AccountProfileSetupTask;
import com.nowellpoint.aws.api.tasks.AccountSetupRequest;
import com.nowellpoint.aws.api.tasks.AccountSetupTask;
import com.nowellpoint.aws.api.tasks.SubmitLeadRequest;
import com.nowellpoint.aws.api.tasks.SubmitLeadTask;
import com.nowellpoint.aws.data.mongodb.MongoDatastore;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.model.admin.Properties;

@Path("/signup")
public class SignUpService {
	
	@Inject
	private EmailService emailService;
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@Inject
	private IdentityProviderService identityProviderService;
	
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
		
		Account account = identityProviderService.findAccountByUsername(email);
		
		if (account != null && "ENABLED".equals(account.getStatus())) {
			ErrorDTO error = new ErrorDTO(1000, "Account for email is already enabled");
			ResponseBuilder builder = Response.status(Status.CONFLICT);
			builder.entity(error);
			throw new WebApplicationException(builder.build());
		}
		
		AccountSetupRequest accountSetupRequest = new AccountSetupRequest()
				.withEmail("administrator@nowellpoint.com")
				.withGivenName(firstName)
				.withHref(account != null ? account.getHref() : null)
				.withSurname(lastName)
				.withMiddleName(null)
				.withPassword(password)
				.withUsername(email);
		
		AccountProfileSetupRequest accountProfileSetupRequest = new AccountProfileSetupRequest()
				.withEmail(email)
				.withFirstName(firstName)
				.withIsActive(Boolean.TRUE)
				.withLastName(lastName)
				.withUsername(email)
				.withHref(System.getProperty(Properties.DEFAULT_SUBJECT))
				.withCountryCode(countryCode);
		
		SubmitLeadRequest submitLeadRequest = new SubmitLeadRequest()
				.withCountryCode(countryCode)
				.withEmail(email)
				.withFirstName(firstName)
				.withLastName(lastName)
				.withLeadSource(leadSource);
		
		ExecutorService executor = Executors.newFixedThreadPool(3);
		
		Future<Account> accountSetupTask = executor.submit(new AccountSetupTask(accountSetupRequest));
		Future<AccountProfile> accountProfileSetupTask = executor.submit(new AccountProfileSetupTask(accountProfileSetupRequest));
		Future<Lead> submitLeadTask = executor.submit(new SubmitLeadTask(submitLeadRequest));
		
		executor.shutdown();
		
		try {
			executor.awaitTermination(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new WebApplicationException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
		}
		
		AccountProfile accountProfile = null; 
		
		try {
			
			account = accountSetupTask.get();
			
			accountProfile = accountProfileSetupTask.get();
			accountProfile.setCreatedById(account.getHref());
			accountProfile.setLastModifiedById(account.getHref());
			accountProfile.setLastModifiedDate(Date.from(Instant.now()));
			accountProfile.setLeadId(submitLeadTask.get().getId());
			accountProfile.setHref(account.getHref());
			
			MongoDatastore.replaceOne( accountProfile );
			
		} catch (InterruptedException | ExecutionException e) {
			throw new WebApplicationException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
		}
		
		String emailVerificationToken = account.getEmailVerificationToken().getHref().substring(account.getEmailVerificationToken().getHref().lastIndexOf("/") + 1);
		
		emailService.sendEmailVerificationMessage(account, emailVerificationToken);
		
		URI emailVerificationTokenUri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(SignUpService.class)
				.path("verify-email")
				.path("{emailVerificationToken}")
				.build(emailVerificationToken);
		
		URI resourceUri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(AccountProfileResource.class)
				.path("/{id}")
				.build(accountProfile.getId().toString());
		
		Map<String,Object> response = new HashMap<String,Object>();
		response.put("href", resourceUri);
		response.put("emailVerificationToken", emailVerificationTokenUri);
		
		return Response.ok(response)
				.build();
	}
	
	@PermitAll
	@POST
	@Path("verify-email/{emailVerificationToken}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response verifyEmail(@PathParam("emailVerificationToken") String emailVerificationToken) {
		
		String href = identityProviderService.verifyEmail(emailVerificationToken);
		
		String username = identityProviderService.getAccountByHref(href).getUsername();
		
		Account account = new Account();
		account.setHref(href);
		account.setUsername(username);
		account.setEmail(username);
		
		identityProviderService.updateAccount(account);
		
		emailService.sendWelcomeMessage(account);
		
		Optional<AccountProfileDTO> query = Optional.ofNullable(accountProfileService.findAccountProfileBySubject(href));
		
		if (! query.isPresent()) {
			ErrorDTO error = new ErrorDTO(1001, String.format("AccountProfile for href: %s was not found", href));
			ResponseBuilder builder = Response.status(Status.NOT_FOUND);
			builder.entity(error);
			throw new WebApplicationException(builder.build());
		}
		
		AccountProfileDTO resource = query.get();
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(AccountProfileResource.class)
				.path("/{id}")
				.build(resource.getId());
		
		Map<String,Object> response = new HashMap<String,Object>();
		response.put("href", uri);
		
		return Response.ok(response)
				.build();
	}
}