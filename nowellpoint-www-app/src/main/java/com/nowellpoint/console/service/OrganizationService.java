package com.nowellpoint.console.service;

import java.sql.Date;
import java.time.Instant;

import javax.validation.ValidationException;

import org.bson.types.ObjectId;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.nowellpoint.console.entity.OrganizationDAO;
import com.nowellpoint.console.model.BillingContactRequest;
import com.nowellpoint.console.model.CreditCard;
import com.nowellpoint.console.model.ModifiableOrganization;
import com.nowellpoint.console.model.Organization;
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
		com.nowellpoint.console.entity.Organization entity = organizationDAO.get(new ObjectId(id));
		ModifiableOrganization organization = modelMapper.map(entity, ModifiableOrganization.class);
		return organization.toImmutable();
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
	
	public Organization update(String id, BillingContactRequest request) {
		return null;
	}
	
	private Organization update(Organization organization) {
		com.nowellpoint.console.entity.Organization entity = modelMapper.map(organization, com.nowellpoint.console.entity.Organization.class);
		entity.setLastUpdatedOn(Date.from(Instant.now()));
		entity.setLastUpdatedBy(UserContext.get().getUserId());
		organizationDAO.save(entity);
		return modelMapper.map(entity, ModifiableOrganization.class).toImmutable();
	}
}