package com.nowellpoint.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("organizations")
public interface OrganizationResource {

	@GET
	@Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getOrganization(
			@PathParam("id") String id);
	
	@DELETE
	@Path("{id}")
	public Response deleteOrganization(@PathParam("id") String id);
	
	@POST
	@Path("{id}/subscription")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response changePlan(
			@PathParam("id") String id, 
			@FormParam("planId") String planId,
			@FormParam("number") String number, 
			@FormParam("cardholderName") String cardholderName, 
			@FormParam("expirationMonth") String expirationMonth, 
			@FormParam("expirationYear") String expirationYear,
			@FormParam("cvv") String cvv);
	
	@POST
	@Path("{id}/subscription/credit-card")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCreditCard(
			@PathParam("id") String id, 
			@FormParam("cardholderName") String cardholderName,
			@FormParam("expirationMonth") String expirationMonth,
			@FormParam("expirationYear") String expirationYear,
			@FormParam("number") String number,
			@FormParam("cvv") String cvv);
	
	@DELETE
	@Path("{id}/subscription/credit-card")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeCreditCard(@PathParam("id") String id);
	
	@POST
	@Path("{id}/subscription/billing-address")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateBillingAddress(
			@PathParam("id") String id, 
			@FormParam("street") String street,
			@FormParam("city") String city,
			@FormParam("state") String state,
			@FormParam("postalCode") String postalCode,
			@FormParam("countryCode") String countryCode);
	
	@POST
	@Path("{id}/subscription/billing-contact")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateBillingContact(
			@PathParam("id") String id, 
			@FormParam("firstName") String firstName,
			@FormParam("lastName") String lastName,
			@FormParam("email") String email,
			@FormParam("phone") String phone);
	
	@GET
	@Path("{id}/invoice/{invoiceNumber}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getInvoice(@PathParam("id") String id, @PathParam("invoiceNumber") String invoiceNumber);
}