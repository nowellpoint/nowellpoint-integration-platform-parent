package com.nowellpoint.api.rest.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.rest.OrganizationResource;
import com.nowellpoint.api.rest.domain.Organization;
import com.nowellpoint.api.service.OrganizationService;

public class OrganizationResourceImpl implements OrganizationResource {
	
	@Inject
	private OrganizationService organizationService;

	@Override
	public Response getOrganization(String id) {
		Organization organization = organizationService.findById(id);
		return Response.ok(organization)
				.build();
	}
	
	@Override
	public Response changePlan(String id, String planId) {
		Organization organization = organizationService.changePlan(id, planId);
		return Response.ok(organization)
				.build();
	}

	@Override
	public Response updateCreditCard(String id, String cardholderName, String expirationMonth, String expirationYear, String number, String cvv) {
		Organization organization = organizationService.updateCreditCard(id, cardholderName, expirationMonth, expirationYear, number, cvv);
		return Response.ok(organization)
				.build();
	}

	@Override
	public Response updateBillingAddress(String id, String street, String city, String stateCode, String postalCode, String countryCode) {
		Organization organization = organizationService.updateBillingAddress(id, street, city, stateCode, postalCode, countryCode);
		return Response.ok(organization)
				.build();
	}

	@Override
	public Response updateBillingContact(String id, String firstName, String lastName, String email, String phone) {
		Organization organization = organizationService.updateBillingContact(id, firstName, lastName, email, phone);
		return Response.ok(organization)
				.build();
	}
}