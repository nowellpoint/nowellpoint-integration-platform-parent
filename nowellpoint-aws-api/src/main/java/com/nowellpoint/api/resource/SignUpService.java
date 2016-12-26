package com.nowellpoint.api.resource;

import static com.nowellpoint.util.Assert.isNull;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

import com.braintreegateway.CreditCardRequest;
import com.braintreegateway.CustomerRequest;
import com.nowellpoint.api.model.document.Address;
import com.nowellpoint.api.model.domain.AccountProfile;
import com.nowellpoint.api.model.domain.Error;
import com.nowellpoint.api.model.domain.idp.User;
import com.nowellpoint.api.service.AccountProfileService;
import com.nowellpoint.api.service.EmailService;
import com.nowellpoint.api.service.IdentityProviderService;
import com.nowellpoint.api.service.PaymentGatewayService;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountStatus;

@Path("/signup")
public class SignUpService {
	
	@Inject
	private EmailService emailService;
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@Inject
	private IdentityProviderService identityProviderService;
	
	@Inject
	private PaymentGatewayService paymentGatewayService;
	
	@Context
	private UriInfo uriInfo;

	@PermitAll
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
    public Response signUp(
    		@FormParam("firstName") String firstName,
    		@FormParam("lastName") @NotEmpty(message="Last Name must be filled in") String lastName,
    		@FormParam("email") @Email String email,
    		@FormParam("countryCode") @NotEmpty String countryCode,
    		@FormParam("password") @Length(min=8, max=100, message="Password must be between {min} and {max}") @Pattern.List({
    	        @Pattern(regexp = "(?=.*[0-9]).+", message = "Password must contain one digit."),
    	        @Pattern(regexp = "(?=.*[a-z]).+", message = "Password must contain one lowercase letter."),
    	        @Pattern(regexp = "(?=.*[a-z]).+", message = "Password must contain one upper letter."),
    	        @Pattern(regexp = "(?=.*[!@#$%^&*+=?-_()/\"\\.,<>~`;:]).+", message ="Password must contain one special character."),
    	        @Pattern(regexp = "(?=\\S+$).+", message = "Password must contain no whitespace.") }) String password,
    		@FormParam("confirmPassword") @NotEmpty(message="Confirmation Password must be filled in") String confirmPassword,
    		@FormParam("planId") @NotEmpty(message="No plan has been specified") String planId,
    		@FormParam("cardNumber") String cardNumber,
    		@FormParam("expirationMonth") String expirationMonth,
    		@FormParam("expirationYear") String expirationYear,
    		@FormParam("securityCode") String securityCode) {
		
		/**
		 * 
		 * 
		 * 
		 */
		
		if (! password.equals(confirmPassword)) {
			Error error = new Error(2000, "Password mismatch");
			ResponseBuilder builder = Response.status(Status.BAD_REQUEST);
			builder.entity(error);
			throw new WebApplicationException(builder.build());
		}
		
		/**
		 * 
		 * 
		 * 
		 * 
		 */
		
		if (identityProviderService.isEnabledAccount(email)) {
			Error error = new Error(1000, "Account for email is already enabled");
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
		
		Account user = identityProviderService.findByUsername(email);
			
		user.setGivenName(firstName);
		user.setMiddleName(null);
		user.setSurname(lastName);
		user.setEmail("administrator@nowellpoint.com");
		user.setUsername(email);
		user.setPassword(password);
		user.setStatus(AccountStatus.UNVERIFIED);
			
		if (user.getHref() == null) {
			identityProviderService.createAccount( user );
		} else {
			identityProviderService.updateAccount( user );
		}
		
		/**
		 * 
		 * 
		 * 
		 * 
		 */
		
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
		accountProfile.setHref(user.getHref());
			
		Address address = accountProfile.getAddress() != null ? accountProfile.getAddress() : new Address();
		address.setCountryCode(countryCode);
			
		accountProfile.setAddress(address);
			
		if (isNull(accountProfile.getId())) {			
			accountProfileService.createAccountProfile( accountProfile );
		} else {
			accountProfileService.updateAccountProfile( accountProfile );
		}
		
		/**
		 * 
		 * 
		 * 
		 * 
		 */
		
		CustomerRequest customerRequest = new CustomerRequest()
				.customerId( accountProfile.getId() )
				.email( email )
				.firstName( firstName )
				.lastName( lastName );
		
		//paymentGatewayService.addOrUpdateCustomer(customerRequest);
		
		CreditCardRequest creditCardRequest = new CreditCardRequest()
				.cardholderName( accountProfile.getName() )
				.expirationMonth( expirationMonth )
				.expirationYear( expirationYear )
				.number( cardNumber )
				.customerId( accountProfile.getId() )
				.billingAddress()
				.firstName( firstName )
				.lastName( lastName )
				.countryCodeAlpha2( countryCode )
				.done();
		
		paymentGatewayService.createCreditCard(creditCardRequest);
		
		/**
		 * 
		 * 
		 * 
		 * 
		 */
		
		String emailVerificationToken = user.getEmailVerificationToken().getHref().substring(user.getEmailVerificationToken().getHref().lastIndexOf("/") + 1);
		
		emailService.sendEmailVerificationMessage(user.getEmail(), user.getFullName(), emailVerificationToken);
		
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
		
		User user = new User();
		user.setHref(href);
		user.setUsername(username);
		user.setEmail(username);
		
		identityProviderService.updateUsername(href, username);
		
		emailService.sendWelcomeMessage(user);
		
		Optional<AccountProfile> query = Optional.ofNullable(accountProfileService.findAccountProfileByHref(href));
		
		if (! query.isPresent()) {
			Error error = new Error(1001, String.format("AccountProfile for href: %s was not found", href));
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