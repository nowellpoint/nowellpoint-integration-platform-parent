package com.nowellpoint.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.nowellpoint.api.rest.domain.AccountProfile;

@Path("account-profile")
public interface AccountProfileResource {
	
	@GET
	@Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountProfile(@PathParam("id") String id);
	
	@GET
	@Path("{id}/address")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getAddress(@PathParam("id") String id);
	
	@GET
	@Path("{id}/subscription")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSubscription(@PathParam("id") String id);
	
	@GET
	@Path("{id}/credit-card/{token}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCreditCard(
			@PathParam("id") String id, 
			@PathParam("token") String token);
	
	@POST
	@Path("{id}/subscription")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response setSubscription(
			@PathParam("id") String id, 
			@FormParam(value = "planId") String planId, 
			@FormParam(value = "paymentMethodToken") String paymentMethodToken);
	
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
			@FormParam("street") String street);
	
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
    		@FormParam("enableSalesforceLogin") Boolean enableSalesforceLogin);

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccountProfile(AccountProfile accountProfile);
	
	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response udpateAccountProfile(@PathParam("id") String id, AccountProfile accountProfile);
	
	@DELETE
	@Path("{id}")
    public Response deactivateAccountProfile(@PathParam("id") String id);
	
	@POST
	@Path("{id}/credit-card")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addCreditCard(
			@PathParam("id") String id, 
			@FormParam("cardholderName") String cardholderName,
			@FormParam("cvv") String cvv,
			@FormParam("number") String number,
			@FormParam("expirationMonth") String expirationMonth,
			@FormParam("expirationYear") String expirationYear,
			@FormParam("primary") Boolean primary,
			@FormParam("street") String street,
			@FormParam("city") String city,
			@FormParam("state") String state,
			@FormParam("postalCode") String postalCode,
			@FormParam("countryCode") String countryCode,
			@FormParam("firstName") String firstName,
			@FormParam("lastName") String lastName);
	
	@POST
	@Path("{id}/credit-card/{token}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCreditCard(
			@PathParam("id") String id, 
			@PathParam("token") String token, 
			@FormParam("cardholderName") String cardholderName,
			@FormParam("cvv") String cvv,
			@FormParam("expirationMonth") String expirationMonth,
			@FormParam("expirationYear") String expirationYear,
			@FormParam("primary") Boolean primary,
			@FormParam("street") String street,
			@FormParam("city") String city,
			@FormParam("state") String state,
			@FormParam("postalCode") String postalCode,
			@FormParam("countryCode") String countryCode,
			@FormParam("firstName") String firstName,
			@FormParam("lastName") String lastName);
	
	@POST
	@Path("{id}/credit-card/{token}/primary")
	@Produces(MediaType.APPLICATION_JSON)
	public Response setPrimary(@PathParam("id") String id, @PathParam("token") String token);
	
	@GET
	@Path("{id}/invoice/{invoiceNumber}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getInvoice(@PathParam("id") String id, @PathParam("invoiceNumber") String invoiceNumber);
	
	@DELETE
	@Path("{id}/credit-card/{token}")
	public Response removeCreditCard(@PathParam("id") String id, @PathParam("token") String token);
	
	@DELETE
	@Path("{id}/photo")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeProfilePicture(@PathParam("id") String id);
}