package com.nowellpoint.api.resource;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.nowellpoint.api.model.document.Address;
import com.nowellpoint.api.model.document.Photos;
import com.nowellpoint.api.model.domain.AccountProfile;
import com.nowellpoint.api.model.domain.CreditCard;
import com.nowellpoint.api.model.domain.Plan;
import com.nowellpoint.api.model.domain.Subscription;
import com.nowellpoint.api.service.AccountProfileService;
import com.nowellpoint.api.service.IdentityProviderService;
import com.nowellpoint.api.service.PlanService;

@Path("account-profile")
public class AccountProfileResource {
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@Inject
	private IdentityProviderService identityProviderService;
	
	@Inject
	private PlanService planService;

	@Context
	private UriInfo uriInfo;
	
	@Context 
	private SecurityContext securityContext;
	
	@GET
	@Path("me")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAccountProfile() {
		String subject = securityContext.getUserPrincipal().getName();
		
		AccountProfile accountProfile = accountProfileService.findAccountProfile( subject );
				
		return Response.ok(accountProfile)
				.build();
	}
	
	@GET
	@Path("{id}/subscription")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSubscription(@PathParam("id") String id) {
		
		Subscription subscription = accountProfileService.getSubscription( id );
		
		return Response.ok(subscription)
				.build();
	}
	
	@POST
	@Path("{id}/subscription")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setSubscription(@PathParam("id") String id, @FormParam(value = "planId") String planId, @FormParam(value = "paymentMethodToken") String paymentMethodToken) {
		
		Plan plan = planService.findPlan(planId);
		
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
	
	@GET
	@Path("{id}/address")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAddress(@PathParam("id") String id) {
		
		Address address = accountProfileService.getAddress( id );
		
		return Response.ok(address)
				.build();
	}
	
	@POST
	@Path("{id}/address")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateAddress(
			@PathParam("id") String id, 
			@FormParam("city") String city,
			@FormParam("countryCode") String countryCode,
			@FormParam("postalCode") String postalCode,
			@FormParam("state") String state,
			@FormParam("street") String street) {
		
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
	
	@POST
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response udpateAccountProfile(
			@PathParam("id") String id,
			@FormParam("firstName") String firstName,
    		@FormParam("lastName") @NotEmpty String lastName,
    		@FormParam("company") String company,
    		@FormParam("division") String division,
    		@FormParam("department") String department,
    		@FormParam("title") String title,
    		@FormParam("email") @Email @NotEmpty String email,
    		@FormParam("fax") String fax,
    		@FormParam("mobilePhone") String mobilePhone,
    		@FormParam("phone") String phone,
    		@FormParam("extension") String extension,
    		@FormParam("localeSidKey") @NotEmpty String localeSidKey,
    		@FormParam("languageSidKey") @NotEmpty String languageSidKey,
    		@FormParam("timeZoneSidKey") @NotEmpty String timeZoneSidKey,
    		@FormParam("enableSalesforceLogin") Boolean enableSalesforceLogin) {
		
		//
		// update account
		//
		
		AccountProfile accountProfile = new AccountProfile();
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
		
		identityProviderService.updateAccount(accountProfile.getHref(), email, firstName, lastName);
		
		return Response.ok(accountProfile)
				.build();
	}
	
	@GET
	@Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountProfile(@PathParam("id") String id) {
		
		AccountProfile resource = accountProfileService.findAccountProfile( id );
		
		String subject = securityContext.getUserPrincipal().getName();
		
		if (! subject.equals(resource.getId())) {
			resource.setCreditCards(null);
			resource.setHasFullAccess(null);
			resource.setEnableSalesforceLogin(null);
		} 
		
		return Response.ok(resource)
				.build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccountProfile(AccountProfile accountProfile) {
		
		accountProfileService.createAccountProfile( accountProfile );
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(AccountProfileResource.class)
				.path("/{id}")
				.build(accountProfile.getId());
		
		return Response.created(uri).build();
	}
	
	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response udpateAccountProfile(@PathParam("id") String id, AccountProfile accountProfile) {
		
		accountProfile.setId(id);
		accountProfileService.updateAccountProfile( accountProfile );
		
		return Response.ok(accountProfile).build();
	}
	
	@DELETE
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deactivateAccountProfile(@PathParam("id") String id) {
		
		AccountProfile resource = new AccountProfile(id);
		accountProfileService.deactivateAccountProfile( resource );
		
		identityProviderService.deactivateUser(resource.getHref());
		
		return Response.ok().build();
	}
	
	@GET
	@Path("{id}/credit-card/{token}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCreditCard(@PathParam("id") String id, @PathParam("token") String token) {
		
		CreditCard resource = accountProfileService.getCreditCard( id, token);
		
		if (resource == null) {
			throw new NotFoundException(String.format("Credit Card for token %s was not found", token));
		}
		
		return Response
				.ok(resource)
				.build();
	}
	
	@POST
	@Path("{id}/credit-card")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addCreditCard(@PathParam("id") String id, CreditCard creditCard) {
		
		accountProfileService.addCreditCard( id, creditCard);
		
		return Response
				.ok(creditCard)
				.build();
	}
	
	@POST
	@Path("{id}/credit-card/{token}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCreditCard(@PathParam("id") String id, @PathParam("token") String token, MultivaluedMap<String, String> parameters) {
		
		CreditCard resource = accountProfileService.updateCreditCard( id, token, parameters );
		
		return Response
				.ok(resource)
				.build();
	}
	
	@PUT
	@Path("{id}/credit-card/{token}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCreditCard(@PathParam("id") String id, @PathParam("token") String token, CreditCard creditCard) {
		
		accountProfileService.updateCreditCard( id, token, creditCard );
		
		return Response
				.ok(creditCard)
				.build();
	}
	
	@DELETE
	@Path("{id}/credit-card/{token}")
	public Response removeCreditCard(@PathParam("id") String id, @PathParam("token") String token) {
		
		accountProfileService.removeCreditCard( id, token );
		
		return Response
				.ok()
				.build();
	}
	
	@DELETE
	@Path("{id}/photo")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response removeProfilePicture(@PathParam("id") String id) {
		
		AccountProfile accountProfile = accountProfileService.findAccountProfile( id );
		
		AmazonS3 s3Client = new AmazonS3Client();
		
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest("nowellpoint-profile-pictures", accountProfile.getId().toString());
		
		s3Client.deleteObject(deleteObjectRequest);
		
		Photos photos = new Photos();
		photos.setProfilePicture("/images/person-generic.jpg");
		
		accountProfile.setPhotos(photos);
		
		accountProfileService.updateAccountProfile( accountProfile );
		
		return Response.ok(accountProfile)
				.build();
	}
}