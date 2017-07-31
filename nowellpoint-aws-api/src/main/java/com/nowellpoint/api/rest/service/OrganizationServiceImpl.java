package com.nowellpoint.api.rest.service;

import java.time.Instant;
import java.util.Date;

import javax.inject.Inject;

import com.mongodb.client.model.Filters;
import com.nowellpoint.api.rest.domain.Organization;
import com.nowellpoint.api.rest.domain.ReferenceLink;
import com.nowellpoint.api.rest.domain.ReferenceLinkTypes;
import com.nowellpoint.api.rest.domain.Registration;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.service.OrganizationService;
import com.nowellpoint.api.service.PaymentGatewayService;
import com.nowellpoint.api.util.UserContext;

public class OrganizationServiceImpl extends AbstractOrganizationService implements OrganizationService {
	
	@Inject
	private PaymentGatewayService paymentGatewayService;
	
	@Override
	public Organization findById(String id) {
		return super.findById(id);
	}
	
	@Override
	public Organization findByDomain(String domain) {
		return super.query( Filters.eq( "domain", domain ));
	}

	@Override
	public Organization createOrganization(Registration registration) {
		
		UserInfo userInfo = UserInfo.of(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
		String customerId = addCustomer(
				registration.getDomain(), 
				registration.getEmail(), 
				registration.getFirstName(), 
				registration.getLastName(), 
				null);
		
		ReferenceLink referenceLink = ReferenceLink.of(ReferenceLinkTypes.CUSTOMER_ID, customerId);

		Organization organization = Organization.builder()
				.domain(registration.getDomain())
				.subscription(registration.getSubscription())
				.addReferenceLink(referenceLink)
				.createdBy(userInfo)
				.createdOn(now)
				.lastUpdatedBy(userInfo)
				.lastUpdatedOn(now)
				.build();
		
		super.create(organization);
		
		return organization;
	}

	@Override
	public Organization updateOrganization(String id, String domain) {
		
		Organization original = findById(id);
		
		Organization organization = Organization.builder()
				.from(original)
				.domain(domain)
				.build();
		
		super.update(organization);
		
		return organization;
	}
	
	@Override
	public Organization addCreditCard(String id, String cardholderName, String cardType, String expirationMonth, String expirationYear, String number, Boolean primary) {
		
		Organization original = findById(id);
		
		paymentGatewayService.addCreditCard(original.getId(), original.getBillingAddress().getId(), cardholderName, number, expirationMonth, expirationYear);
		
		Organization organization = Organization.builder()
				.from(original)
				.build();
		
		super.update(organization);
		
		return organization;
		
	}
	
	private String addCustomer(String company, String email, String firstName, String lastName, String phone) {
		return paymentGatewayService.addCustomer(company, email, firstName, lastName, phone).getTarget().getId();
	}
}