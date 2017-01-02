package com.nowellpoint.api.resource;

import static com.nowellpoint.util.Assert.isNull;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
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
import com.stormpath.sdk.resource.ResourceException;

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
		
		AccountProfile accountProfile = null;
		
		try {
			
			accountProfile = accountProfileService.findAccountProfileByUsername(email);
			
			if (accountProfile.getIsActive()) {
				Error error = new Error(1000, "Account for email is already enabled");
				ResponseBuilder builder = Response.status(Status.CONFLICT);
				builder.entity(error);
				throw new WebApplicationException(builder.build());
			}
			
		} catch (DocumentNotFoundException e) {
			
			Account account = identityProviderService.findByUsername(email);
			
			if (isNull(account)) {
				account = identityProviderService.createAccount(email, firstName, lastName, password);
			}
					
			accountProfile = new AccountProfile();
			accountProfile.setHref(account.getHref());
			accountProfile.setEmailVerificationToken(account.getEmailVerificationToken().getValue());
			accountProfile.setFirstName(firstName);
			accountProfile.setLastName(lastName);
			accountProfile.setEmail(email);
			accountProfile.setUsername(email);
			accountProfile.setIsActive(Boolean.FALSE);
				
			Address address = accountProfile.getAddress() != null ? accountProfile.getAddress() : new Address();
			address.setCountryCode(countryCode);
				
			accountProfile.setAddress(address);
			
			if (isNull(accountProfile.getId())) {	
				accountProfileService.createAccountProfile( accountProfile );
			} else {
				accountProfileService.updateAccountProfile( accountProfile );
			}
		}
		
		/**
		 * 
		 * 
		 * 
		 * 
		 */
		
		Plan plan = planService.findPlan(planId);
		
		Subscription subscription = new Subscription();
		subscription.setPlanId(planId);
		subscription.setCurrencyIsoCode(plan.getPrice().getCurrencyIsoCode());
		subscription.setPlanCode(plan.getPlanCode());
		subscription.setUnitPrice(plan.getPrice().getUnitPrice());
		subscription.setPlanName(plan.getPlanName());
		subscription.setBillingFrequency(plan.getBillingFrequency());
		subscription.setCurrencySymbol(plan.getPrice().getCurrencySymbol());
		subscription.setAddedOn(Date.from(Instant.now()));
		subscription.setUpdatedOn(Date.from(Instant.now()));
		
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
		
		AddressRequest addressRequest = new AddressRequest()
				.countryCodeAlpha2(countryCode);
		
		Result<com.braintreegateway.Address> addressResult = null;
		
		if (isNull(customerResult.getTarget().getAddresses()) || customerResult.getTarget().getAddresses().isEmpty()) {
			addressResult = paymentGatewayService.createAddress(accountProfile.getId(), addressRequest);
		} else {
			addressResult = paymentGatewayService.updateAddress(accountProfile.getId(), customerResult.getTarget().getAddresses().get(0).getId(), addressRequest);
		}
		
		if (! addressResult.isSuccess()) {
			LOGGER.error(customerResult.getMessage());
		}
		
		/**
		 * 
		 * 
		 * 
		 * 
		 */
		
		if (plan.getPrice().getUnitPrice() > 0) {
			
			if (isNull(cardNumber) || isNull(expirationMonth) || isNull(expirationYear) || isNull(securityCode)) {
				throw new IllegalArgumentException("Missing credit card information. Required information: card number, expiration month, expiration year and security code");
			}
			
			Result<com.braintreegateway.CreditCard> creditCardResult = null;
			
			if (isNull(customerResult.getTarget().getCreditCards()) || customerResult.getTarget().getCreditCards().isEmpty()) {
				
				CreditCardRequest creditCardRequest = new CreditCardRequest()
						.cardholderName( accountProfile.getName() )
						.expirationMonth( expirationMonth )
						.expirationYear( expirationYear )
						.number( cardNumber )
						.customerId( accountProfile.getId() )
						.billingAddressId(addressResult.getTarget().getId());
				
				creditCardResult = paymentGatewayService.createCreditCard(creditCardRequest);
				
			} else {
				
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
			creditCard.setAddedOn(Date.from(Instant.now()));
			creditCard.setUpdatedOn(Date.from(Instant.now()));
			
			accountProfile.setCreditCards(null);
				
			accountProfile.addCreditCard(creditCard);
			
			subscription.setSubscriptionId(subscriptionResult.getTarget().getId());
		}
		
		accountProfile.setSubscription(subscription);
		
		accountProfileService.updateAccountProfile( accountProfile );
		
		/**
		 * 
		 * 
		 * 
		 * 
		 */
		
		emailService.sendEmailVerificationMessage(accountProfile.getEmail(), accountProfile.getName(), accountProfile.getEmailVerificationToken());
		
		URI emailVerificationTokenUri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(SignUpService.class)
				.path("verify-email")
				.path("{emailVerificationToken}")
				.build(accountProfile.getEmailVerificationToken());
		
		URI resourceUri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(AccountProfileResource.class)
				.path("/{id}")
				.build(accountProfile.getId());
		
		Map<String,Object> response = new HashMap<String,Object>();
		response.put("href", resourceUri);
		response.put("emailVerificationToken", accountProfile.getEmailVerificationToken());
		response.put("emailVerificationTokenHref", emailVerificationTokenUri);
		
		return Response.ok(response)
				.build();
	}
	
	@PermitAll
	@POST
	@Path("verify-email/{emailVerificationToken}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response verifyEmail(@PathParam("emailVerificationToken") String emailVerificationToken) {
		
		Account account = null;
		
		try {
			account = identityProviderService.verifyEmail(emailVerificationToken);
		} catch (ResourceException e) {
			Error error = new Error(e.getCode(), e.getDeveloperMessage());
			ResponseBuilder builder = Response.status(Status.BAD_REQUEST);
			builder.entity(error);
			throw new WebApplicationException(builder.build());
		}
		
		Optional<AccountProfile> query = Optional.ofNullable(accountProfileService.findAccountProfileByHref(account.getHref()));
		
		if (! query.isPresent()) {
			Error error = new Error(1001, String.format("AccountProfile for href: %s was not found", account.getHref()));
			ResponseBuilder builder = Response.status(Status.NOT_FOUND);
			builder.entity(error);
			throw new WebApplicationException(builder.build());
		}
		
		AccountProfile accountProfile = query.get();
		accountProfile.setIsActive(Boolean.TRUE);
		accountProfile.setEmailVerificationToken(null);
		
		accountProfileService.updateAccountProfile(accountProfile);
		
		emailService.sendWelcomeMessage(accountProfile.getEmail(), accountProfile.getUsername(), accountProfile.getName());
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(AccountProfileResource.class)
				.path("/{id}")
				.build(accountProfile.getId());
		
		Map<String,Object> response = new HashMap<String,Object>();
		response.put("href", uri);
		
		return Response.ok(response)
				.build();
	}
}