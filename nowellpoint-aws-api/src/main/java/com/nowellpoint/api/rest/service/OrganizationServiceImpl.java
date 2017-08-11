package com.nowellpoint.api.rest.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import com.braintreegateway.AddressRequest;
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.CreditCardRequest;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.SubscriptionRequest;
import com.mongodb.client.model.Filters;
import com.nowellpoint.api.rest.domain.Address;
import com.nowellpoint.api.rest.domain.Contact;
import com.nowellpoint.api.rest.domain.CreditCard;
import com.nowellpoint.api.rest.domain.Organization;
import com.nowellpoint.api.rest.domain.Plan;
import com.nowellpoint.api.rest.domain.ReferenceLink;
import com.nowellpoint.api.rest.domain.ReferenceLinkTypes;
import com.nowellpoint.api.rest.domain.Subscription;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.rest.domain.ValidationException;
import com.nowellpoint.api.service.OrganizationService;
import com.nowellpoint.api.service.PlanService;
import com.nowellpoint.api.util.MessageConstants;
import com.nowellpoint.api.util.MessageProvider;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;
import com.nowellpoint.util.Properties;

public class OrganizationServiceImpl extends AbstractOrganizationService implements OrganizationService {
	
	private static BraintreeGateway gateway = new BraintreeGateway(
			Environment.parseEnvironment(System.getProperty(Properties.BRAINTREE_ENVIRONMENT)),
			System.getProperty(Properties.BRAINTREE_MERCHANT_ID),
			System.getProperty(Properties.BRAINTREE_PUBLIC_KEY),
			System.getProperty(Properties.BRAINTREE_PRIVATE_KEY)
	);
	
	static {
		gateway.clientToken().generate();
	}
	
	@Inject
	private PlanService planService;
	
	@Override
	public Organization findById(String id) {
		return super.findById(id);
	}
	
	@Override
	public Organization findByDomain(String domain) {
		return super.query( Filters.eq( "domain", domain ));
	}

	@Override
	public Organization createOrganization(
			String domain, 
			String planId, 
			String firstName,
			String lastName,
			String email,
			String phone,
			String countryCode,
			String cardholderName, 
			String expirationMonth, 
			String expirationYear,
			String number, 
			String cvv) {
		
		UserInfo userInfo = UserInfo.of(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
		Plan plan = findPlanById(planId);
		
		if (plan.getPrice().getUnitPrice() > 0) {
			
			CustomerRequest customerRequest = new CustomerRequest()
					.company(domain)
					.email(email)
					.firstName(firstName)
					.lastName(lastName)
					.phone(phone)
					.creditCard()
						.cardholderName(cardholderName)
						.cvv(cvv)
						.expirationMonth(expirationMonth)
						.expirationYear(expirationYear)
						.number(number)
						.billingAddress()
							.countryCodeAlpha2(countryCode)
							.done()
						.done();
			
			Result<com.braintreegateway.Customer> customerResult = addCustomer(customerRequest);
			
			SubscriptionRequest subscriptionRequest = new SubscriptionRequest()
					.paymentMethodToken(customerResult.getTarget().getCreditCards().get(0).getToken())
					.planId(plan.getPlanCode())
					.price(new BigDecimal(plan.getPrice().getUnitPrice()));
			
			Result<com.braintreegateway.Subscription> subscriptionResult = createSubscription(subscriptionRequest);
			
			ReferenceLink referenceLink = ReferenceLink.builder()
					.id(customerResult.getTarget().getId())
					.type(ReferenceLinkTypes.CUSTOMER_ID.name())
					.build();
			
			CreditCard creditCard = CreditCard.builder()
					.addedOn(now)
					.cardholderName(cardholderName)
					.cardType(customerResult.getTarget().getCreditCards().get(0).getCardType())
					.expirationMonth(expirationMonth)
					.expirationYear(expirationYear)
					.imageUrl(customerResult.getTarget().getCreditCards().get(0).getImageUrl())
					.lastFour(customerResult.getTarget().getCreditCards().get(0).getLast4())
					.token(customerResult.getTarget().getCreditCards().get(0).getToken())
					.build();
			
			Address billingAddress = Address.builder()
					.id(customerResult.getTarget().getCreditCards().get(0).getBillingAddress().getId())
					.countryCode(countryCode)
					.build();
			
			Subscription subscription = Subscription.builder()
					.subscriptionId(subscriptionResult.getTarget().getId())
					.addedOn(now)
					.planId(plan.getId())
					.planCode(plan.getPlanCode())
					.planName(plan.getPlanName())
					.unitPrice(plan.getPrice().getUnitPrice())
					.currencySymbol(plan.getPrice().getCurrencySymbol())
					.currencyIsoCode(plan.getPrice().getCurrencyIsoCode())
					.billingFrequency(plan.getBillingFrequency())
					.creditCard(creditCard)
					.billingAddress(billingAddress)
					.updatedOn(now)
					.build();

			Organization organization = Organization.builder()
					.domain(domain)
					.subscription(subscription)
					.referenceLink(referenceLink)
					.createdBy(userInfo)
					.createdOn(now)
					.lastUpdatedBy(userInfo)
					.lastUpdatedOn(now)
					.build();
			
			super.create(organization);
			
			return organization;
			
		} else {
			
			Address billingAddress = Address.builder()
					.countryCode(countryCode)
					.build();
			
			Subscription subscription = Subscription.builder()
					.addedOn(now)
					.planId(plan.getId())
					.planCode(plan.getPlanCode())
					.planName(plan.getPlanName())
					.unitPrice(plan.getPrice().getUnitPrice())
					.currencySymbol(plan.getPrice().getCurrencySymbol())
					.currencyIsoCode(plan.getPrice().getCurrencyIsoCode())
					.billingFrequency(plan.getBillingFrequency())
					.billingAddress(billingAddress)
					.updatedOn(now)
					.build();

			Organization organization = Organization.builder()
					.domain(domain)
					.subscription(subscription)
					.createdBy(userInfo)
					.createdOn(now)
					.lastUpdatedBy(userInfo)
					.lastUpdatedOn(now)
					.build();
			
			super.create(organization);
			
			return organization;
			
		}
	}

	@Override
	public Organization updateOrganization(String id, String domain) {
		
		Organization organization = findById(id);
		
		Organization newInstance = Organization.builder()
				.from(organization)
				.domain(domain)
				.build();
		
		super.update(newInstance);
		
		return newInstance;
	}
	
	@Override
	public Organization changePlan(String id, String planId) {
		
		Organization organization = findById(id);
		
		Plan plan = planService.findById(planId);
		
		SubscriptionRequest subscriptionRequest = new SubscriptionRequest()
				.paymentMethodToken(organization.getSubscription().getCreditCard().getToken())
				.planId(plan.getPlanCode())
				.price(new BigDecimal(plan.getPrice().getUnitPrice()));
		
		updateSubscription(id, subscriptionRequest);
		
		Subscription subscription = Subscription.builder()
				.from(organization.getSubscription())
				.planId(plan.getId())
				.planCode(plan.getPlanCode())
				.planName(plan.getPlanName())
				.unitPrice(plan.getPrice().getUnitPrice())
				.currencySymbol(plan.getPrice().getCurrencySymbol())
				.currencyIsoCode(plan.getPrice().getCurrencyIsoCode())
				.billingFrequency(plan.getBillingFrequency())
				.updatedOn(Date.from(Instant.now()))
				.build();
		
		Organization newInstance = Organization.builder()
				.from(organization)
				.subscription(subscription)
				.build();
		
		super.update(newInstance);
		
		return newInstance;
		
	}
	
	@Override
	public Organization updateCreditCard(String id, String cardholderName, String expirationMonth, String expirationYear, String number, String cvv) {
		
		Organization organization = findById(id);
		
		CreditCardRequest creditCardRequest = new CreditCardRequest()
				.cardholderName(cardholderName)
				.expirationMonth(expirationMonth)
				.expirationYear(expirationYear)
				.number(number)
				.cvv(cvv)
				.customerId(organization.getReferenceLink().getId())
				.billingAddressId(organization.getSubscription().getBillingAddress().getId());
		
		Result<com.braintreegateway.CreditCard> result = updateCreditCard(organization.getSubscription().getCreditCard().getToken(), creditCardRequest);
		
		CreditCard creditCard = CreditCard.builder()
				.from(organization.getSubscription().getCreditCard())
				.cardholderName(cardholderName)
				.cardType(result.getTarget().getCardType())
				.expirationMonth(expirationMonth)
				.expirationYear(expirationYear)
				.imageUrl(result.getTarget().getImageUrl())
				.lastFour(result.getTarget().getLast4())
				.updatedOn(Date.from(Instant.now()))
				.build();
		
		Subscription subscription = Subscription.builder()
				.from(organization.getSubscription())
				.creditCard(creditCard)
				.build();
		
		Organization newInstance = Organization.builder()
				.from(organization)
				.subscription(subscription)
				.build();
		
		super.update(newInstance);
		
		return newInstance;
		
	}
	
	@Override
	public Organization updateBillingAddress(String id, String street, String city, String stateCode, String postalCode, String countryCode) {
		
		Organization organization = findById(id);
		
		AddressRequest addressRequest = new AddressRequest()
				.postalCode(postalCode)
				.streetAddress(street)
				.region(stateCode)
				.locality(city)
				.countryCodeAlpha2(countryCode);
		
		updateAddress(organization.getReferenceLink().getId(), organization.getSubscription().getBillingAddress().getId(), addressRequest);
		
		Address billingAddress = Address.builder().from(organization.getSubscription()
				.getBillingAddress())
				.city(city)
				.countryCode(countryCode)
				.postalCode(postalCode)
				.stateCode(stateCode)
				.street(street)
				.build();
		
		Subscription subscription = Subscription.builder()
				.from(organization.getSubscription())
				.billingAddress(billingAddress)
				.build();
		
		Organization newInstance = Organization.builder()
				.from(organization)
				.subscription(subscription)
				.build();
		
		super.update(newInstance);
		
		return newInstance;
	}
	
	@Override
	public Organization updateBillingContact(String id, String firstName, String lastName, String email, String phone) {
		
		Organization organization = findById(id);
		
		Contact billingContact = Contact.builder()
				.from(organization.getSubscription().getBillingContact())
				.firstName(firstName)
				.lastName(lastName)
				.email(email)
				.phone(phone)
				.build();
		
		Subscription subscription = Subscription.builder()
				.from(organization.getSubscription())
				.billingContact(billingContact)
				.build();
		
		Organization newInstance = Organization.builder()
				.from(organization)
				.subscription(subscription)
				.build();
		
		super.update(newInstance);
		
		return newInstance;

	}
	
	private Result<com.braintreegateway.Subscription> createSubscription(SubscriptionRequest subscriptionRequest) {
		return gateway.subscription().create(subscriptionRequest);
	}
	
	private Result<com.braintreegateway.Subscription> updateSubscription(String id, SubscriptionRequest subscriptionRequest) {
		return gateway.subscription().update(id, subscriptionRequest);
	}
	
	public Result<com.braintreegateway.Address> updateAddress(String customerId, String billingAddressId, AddressRequest addressRequest) {
		Result<com.braintreegateway.Address> addressResult = gateway.address().update(customerId, billingAddressId, addressRequest);
		return addressResult;
	}
	
	private Result<com.braintreegateway.CreditCard> addCreditCard(CreditCardRequest creditCardRequest) {
		Result<com.braintreegateway.CreditCard> result = gateway.creditCard().create(creditCardRequest);
		return result;
	}
	
	private Result<com.braintreegateway.CreditCard> updateCreditCard(String token, CreditCardRequest creditCardRequest) {
		Result<com.braintreegateway.CreditCard> result = gateway.creditCard().update(token, creditCardRequest);
		return result;
	}
	
	private Result<com.braintreegateway.Customer> addCustomer(CustomerRequest customerRequest) {
		Result<com.braintreegateway.Customer> result = gateway.customer().create(customerRequest);
		return result;
	}
	
	private Plan findPlanById(String planId) {
		try {
    		return planService.findById(planId);
    	} catch (DocumentNotFoundException ignore) {
    		throw new ValidationException(String.format(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.REGISTRATION_INVALID_PLAN), planId));
		}
	}
}