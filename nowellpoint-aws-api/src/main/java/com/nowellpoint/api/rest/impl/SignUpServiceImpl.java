package com.nowellpoint.api.rest.impl;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.api.rest.SignUpService;
import com.nowellpoint.api.rest.domain.Registration;
import com.nowellpoint.api.service.RegistrationService;

public class SignUpServiceImpl implements SignUpService {
	
	@Inject
	private RegistrationService registrationService;
	
	@Context
	private UriInfo uriInfo;

//    public Response signUp(
//    		String firstName,
//    		String lastName,
//    		String email,
//    		String countryCode,
//    		String password,
//    		String confirmPassword,
//    		String planId,
//    		String cardNumber,
//    		String expirationMonth,
//    		String expirationYear,
//    		String securityCode) {
//		
//		/**
//		 * 
//		 * 
//		 * 
//		 */
//		
//		if (! password.equals(confirmPassword)) {
//			Error error = new Error(2000, "Password mismatch");
//			ResponseBuilder builder = Response.status(Status.BAD_REQUEST);
//			builder.entity(error);
//			throw new WebApplicationException(builder.build());
//		}
//		
//		/**
//		 * 
//		 * 
//		 * 
//		 * 
//		 */
//		
//		AccountProfile accountProfile = null;
//		
//		try {
//			
//			accountProfile = accountProfileService.findByUsername(email);
//			
//			if (accountProfile.getIsActive()) {
//				Error error = new Error(1000, "Account for email is already enabled");
//				ResponseBuilder builder = Response.status(Status.CONFLICT);
//				builder.entity(error);
//				throw new WebApplicationException(builder.build());
//			}
//			
//		} catch (DocumentNotFoundException e) {
//			
//			//User user = identityProviderService.findByUsername(email);
//			
//			//if (isNotNull(user)) {
//			//	user.delete();
//			//} 
//			
//			User user = identityProviderService.createUser(email, firstName, lastName);
//					
//			accountProfile = AccountProfile.createAccountProfile();
//			accountProfile.setAccountHref(user.getResourceHref());
//			//accountProfile.setEmailVerificationToken(user..getEmailVerificationToken().getValue());
//			accountProfile.setFirstName(firstName);
//			accountProfile.setLastName(lastName);
//			accountProfile.setEmail(email);
//			accountProfile.setUsername(email);
//			accountProfile.setIsActive(Boolean.FALSE);
//				
//			Address address = accountProfile.getAddress() != null ? accountProfile.getAddress() : new Address();
//			address.setCountryCode(countryCode);
//				
//			accountProfile.setAddress(address);
//			
//			if (isNull(accountProfile.getId())) {	
//				accountProfileService.createAccountProfile( accountProfile );
//			} else {
//				accountProfileService.updateAccountProfile( accountProfile );
//			}
//		}
//		
//		/**
//		 * 
//		 * 
//		 * 
//		 * 
//		 */
//		
//		Plan plan = planService.findById(planId);
//		
//		Subscription subscription = new Subscription();
//		subscription.setPlanId(planId);
//		subscription.setCurrencyIsoCode(plan.getPrice().getCurrencyIsoCode());
//		subscription.setPlanCode(plan.getPlanCode());
//		subscription.setUnitPrice(plan.getPrice().getUnitPrice());
//		subscription.setPlanName(plan.getPlanName());
//		subscription.setBillingFrequency(plan.getBillingFrequency());
//		subscription.setCurrencySymbol(plan.getPrice().getCurrencySymbol());
//		subscription.setAddedOn(Date.from(Instant.now()));
//		subscription.setUpdatedOn(Date.from(Instant.now()));
//		
//		/**
//		 * 
//		 * 
//		 * 
//		 * 
//		 */
//		
//		CustomerRequest customerRequest = new CustomerRequest()
//				.id(accountProfile.getId())
//				.company(accountProfile.getCompany())
//				.email(accountProfile.getEmail())
//				.firstName(accountProfile.getFirstName())
//				.lastName(accountProfile.getLastName())
//				.phone(accountProfile.getPhone());
//		
//		Result<com.braintreegateway.Customer> customerResult = null;
//		
//		try {
//			customerResult = paymentGatewayService.addOrUpdateCustomer(customerRequest);
//		} catch (NotFoundException e) {
//			LOGGER.error(e);
//		}
//		
//		if (! customerResult.isSuccess()) {
//			LOGGER.error(customerResult.getMessage());
//		}
//		
//		/**
//		 * 
//		 * 
//		 * 
//		 * 
//		 */
//		
//		AddressRequest addressRequest = new AddressRequest()
//				.countryCodeAlpha2(countryCode);
//		
//		Result<com.braintreegateway.Address> addressResult = null;
//		
//		if (isNull(customerResult.getTarget().getAddresses()) || customerResult.getTarget().getAddresses().isEmpty()) {
//			addressResult = paymentGatewayService.createAddress(accountProfile.getId(), addressRequest);
//		} else {
//			addressResult = paymentGatewayService.updateAddress(accountProfile.getId(), customerResult.getTarget().getAddresses().get(0).getId(), addressRequest);
//		}
//		
//		if (! addressResult.isSuccess()) {
//			LOGGER.error(customerResult.getMessage());
//		}
//		
//		/**
//		 * 
//		 * 
//		 * 
//		 * 
//		 */
//		
//		if (plan.getPrice().getUnitPrice() > 0) {
//			
//			if (isNull(cardNumber) || isNull(expirationMonth) || isNull(expirationYear) || isNull(securityCode)) {
//				throw new IllegalArgumentException("Missing credit card information. Required information: card number, expiration month, expiration year and security code");
//			}
//			
//			Result<com.braintreegateway.CreditCard> creditCardResult = null;
//			
//			if (isNull(customerResult.getTarget().getCreditCards()) || customerResult.getTarget().getCreditCards().isEmpty()) {
//				
//				CreditCardRequest creditCardRequest = new CreditCardRequest()
//						.cardholderName( accountProfile.getName() )
//						.expirationMonth( expirationMonth )
//						.expirationYear( expirationYear )
//						.number( cardNumber )
//						.customerId( accountProfile.getId() )
//						.billingAddressId(addressResult.getTarget().getId());
//				
//				creditCardResult = paymentGatewayService.createCreditCard(creditCardRequest);
//				
//			} else {
//				
//				CreditCardRequest creditCardRequest = new CreditCardRequest()
//						.cardholderName( accountProfile.getName() )
//						.expirationMonth( expirationMonth )
//						.expirationYear( expirationYear )
//						.number( cardNumber )
//						.customerId( accountProfile.getId() )
//						.billingAddressId(addressResult.getTarget().getId());
//				
//				creditCardResult = paymentGatewayService.updateCreditCard(accountProfile.getPrimaryCreditCard().getToken(), creditCardRequest);
//			}
//			
//			if (! creditCardResult.isSuccess()) {
//				LOGGER.error(creditCardResult.getMessage());
//			}
//			
//			/**
//			 * 
//			 * 
//			 * 
//			 * 
//			 */
//			
//			SubscriptionRequest subscriptionRequest = new SubscriptionRequest()
//					.paymentMethodToken(creditCardResult.getTarget().getToken())
//					.planId(plan.getPlanCode())
//					.price(new BigDecimal(plan.getPrice().getUnitPrice()));
//			
//			Result<com.braintreegateway.Subscription> subscriptionResult = null;
//				
//			if (isNull(accountProfile.getSubscription())) {
//				subscriptionResult = paymentGatewayService.createSubscription(subscriptionRequest);
//			} else {
//				subscriptionResult = paymentGatewayService.updateSubscription(accountProfile.getSubscription().getSubscriptionId(), subscriptionRequest);
//			}
//
//			if (! subscriptionResult.isSuccess()) {
//				LOGGER.error(subscriptionResult.getMessage());
//			}
//			
//			/**
//			 * 
//			 * 
//			 * 
//			 * 
//			 */
//				
//			CreditCard creditCard = new CreditCard();
//			creditCard.setCardholderName(creditCardResult.getTarget().getCardholderName());
//			creditCard.setCardType(creditCardResult.getTarget().getCardType());
//			creditCard.setExpirationMonth(creditCardResult.getTarget().getExpirationMonth());
//			creditCard.setExpirationYear(creditCardResult.getTarget().getExpirationYear());
//			creditCard.setImageUrl(creditCardResult.getTarget().getImageUrl());
//			creditCard.setLastFour(creditCardResult.getTarget().getLast4());
//			creditCard.setPrimary(Boolean.TRUE);
//			creditCard.setToken(creditCardResult.getTarget().getToken());
//			creditCard.setNumber(creditCardResult.getTarget().getMaskedNumber());
//			creditCard.setAddedOn(Date.from(Instant.now()));
//			creditCard.setUpdatedOn(Date.from(Instant.now()));
//			
//			accountProfile.setCreditCards(null);
//				
//			accountProfile.addCreditCard(creditCard);
//			
//			subscription.setSubscriptionId(subscriptionResult.getTarget().getId());
//		}
//		
//		accountProfile.setSubscription(subscription);
//		
//		accountProfileService.updateAccountProfile( accountProfile );
//		
//		/**
//		 * 
//		 * 
//		 * 
//		 * 
//		 */
//		
//		URI emailVerificationTokenUri = UriBuilder.fromUri(uriInfo.getBaseUri())
//				.path(SignUpService.class)
//				.path("verify-email")
//				.path("{emailVerificationToken}")
//				.build(accountProfile.getEmailVerificationToken());
//		
//		URI resourceUri = UriBuilder.fromUri(uriInfo.getBaseUri())
//				.path(AccountProfileResource.class)
//				.path("/{id}")
//				.build(accountProfile.getId());
//		
//		Map<String,Object> response = new HashMap<String,Object>();
//		response.put("href", resourceUri);
//		response.put("emailVerificationToken", accountProfile.getEmailVerificationToken());
//		response.put("emailVerificationTokenHref", emailVerificationTokenUri);
//		
//		return Response.ok(response)
//				.build();
//	}
    
	@Override
    public Response createRegistration(
    		String firstName,
    		String lastName,
    		String email,
    		String countryCode,
    		String domain,
    		String planId) {
    	
		Registration registration = registrationService.register(
				firstName, 
				lastName, 
				email, 
				countryCode, 
				domain,
				planId);
    	
    	return Response.ok(registration)
				.build();
    }
	
	@Override
	public Response updateRegistration(String id, String domain) {
		
		Registration registration = registrationService.updateRegistration(
				id, 
				domain);
		
		return Response.ok(registration)
				.build();
	}
    
	@Override
    public Response setPassword(String password, String confirmPassword) {
    	return Response.ok().build();
    }
	
	@Override
	public Response verifyEmail(String emailVerificationToken) {
		
		Registration registration = registrationService.verifyEmail(emailVerificationToken);
		
		
		
//		AccountProfile accountProfile = new AccountProfile();
//		accountProfile.setIsActive(Boolean.TRUE);
//		accountProfile.setEmailVerificationToken(null);
//		
//		accountProfileService.updateAccountProfile(accountProfile);
		
//		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
//				.path(AccountProfileResource.class)
//				.path("/{id}")
//				.build(accountProfile.getId());
		
		Map<String,Object> response = new HashMap<String,Object>();
//		response.put("href", uri);
		
		return Response.ok(registration)
				.build();
	}
}