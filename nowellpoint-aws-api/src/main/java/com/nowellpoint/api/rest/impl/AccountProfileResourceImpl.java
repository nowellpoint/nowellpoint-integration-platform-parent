package com.nowellpoint.api.rest.impl;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.nowellpoint.api.model.document.Address;
import com.nowellpoint.api.model.document.Contact;
import com.nowellpoint.api.model.document.Photos;
import com.nowellpoint.api.rest.AccountProfileResource;
import com.nowellpoint.api.rest.AccountProfileService;
import com.nowellpoint.api.rest.PlanService;
import com.nowellpoint.api.rest.domain.AccountProfile;
import com.nowellpoint.api.rest.domain.CreditCard;
import com.nowellpoint.api.rest.domain.Plan;
import com.nowellpoint.api.rest.domain.Subscription;
import com.nowellpoint.util.Assert;

public class AccountProfileResourceImpl implements AccountProfileResource {
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@Inject
	private PlanService planService;

	@Context
	private UriInfo uriInfo;
	
	@Context 
	private SecurityContext securityContext;
	
	@Override
    public Response getAccountProfile(String id) {
		
		AccountProfile resource = accountProfileService.findById( id );
		
		String subject = securityContext.getUserPrincipal().getName();
		
		if (Assert.isNotEqual(id, subject)) {
			resource.setSubscription(null);
			resource.setCreditCards(null);
			resource.setHasFullAccess(null);
			resource.setEnableSalesforceLogin(null);
			resource.setTransactions(null);
		}
		
		return Response.ok(resource)
				.build();
	}
	
    @Override
	public Response getSubscription(String id) {
		
		Subscription subscription = accountProfileService.getSubscription( id );
		
		return Response.ok(subscription)
				.build();
	}
	
	@Override
	public Response setSubscription(String id, String planId, String paymentMethodToken) {
		
		Plan plan = planService.findById(planId);
		
		Subscription subscription = new Subscription();
		subscription.setPlanId(planId);
		subscription.setCurrencyIsoCode(plan.getPrice().getCurrencyIsoCode());
		subscription.setPlanCode(plan.getPlanCode());
		subscription.setUnitPrice(plan.getPrice().getUnitPrice());
		subscription.setPlanName(plan.getPlanName());
		subscription.setBillingFrequency(plan.getBillingFrequency());
		subscription.setCurrencySymbol(plan.getPrice().getCurrencySymbol());
		
		accountProfileService.setSubscription( id, paymentMethodToken, subscription );
		
		return Response.ok(subscription)
				.build();
	}
	
	@Override
	public Response getAddress(String id) {
		
		Address address = accountProfileService.getAddress( id );
		
		return Response.ok(address)
				.build();
	}
	
	@Override
	public Response updateAddress(
			String id, 
			String city,
			String countryCode,
			String postalCode,
			String state,
			String street) {
		
		Address address = new Address();
		address.setCity(city);
		address.setCountryCode(countryCode);
		address.setPostalCode(postalCode);
		address.setState(state);
		address.setStreet(street);
		
		accountProfileService.updateAddress( id, address);
		
		return Response.ok(address)
				.build();
	}
	
	@Override
	public Response udpateAccountProfile(
			String id,
			String firstName,
    		String lastName,
    		String company,
    		String division,
    		String department,
    		String title,
    		String email,
    		String fax,
    		String mobilePhone,
    		String phone,
    		String extension,
    		String localeSidKey,
    		String languageSidKey,
    		String timeZoneSidKey,
    		Boolean enableSalesforceLogin) {
		
		//
		// update account
		//
		
		AccountProfile accountProfile = AccountProfile.createAccountProfile();
		accountProfile.setId(id);
		accountProfile.setFirstName(firstName);
		accountProfile.setLastName(lastName);
		accountProfile.setEmail(email);
		accountProfile.setCompany(company);
		accountProfile.setDivision(division);
		accountProfile.setDepartment(department);
		accountProfile.setFax(fax);
		accountProfile.setTitle(title);
		accountProfile.setMobilePhone(mobilePhone);
		accountProfile.setPhone(phone);
		accountProfile.setExtension(extension);
		accountProfile.setLocaleSidKey(localeSidKey);
		accountProfile.setLanguageSidKey(languageSidKey);
		accountProfile.setTimeZoneSidKey(timeZoneSidKey);
		accountProfile.setEnableSalesforceLogin(enableSalesforceLogin);
		
		accountProfileService.updateAccountProfile( accountProfile );
		
		return Response.ok(accountProfile)
				.build();
	}

	@Override
    public Response createAccountProfile(AccountProfile accountProfile) {
		
		accountProfileService.createAccountProfile( accountProfile );
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(AccountProfileResource.class)
				.path("/{id}")
				.build(accountProfile.getId());
		
		return Response.created(uri)
				.build();
	}
	
    @Override
    public Response udpateAccountProfile(String id, AccountProfile accountProfile) {
		
		accountProfile.setId(id);
		accountProfileService.updateAccountProfile( accountProfile );
		
		return Response.ok(accountProfile).build();
	}
	
    @Override
    public Response deactivateAccountProfile(String id) {

		accountProfileService.deactivateAccountProfile( id );
		
		return Response.ok()
				.build();
	}
	
    @Override
	public Response getCreditCard(String id, String token) {
		
		CreditCard creditCard = accountProfileService.getCreditCard( id, token );
		
		if (creditCard == null) {
			throw new NotFoundException(String.format("Credit Card for token %s was not found", token));
		}
		
		return Response.ok(creditCard)
				.build();
	}
    
    @Override
    public Response setPrimary(@PathParam("id") String id, @PathParam("token") String token) {
    	
    	CreditCard creditCard = accountProfileService.setPrimary(id, token);
    	
    	if (creditCard == null) {
			throw new NotFoundException(String.format("Credit Card for token %s was not found", token));
		}
    	
    	return Response.ok(creditCard)
				.build();
    }
	
	@Override
	public Response removeCreditCard(String id, String token) {
		
		accountProfileService.removeCreditCard( id, token );
		
		return Response.ok()
				.build();
	}
	
	@Override
	public Response removeProfilePicture(String id) {
		
		AccountProfile accountProfile = accountProfileService.findById( id );
		
		AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
		
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest("nowellpoint-profile-pictures", accountProfile.getId().toString());
		
		s3Client.deleteObject(deleteObjectRequest);
		
		Photos photos = new Photos();
		photos.setProfilePicture("/images/person-generic.jpg");
		
		accountProfile.setPhotos(photos);
		
		accountProfileService.updateAccountProfile( accountProfile );
		
		return Response.ok(accountProfile)
				.build();
	}

	@Override
	public Response addCreditCard(String id, 
			String cardholderName, 
			String cvv, 
			String number, 
			String expirationMonth,
			String expirationYear, 
			Boolean primary, 
			String street, 
			String city, 
			String state, 
			String postalCode,
			String countryCode, 
			String firstName, 
			String lastName) {
		
		Contact billingContact = new Contact();
		billingContact.setFirstName(firstName);
		billingContact.setLastName(lastName);
		
		Address billingAddress = new Address();
		billingAddress.setCity(city);
		billingAddress.setCountryCode(countryCode);
		billingAddress.setPostalCode(postalCode);
		billingAddress.setState(state);
		billingAddress.setStreet(street);
		
		CreditCard creditCard = new CreditCard();
		creditCard.setCardholderName(cardholderName);
		creditCard.setCvv(cvv);
		creditCard.setNumber(number);
		creditCard.setExpirationMonth(expirationMonth);
		creditCard.setExpirationYear(expirationYear);
		creditCard.setPrimary(primary);
		creditCard.setBillingContact(billingContact);
		creditCard.setBillingAddress(billingAddress);
		
		accountProfileService.addCreditCard( id, creditCard );
		
		return Response.ok(creditCard)
				.build();
	}

	@Override
	public Response updateCreditCard(
			String id, 
			String token, 
			String cardholderName, 
			String cvv, 
			String expirationMonth,
			String expirationYear, 
			Boolean primary, 
			String street, 
			String city, 
			String state, 
			String postalCode,
			String countryCode, 
			String firstName, 
			String lastName) {
		
		Contact billingContact = new Contact();
		billingContact.setFirstName(firstName);
		billingContact.setLastName(lastName);
		
		Address billingAddress = new Address();
		billingAddress.setCity(city);
		billingAddress.setCountryCode(countryCode);
		billingAddress.setPostalCode(postalCode);
		billingAddress.setState(state);
		billingAddress.setStreet(street);
		
		CreditCard creditCard = new CreditCard();
		creditCard.setToken(token);
		creditCard.setCardholderName(cardholderName);
		creditCard.setExpirationMonth(expirationMonth);
		creditCard.setExpirationYear(expirationYear);
		creditCard.setPrimary(primary);
		creditCard.setBillingContact(billingContact);
		creditCard.setBillingAddress(billingAddress);
		
		accountProfileService.updateCreditCard( id, token, creditCard );
		
		return Response.ok(creditCard)
				.build();
	}
}