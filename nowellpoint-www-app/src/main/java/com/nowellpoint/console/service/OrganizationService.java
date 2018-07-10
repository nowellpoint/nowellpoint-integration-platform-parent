package com.nowellpoint.console.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import javax.validation.ValidationException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.bson.types.ObjectId;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.SubscriptionRequest;
import com.nowellpoint.console.entity.OrganizationDAO;
import com.nowellpoint.console.model.BillingContactRequest;
import com.nowellpoint.console.model.CreditCard;
import com.nowellpoint.console.model.ModifiableOrganization;
import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.model.Plan;
import com.nowellpoint.console.model.CreditCardRequest;
import com.nowellpoint.console.model.Subscription;
import com.nowellpoint.console.util.UserContext;
import com.nowellpoint.util.Assert;

public class OrganizationService extends AbstractService {
	
	private static BraintreeGateway gateway = new BraintreeGateway(
			Environment.parseEnvironment(System.getenv("BRAINTREE_ENVIRONMENT")),
			System.getenv("BRAINTREE_MERCHANT_ID"),
			System.getenv("BRAINTREE_PUBLIC_KEY"),
			System.getenv("BRAINTREE_PRIVATE_KEY")
	);
	
	static {
		gateway.clientToken().generate();
	}
	
	private OrganizationDAO organizationDAO;
	
	public OrganizationService() {
		organizationDAO = new OrganizationDAO(com.nowellpoint.console.entity.Organization.class, datastore);
	}

	public Organization get(String id) {
		com.nowellpoint.console.entity.Organization entity = null;
		try {
			entity = organizationDAO.get(new ObjectId(id));
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(String.format("Invalid Organization Id: %s", id));
		}
		
		if (Assert.isNull(entity)) {
			throw new NotFoundException(String.format("Organization Id: %s was not found",id));
		}
		
		Organization organization = Organization.of(entity);
		return organization;
	}
	
	public Organization update(String id, CreditCardRequest request) {
		
		Organization instance = get(id);
		
		com.braintreegateway.CreditCardRequest creditCardRequest = new com.braintreegateway.CreditCardRequest()
				.billingAddressId(instance.getSubscription().getBillingAddress().getId())
				.cardholderName(request.getCardholderName())
				.customerId(instance.getNumber())
				.cvv(Assert.isEmpty(request.getCvv()) ? null : request.getCvv())
				.expirationMonth(request.getExpirationMonth())
				.expirationYear(request.getExpirationYear())
				.number(Assert.isEmpty(request.getNumber()) ? null : request.getNumber());
		
		Result<com.braintreegateway.CreditCard> creditCardResult = gateway.creditCard().update(instance.getSubscription().getCreditCard().getToken(), creditCardRequest);
		
		if (creditCardResult.getMessage() != null) {
			throw new ValidationException(creditCardResult.getMessage());
		}
		
		CreditCard creditCard = CreditCard.builder()
				.from(instance.getSubscription().getCreditCard())
				.cardholderName(creditCardResult.getTarget().getCardholderName())
				.cardType(creditCardResult.getTarget().getCardType())
				.expirationMonth(request.getExpirationMonth())
				.expirationYear(request.getExpirationYear())
				.imageUrl(creditCardResult.getTarget().getImageUrl())
				.lastFour(creditCardResult.getTarget().getLast4())
				.updatedOn(Date.from(Instant.now()))
				.build();
		
		Subscription subscription = Subscription.builder()
				.from(instance.getSubscription())
				.creditCard(creditCard)
				.build();
		
		Organization organization = Organization.builder()
				.from(instance)
				.subscription(subscription)
				.build();
		
		return update(organization);
	}
	
	public Organization setPlan(String id, Plan plan) {
		
		Organization instance = get(id);
		
		SubscriptionRequest subscriptionRequest = new SubscriptionRequest()
				.paymentMethodToken(instance.getSubscription().getCreditCard().getToken())
				.planId(plan.getPlanCode())
				.price(new BigDecimal(plan.getPrice().getUnitPrice()));
		
		Result<com.braintreegateway.Subscription> subscriptionResult = null;
		
		Optional<String> number = Optional.of(instance)
				.map(Organization::getSubscription)
				.map(Subscription::getNumber);
		
		if (number.isPresent()) {
			subscriptionResult = gateway.subscription().update(instance.getSubscription().getNumber(), subscriptionRequest);
		} else {
			subscriptionResult = gateway.subscription().create(subscriptionRequest);
		}
		
		if (subscriptionResult.getMessage() != null) {
			throw new ValidationException(subscriptionResult.getMessage());
		}
		
		Subscription subscription = Subscription.builder()
				.from(instance.getSubscription())
				.billingFrequency(plan.getBillingFrequency())
				.currencyIsoCode(plan.getPrice().getCurrencyIsoCode())
				.currencySymbol(plan.getPrice().getCurrencySymbol())
				.billingPeriodEndDate(subscriptionResult.getTarget().getBillingPeriodEndDate().getTime())
				.billingPeriodStartDate(subscriptionResult.getTarget().getBillingPeriodStartDate().getTime())
				.features(plan.getFeatures())
				.nextBillingDate(subscriptionResult.getTarget().getNextBillingDate().getTime())
				.planCode(plan.getPlanCode())
				.planId(plan.getId())
				.planName(plan.getPlanName())
				.unitPrice(plan.getPrice().getUnitPrice())
				.updatedOn(Date.from(Instant.now()))
				.build();
		
		Organization organization = Organization.builder()
				.from(instance)
				.subscription(subscription)
				.build();
		
		return update(organization);
	}
	
	public Organization setPlan(String id, Plan plan, CreditCardRequest request) {
		
		Organization instance = get(id);
		
		com.braintreegateway.CreditCardRequest creditCardRequest = new com.braintreegateway.CreditCardRequest()
				.billingAddressId(instance.getSubscription().getBillingAddress().getId())
				.cardholderName(request.getCardholderName())
				.customerId(instance.getNumber())
				.cvv(Assert.isEmpty(request.getCvv()) ? null : request.getCvv())
				.expirationMonth(request.getExpirationMonth())
				.expirationYear(request.getExpirationYear())
				.number(Assert.isEmpty(request.getNumber()) ? null : request.getNumber());
		
		Result<com.braintreegateway.CreditCard> creditCardResult = null;
		
		Optional<String> token = Optional.of(instance)
				.map(Organization::getSubscription)
				.map(Subscription::getCreditCard)
				.map(CreditCard::getToken);
		
		if (token.isPresent()) {
			creditCardResult = gateway.creditCard().update(instance.getSubscription().getCreditCard().getToken(), creditCardRequest);
		} else {
			creditCardResult = gateway.creditCard().create(creditCardRequest);
		}
		
		if (creditCardResult.getMessage() != null) {
			throw new ValidationException(creditCardResult.getMessage());
		}
		
		CreditCard creditCard = CreditCard.builder()
				.from(instance.getSubscription().getCreditCard())
				.cardholderName(creditCardResult.getTarget().getCardholderName())
				.cardType(creditCardResult.getTarget().getCardType())
				.expirationMonth(request.getExpirationMonth())
				.expirationYear(request.getExpirationYear())
				.imageUrl(creditCardResult.getTarget().getImageUrl())
				.lastFour(creditCardResult.getTarget().getLast4())
				.updatedOn(Date.from(Instant.now()))
				.build();
		
		SubscriptionRequest subscriptionRequest = new SubscriptionRequest()
				.paymentMethodToken(creditCardResult.getTarget().getToken())
				.planId(plan.getPlanCode())
				.price(new BigDecimal(plan.getPrice().getUnitPrice()));
		
		Result<com.braintreegateway.Subscription> subscriptionResult = null;
		
		Optional<String> number = Optional.of(instance)
				.map(Organization::getSubscription)
				.map(Subscription::getNumber);
		
		if (number.isPresent()) {
			subscriptionResult = gateway.subscription().update(instance.getSubscription().getNumber(), subscriptionRequest);
		} else {
			subscriptionResult = gateway.subscription().create(subscriptionRequest);
		}
		
		if (subscriptionResult.getMessage() != null) {
			throw new ValidationException(subscriptionResult.getMessage());
		}
		
		Subscription subscription = Subscription.builder()
				.from(instance.getSubscription())
				.billingFrequency(plan.getBillingFrequency())
				.creditCard(creditCard)
				.currencyIsoCode(plan.getPrice().getCurrencyIsoCode())
				.currencySymbol(plan.getPrice().getCurrencySymbol())
				.billingPeriodEndDate(subscriptionResult.getTarget().getBillingPeriodEndDate().getTime())
				.billingPeriodStartDate(subscriptionResult.getTarget().getBillingPeriodStartDate().getTime())
				.features(plan.getFeatures())
				.planCode(plan.getPlanCode())
				.planId(plan.getId())
				.planName(plan.getPlanName())
				.unitPrice(plan.getPrice().getUnitPrice())
				.updatedOn(Date.from(Instant.now()))
				.build();
		
		Organization organization = Organization.builder()
				.from(instance)
				.subscription(subscription)
				.build();
		
		return update(organization);
	}
	
	public Organization update(String id, BillingContactRequest request) {
		return null;
	}
	
	private Organization update(Organization organization) {
		com.nowellpoint.console.entity.Organization entity = modelMapper.map(organization, com.nowellpoint.console.entity.Organization.class);
		entity.setLastUpdatedOn(Date.from(Instant.now()));
		entity.setLastUpdatedBy(UserContext.get().getUserId());
		organizationDAO.save(entity);
		return Organization.of(entity);
	}
}