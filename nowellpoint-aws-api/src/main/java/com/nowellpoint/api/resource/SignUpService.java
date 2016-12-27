package com.nowellpoint.api.resource;

import static com.nowellpoint.util.Assert.isNull;
import static com.nowellpoint.util.Assert.isNotNull;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
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

import com.braintreegateway.AddressRequest;
import com.braintreegateway.CreditCardRequest;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Result;
import com.braintreegateway.SubscriptionRequest;
import com.braintreegateway.exceptions.NotFoundException;
import com.nowellpoint.api.model.document.Address;
import com.nowellpoint.api.model.domain.AccountProfile;
import com.nowellpoint.api.model.domain.CreditCard;
import com.nowellpoint.api.model.domain.Error;
import com.nowellpoint.api.model.domain.Plan;
import com.nowellpoint.api.model.domain.Subscription;
import com.nowellpoint.api.service.AccountProfileService;
import com.nowellpoint.api.service.EmailService;
import com.nowellpoint.api.service.IdentityProviderService;
import com.nowellpoint.api.service.PaymentGatewayService;
import com.nowellpoint.api.service.PlanService;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountStatus;

@Path("/signup")
public class SignUpService {
	
	private static final Logger LOGGER = Logger.getLogger(SignUpService.class);
	
	@Inject
	private EmailService emailService;
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@Inject
	private IdentityProviderService identityProviderService;
	
	@Inject
	private PaymentGatewayService paymentGatewayService;
	
	@Inject
	private PlanService planService;
	
	@Context
	private UriInfo uriInfo;
	
	/**
	 * Sign up steps:
	 * 
	 * 1. check that password and confirm password match (Error error = new Error(2000, "Password mismatch")
	 * 2. lookup account in identity provider by username 
	 * 3. if account exists and is is enabled (1000, "Account for email is already enabled")
	 * 4. create or update account in identity provided
	 * 5. create or update account profile local
	 * 6. lookup plan with planId local
	 * 7. add or update customer in payment gateway
	 * 8. add or update credit card in payment gateway with billing address
	 * 9. add or update subscription based on plan
	 * 10. udpate account profile with credit card and subscription info
	 * 11. build and return email verification token and location to account profile
	 * 
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @param countryCode
	 * @param password
	 * @param confirmPassword
	 * @param planId
	 * @param cardNumber
	 * @param expirationMonth
	 * @param expirationYear
	 * @param securityCode
	 * @return the response with verification token and url link to account profile
	 * 
	 */

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
    		@FormParam("cardNumber") @NotEmpty(message="Credit Card number must be provided") String cardNumber,
    		@FormParam("expirationMonth") @NotEmpty(message="Credit card is missing expiration month") String expirationMonth,
    		@FormParam("expirationYear") @NotEmpty(message="Credit card is missing expiration year") String expirationYear,
    		@FormParam("securityCode") @NotEmpty(message="Credit card is mssing security code") String securityCode) {
		
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
		
		Account account = identityProviderService.findByUsername(email);
		
		if (isNotNull(account.getStatus()) && account.getStatus().equals(AccountStatus.ENABLED)) {
			Error error = new Error(1000, "Account for email is already enabled");
			ResponseBuilder builder = Response.status(Status.CONFLICT);
			builder.entity(error);
			throw new WebApplicationException(builder.build());
		}
			
		account.setGivenName(firstName);
		account.setMiddleName(null);
		account.setSurname(lastName);
		account.setEmail("administrator@nowellpoint.com");
		account.setUsername(email);
		account.setPassword(password);
		account.setStatus(AccountStatus.UNVERIFIED);
			
		if (account.getHref() == null) {
			identityProviderService.createAccount( account );
		} else {
			identityProviderService.updateAccount( account );
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
		accountProfile.setHref(account.getHref());
			
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
		
		Plan plan = planService.findPlan(planId);
		
		/**
		 * 
		 * 
		 * 
		 * 
		 */
		
		CustomerRequest customerRequest = new CustomerRequest()
				.id(accountProfile.getId())
				.company(accountProfile.getCompany())
				.email(accountProfile.getEmail())
				.firstName(accountProfile.getFirstName())
				.lastName(accountProfile.getLastName())
				.phone(accountProfile.getPhone());
		
		Result<com.braintreegateway.Customer> customerResult = null;
		
		try {
			customerResult = paymentGatewayService.addOrUpdateCustomer(customerRequest);
		} catch (NotFoundException e) {
			LOGGER.error(e);
		}
		
		if (! customerResult.isSuccess()) {
			LOGGER.error(customerResult.getMessage());
		}
		
		/**
		 * 
		 * 
		 * 
		 * 
		 */
		
		Result<com.braintreegateway.CreditCard> creditCardResult = null;
		
		if (customerResult.getTarget().getCreditCards().isEmpty()) {
			
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
			
			creditCardResult = paymentGatewayService.createCreditCard(creditCardRequest);
			
		} else {
			
			AddressRequest addressRequest = new AddressRequest()
					.countryCodeAlpha2(countryCode);
			
			Result<com.braintreegateway.Address> addressResult = paymentGatewayService.updateAddress(accountProfile.getId(), customerResult.getTarget().getAddresses().get(0).getId(), addressRequest);
			
			CreditCardRequest creditCardRequest = new CreditCardRequest()
					.cardholderName( accountProfile.getName() )
					.expirationMonth( expirationMonth )
					.expirationYear( expirationYear )
					.number( cardNumber )
					.customerId( accountProfile.getId() )
					.billingAddressId(addressResult.getTarget().getId());
			
			creditCardResult = paymentGatewayService.updateCreditCard(accountProfile.getPrimaryCreditCard().getToken(), creditCardRequest);
		}
		
		if (! creditCardResult.isSuccess()) {
			LOGGER.error(creditCardResult.getMessage());
		}
		
		/**
		 * 
		 * 
		 * 
		 * 
		 */
		
		SubscriptionRequest subscriptionRequest = new SubscriptionRequest()
				.paymentMethodToken(creditCardResult.getTarget().getToken())
				.planId(plan.getPlanCode())
				.price(new BigDecimal(plan.getPrice().getUnitPrice()));
		
		Result<com.braintreegateway.Subscription> subscriptionResult = null;
			
		if (isNull(accountProfile.getSubscription())) {
			subscriptionResult = paymentGatewayService.createSubscription(subscriptionRequest);
		} else {
			subscriptionResult = paymentGatewayService.updateSubscription(accountProfile.getSubscription().getSubscriptionId(), subscriptionRequest);
		}

		if (! subscriptionResult.isSuccess()) {
			LOGGER.error(subscriptionResult.getMessage());
		}
		
		/**
		 * 
		 * 
		 * 
		 * 
		 */
		
		Date now = Date.from(Instant.now());
			
		CreditCard creditCard = new CreditCard();
		creditCard.setCardholderName(creditCardResult.getTarget().getCardholderName());
		creditCard.setCardType(creditCardResult.getTarget().getCardType());
		creditCard.setExpirationMonth(creditCardResult.getTarget().getExpirationMonth());
		creditCard.setExpirationYear(creditCardResult.getTarget().getExpirationYear());
		creditCard.setImageUrl(creditCardResult.getTarget().getImageUrl());
		creditCard.setLastFour(creditCardResult.getTarget().getLast4());
		creditCard.setPrimary(Boolean.TRUE);
		creditCard.setToken(creditCardResult.getTarget().getToken());
		creditCard.setNumber(creditCardResult.getTarget().getMaskedNumber());
		creditCard.setAddedOn(now);
		creditCard.setUpdatedOn(now);
		
		accountProfile.setCreditCards(Collections.emptySet());
			
		accountProfile.addCreditCard(creditCard);
		
		Subscription subscription = new Subscription();
		subscription.setPlanId(planId);
		subscription.setCurrencyIsoCode(plan.getPrice().getCurrencyIsoCode());
		subscription.setPlanCode(plan.getPlanCode());
		subscription.setUnitPrice(plan.getPrice().getUnitPrice());
		subscription.setPlanName(plan.getPlanName());
		subscription.setBillingFrequency(plan.getBillingFrequency());
		subscription.setCurrencySymbol(plan.getPrice().getCurrencySymbol());
		subscription.setAddedOn(now);
		subscription.setUpdatedOn(now);
		subscription.setSubscriptionId(subscriptionResult.getTarget().getId());
		
		accountProfile.setSubscription(subscription);
		
		accountProfileService.updateAccountProfile( accountProfile );
		
		/**
		 * 
		 * 
		 * 
		 * 
		 */
		
		String emailVerificationToken = account.getEmailVerificationToken().getHref().substring(account.getEmailVerificationToken().getHref().lastIndexOf("/") + 1);
		
		emailService.sendEmailVerificationMessage(accountProfile.getEmail(), accountProfile.getName(), emailVerificationToken);
		
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
	@Path("verify-email")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response verifyEmail(@FormParam("emailVerificationToken") String emailVerificationToken) {
		
		String href = identityProviderService.verifyEmail(emailVerificationToken);
		
		Account account = identityProviderService.getAccountByHref(href);
		
		identityProviderService.updateEmail(href, account.getUsername());
		
		emailService.sendWelcomeMessage(account.getUsername(), account.getUsername(), account.getFullName());
		
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