package com.nowellpoint.api.resource;

import static com.nowellpoint.util.Assert.isNull;

import java.net.URI;
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
import javax.ws.rs.InternalServerErrorException;
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
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.api.model.document.Address;
import com.nowellpoint.api.model.dto.AccountProfile;
import com.nowellpoint.api.model.dto.ErrorDTO;
import com.nowellpoint.api.model.sforce.Lead;
import com.nowellpoint.api.service.AccountProfileService;
import com.nowellpoint.api.service.EmailService;
import com.nowellpoint.api.service.IdentityProviderService;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.api.dto.idp.Account;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.UsernamePasswordGrantRequest;
import com.nowellpoint.client.sforce.model.Error;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;

@Path("/signup")
public class SignUpService {
	
	private static final Logger LOGGER = Logger.getLogger(SignUpService.class);
	
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
		
		/**
		 * 
		 * 
		 * 
		 * 
		 */
		
		if (identityProviderService.isEnabledAccount(email)) {
			ErrorDTO error = new ErrorDTO(1000, "Account for email is already enabled");
			ResponseBuilder builder = Response.status(Status.CONFLICT);
			builder.entity(error);
			throw new WebApplicationException(builder.build());
		}
		
		/**
		 * 
		 * 
		 * 
		 * 
		 */
		
		ExecutorService executor = Executors.newFixedThreadPool(3);
		
		/**
		 * 
		 * 
		 * 
		 * 
		 */
		
		Future<Account> accountSetupTask = executor.submit(() -> {
			String directoryId = System.getProperty(Properties.STORMPATH_DIRECTORY_ID);
			String apiEndpoint = System.getProperty(Properties.STORMPATH_API_ENDPOINT);
			String apiKeyId = System.getProperty(Properties.STORMPATH_API_KEY_ID);
			String apiKeySecret = System.getProperty(Properties.STORMPATH_API_KEY_SECRET);
			
			Account account = identityProviderService.findAccountByUsername(email);
			
			if (account == null) {
				account = new Account();
			}
			
			account = new Account();
			account.setGivenName(firstName);
			account.setMiddleName(null);
			account.setSurname(lastName);
			account.setEmail("administrator@nowellpoint.com");
			account.setUsername(email);
			account.setPassword(password);
			account.setStatus("UNVERIFIED");
			
			if (account.getHref() == null) {
				
				HttpResponse httpResponse = RestResource.post(apiEndpoint)
						.contentType(MediaType.APPLICATION_JSON)
						.path("directories")
						.path(directoryId)
						.path("accounts")
						.basicAuthorization(apiKeyId, apiKeySecret)
						.body(account)
						.execute();
				
				LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());	
							
				if (httpResponse.getStatusCode() != 201) {
					ObjectNode node = httpResponse.getEntity(ObjectNode.class);
					LOGGER.error(node.toString());
					throw new Exception(node.toString());
				}
				
				account = httpResponse.getEntity(Account.class);
				
			} else {
				
				HttpResponse httpResponse = RestResource.post(account.getHref())
						.contentType(MediaType.APPLICATION_JSON)
						.basicAuthorization(apiKeyId, apiKeySecret)
						.body(account)
						.execute();
				
				LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());	
								
				if (httpResponse.getStatusCode() != 200) {
					ObjectNode node = httpResponse.getEntity(ObjectNode.class);
					LOGGER.error(node.toString());
					throw new Exception(node.toString());
				}
				
				account = httpResponse.getEntity(Account.class);
			}
			
			return account;
		});
		
		/**
		 * 
		 * 
		 * 
		 * 
		 */
		
		Future<AccountProfile> accountProfileSetupTask = executor.submit(() -> {
			AccountProfile accountProfile = null;
			
			try {
				accountProfile = accountProfileService.findAccountProfileByUsername(email);
			} catch (DocumentNotFoundException e) {
				accountProfile = new AccountProfile();
			}
			
			accountProfile.setFirstName(firstName);
			accountProfile.setLastName(lastName);
			accountProfile.setEmail(email);
			accountProfile.setUsername(email);
			accountProfile.setIsActive(Boolean.TRUE);
			
			Address address = accountProfile.getAddress() != null ? accountProfile.getAddress() : new Address();
			address.setCountryCode(countryCode);
			
			accountProfile.setAddress(address);
			
			if (isNull(accountProfile.getId())) {			
				accountProfileService.createAccountProfile( accountProfile );
			} else {
				accountProfileService.updateAccountProfile( accountProfile );
			}
			
			return accountProfile;
		});
		
		/**
		 * 
		 * 
		 * 
		 * 
		 */
		
		Future<Lead> submitLeadTask = executor.submit(() -> {
			
			Lead lead = new Lead();
			lead.setLeadSource(leadSource);
			lead.setFirstName(firstName);
			lead.setLastName(lastName);
			lead.setEmail(email);
			lead.setCountryCode(countryCode);

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
				
			HttpResponse httpResponse = RestResource.post(token.getInstanceUrl())
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.path("services/apexrest/nowellpoint/lead")
					.bearerAuthorization(token.getAccessToken())
					.body(lead)
					.execute();
			
			LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Target: " + httpResponse.getURL());	
			
			if (httpResponse.getStatusCode() != 200 && httpResponse.getStatusCode() != 201) {
				Error error = httpResponse.getEntity(Error.class);
				throw new Exception(error.getErrorDescription());
			}
			
			String leadId = httpResponse.getAsString();
			
			lead.setId(leadId);
			
			return lead;
		});
		
		/**
		 * 
		 * 
		 * 
		 * 
		 */
		
		executor.shutdown();
		
		try {
			executor.awaitTermination(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new InternalServerErrorException(e.getMessage());
		}
		
		/**
		 * 
		 * 
		 * 
		 * 
		 */
		
		AccountProfile accountProfile = null; 
		Account account = null;
		Lead lead = null;
		
		try {
			
			accountProfile = accountProfileSetupTask.get();
			account = accountSetupTask.get();
			lead = submitLeadTask.get();
			
			accountProfile.setLeadId(lead.getId());
			accountProfile.setHref(account.getHref());
			
			accountProfileService.updateAccountProfile( accountProfile );
			
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			throw new InternalServerErrorException(e.getMessage());
		}
		
		/**
		 * 
		 * 
		 * 
		 * 
		 */
		
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
				.build(accountProfile.getId());
		
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
		
		Optional<AccountProfile> query = Optional.ofNullable(accountProfileService.findAccountProfileByHref(href));
		
		if (! query.isPresent()) {
			ErrorDTO error = new ErrorDTO(1001, String.format("AccountProfile for href: %s was not found", href));
			ResponseBuilder builder = Response.status(Status.NOT_FOUND);
			builder.entity(error);
			throw new WebApplicationException(builder.build());
		}
		
		AccountProfile resource = query.get();
		
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