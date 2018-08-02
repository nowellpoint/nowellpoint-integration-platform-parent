package com.nowellpoint.console.service.impl;

import java.sql.Date;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.Subscription;
import com.nowellpoint.console.entity.Address;
import com.nowellpoint.console.entity.CreditCard;
import com.nowellpoint.console.entity.IdentityDAO;
import com.nowellpoint.console.entity.Organization;
import com.nowellpoint.console.entity.OrganizationDAO;
import com.nowellpoint.console.entity.Transaction;
import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.service.AbstractService;
import com.nowellpoint.console.service.IdentityService;

public class IdentityServiceImpl extends AbstractService implements IdentityService {
	
	private static BraintreeGateway gateway = new BraintreeGateway(
			Environment.parseEnvironment(System.getenv("BRAINTREE_ENVIRONMENT")),
			System.getenv("BRAINTREE_MERCHANT_ID"),
			System.getenv("BRAINTREE_PUBLIC_KEY"),
			System.getenv("BRAINTREE_PRIVATE_KEY")
	);
	
	static {
		gateway.clientToken().generate();
	}
	
	private IdentityDAO identityDAO;
	private OrganizationDAO organizationDAO;
	
	public IdentityServiceImpl() {
		identityDAO = new IdentityDAO(com.nowellpoint.console.entity.Identity.class, datastore);
		organizationDAO = new OrganizationDAO(com.nowellpoint.console.entity.Organization.class, datastore);
	}

	@Override
	public Identity getIdentity(String id) {
		
		com.nowellpoint.console.entity.Identity entity = getEntry(id);
		
		if (entity == null) {
			
			try {
				entity = identityDAO.get(new ObjectId(id));
			} catch (IllegalArgumentException e) {
				throw new BadRequestException(String.format("Invalid Identity Id: %s", id));
			}
			
			if (entity == null) {
				throw new NotFoundException(String.format("Identity Id: %s was not found", id));
			}
			
			putEntry(entity.getId().toString(), entity);
		}
		
		return Identity.of(entity);
	}
	
	@Override
	public Identity getBySubject(String subject) {
		
		Query<com.nowellpoint.console.entity.Identity> query = identityDAO.createQuery()
				.field("subject")
				.equal(subject);
		
		com.nowellpoint.console.entity.Identity entity = identityDAO.findOne(query);
		
		Organization organization = entity.getOrganization();
		
		Subscription subscription = gateway.subscription().find(organization.getSubscription().getNumber());
		
		Set<Transaction> transactions = new HashSet<>();
		
		subscription.getTransactions().forEach(t -> {
			CreditCard creditCard = new CreditCard();
			creditCard.setAddedOn(t.getCreditCard().getCreatedAt() != null ? t.getCreditCard().getCreatedAt().getTime() : Date.from(Instant.now()));
			creditCard.setCardholderName(t.getCreditCard().getCardholderName());
			creditCard.setCardType(t.getCreditCard().getCardType());
			creditCard.setExpirationMonth(t.getCreditCard().getExpirationMonth());
			creditCard.setExpirationYear(t.getCreditCard().getExpirationYear());
			creditCard.setImageUrl(t.getCreditCard().getImageUrl());
			creditCard.setLastFour(t.getCreditCard().getLast4());
			creditCard.setToken(t.getCreditCard().getToken());
			creditCard.setUpdatedOn(t.getCreditCard().getUpdatedAt() != null ? t.getCreditCard().getUpdatedAt().getTime() : Date.from(Instant.now()));
			
			Address billingAddress = new Address();
			billingAddress.setAddedOn(t.getBillingAddress().getCreatedAt() != null ? t.getCreditCard().getBillingAddress().getCreatedAt().getTime() : Date.from(Instant.now()));
			billingAddress.setCity(t.getBillingAddress().getLocality());
			billingAddress.setCountryCode(t.getBillingAddress().getCountryCodeAlpha2());
			billingAddress.setId(t.getBillingAddress().getId());
			billingAddress.setPostalCode(t.getBillingAddress().getPostalCode());
			billingAddress.setState(t.getBillingAddress().getRegion());
			billingAddress.setStreet(t.getBillingAddress().getStreetAddress());
			billingAddress.setUpdatedOn(t.getBillingAddress().getUpdatedAt() != null ? t.getCreditCard().getBillingAddress().getUpdatedAt().getTime() : Date.from(Instant.now()));
			
			Transaction transaction = new Transaction();
			transaction.setBillingAddress(billingAddress);
			transaction.setBillingPeriodEndDate(t.getSubscription().getBillingPeriodEndDate().getTime());
			transaction.setBillingPeriodStartDate(t.getSubscription().getBillingPeriodStartDate().getTime());
			transaction.setAmount(t.getAmount().doubleValue());
			transaction.setCreatedOn(t.getCreatedAt().getTime());
			transaction.setCreditCard(creditCard);
			transaction.setCurrencyIsoCode(t.getCurrencyIsoCode());
			transaction.setId(t.getId());
			transaction.setPlan(t.getPlanId());
			transaction.setFirstName(t.getCustomer().getFirstName());
			transaction.setLastName(t.getCustomer().getLastName());
			transaction.setStatus(t.getStatus().name());
			transaction.setUpdatedOn(t.getUpdatedAt().getTime());
			transactions.add(transaction);
		});
		
		organization.getSubscription().setBillingPeriodEndDate(subscription.getBillingPeriodEndDate().getTime());
		organization.getSubscription().setBillingPeriodStartDate(subscription.getBillingPeriodStartDate().getTime());
		organization.getSubscription().setNextBillingDate(subscription.getNextBillingDate().getTime());
		organization.setTransactions(transactions);
		
		organizationDAO.save(entity.getOrganization());
		
		putEntry(entity.getId().toString(), entity);
		
		return Identity.of(entity);
	}
}