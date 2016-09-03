package com.nowellpoint.api.resource;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.nowellpoint.api.dto.AccountProfileDTO;
import com.nowellpoint.api.dto.CreditCardDTO;
import com.nowellpoint.api.model.Address;
import com.nowellpoint.api.model.Photos;
import com.nowellpoint.api.service.AccountProfileService;
import com.nowellpoint.api.service.IdentityProviderService;
import com.nowellpoint.api.service.ServiceException;
import com.nowellpoint.aws.http.HttpRequestException;
import com.nowellpoint.aws.idp.model.Account;

@Path("/account-profile")
public class AccountProfileResource {
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@Inject
	private IdentityProviderService identityProviderService;

	@Context
	private UriInfo uriInfo;
	
	@Context 
	private SecurityContext securityContext;
	
	
	@GET
	@Path("/me")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserProfile() {
		String subject = securityContext.getUserPrincipal().getName();
		
		AccountProfileDTO resource = accountProfileService.findAccountProfileBySubject( subject );
		
		return Response.ok(resource)
				.build();
	}
	
	@GET
	@Path("{id}/address")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAccountProfileAddress(@PathParam("id") String id) {
		
		Address address = accountProfileService.getAccountProfileAddress(id);
		
		return Response.ok(address)
				.build();
	}
	
	@POST
	@Path("{id}/address")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateAccountProfileAddress(@PathParam("id") String id, Address address) {
		
		accountProfileService.updateAccountProfileAddress(id, address);
		
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
    		@FormParam("email") @Email String email,
    		@FormParam("fax") String fax,
    		@FormParam("mobilePhone") String mobilePhone,
    		@FormParam("phone") String phone,
    		@FormParam("extension") String extension,
    		@FormParam("localeSidKey") String localeSidKey,
    		@FormParam("languageSidKey") String languageSidKey,
    		@FormParam("timeZoneSidKey") String timeZoneSidKey) {
				
		String subject = securityContext.getUserPrincipal().getName();
				
		//
		// update account
		//
		
		
		Account account = new Account();
		account.setGivenName(firstName);
		account.setMiddleName(null);
		account.setSurname(lastName);
		account.setEmail(email);
		account.setUsername(email);
		account.setHref(subject);

		try {
			identityProviderService.updateAccount(account);
		} catch (HttpRequestException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
		
		//
		// update identity
		//
		
		AccountProfileDTO resource = new AccountProfileDTO();
		resource.setId(id);
		resource.setFirstName(firstName);
		resource.setLastName(lastName);
		resource.setEmail(email);
		resource.setCompany(company);
		resource.setDivision(division);
		resource.setDepartment(department);
		resource.setFax(fax);
		resource.setTitle(title);
		resource.setMobilePhone(mobilePhone);
		resource.setPhone(phone);
		resource.setExtension(extension);
		resource.setLocaleSidKey(localeSidKey);
		resource.setLanguageSidKey(languageSidKey);
		resource.setTimeZoneSidKey(timeZoneSidKey);
		
		accountProfileService.updateAccountProfile(resource);
		
		return Response.ok(resource)
				.build();
	}
	
	@GET
	@Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountProfile(@PathParam("id") String id) {
		
		AccountProfileDTO resource = accountProfileService.findAccountProfile( id );
		
		return Response.ok(resource)
				.build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccountProfile(AccountProfileDTO resource) {
		
		accountProfileService.createAccountProfile( resource );
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(AccountProfileResource.class)
				.path("/{id}")
				.build(resource.getId());
		
		return Response.created(uri).build();
	}
	
	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response udpateAccountProfile(@PathParam("id") String id, AccountProfileDTO resource) {
		
		resource.setId(id);
		
		accountProfileService.updateAccountProfile(resource);
		
		return Response.ok(resource).build();
	}
	
	@DELETE
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response disableAccountProfile(@PathParam("id") String id) {
		
		AccountProfileDTO resource = accountProfileService.findAccountProfile( id );
		
		try {
			identityProviderService.disableAccount(resource.getHref());
		} catch (ServiceException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return Response.ok().build();
	}
	
	@GET
	@Path("{id}/credit-card/{token}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCreditCard(@PathParam("id") String id, @PathParam("token") String token) {
		
		CreditCardDTO resource = accountProfileService.getCreditCard(id, token);
		
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
	public Response addCreditCard(@PathParam("id") String id, CreditCardDTO resource) {
		
		try {
			accountProfileService.addCreditCard(id, resource);
		} catch (ServiceException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return Response
				.ok(resource)
				.build();
	}
	
	@POST
	@Path("{id}/credit-card/{token}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCreditCard(@PathParam("id") String id, @PathParam("token") String token, MultivaluedMap<String, String> parameters) {
		
		CreditCardDTO resource = null;
		try {
			resource = accountProfileService.updateCreditCard(id, token, parameters);
		} catch (ServiceException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return Response
				.ok(resource)
				.build();
	}
	
	@PUT
	@Path("{id}/credit-card/{token}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCreditCard(@PathParam("id") String id, @PathParam("token") String token, CreditCardDTO resource) {
		
		try {
			accountProfileService.updateCreditCard(id, token, resource);
		} catch (ServiceException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return Response
				.ok(resource)
				.build();
	}
	
	@DELETE
	@Path("{id}/credit-card/{token}")
	public Response removeCreditCard(@PathParam("id") String id, @PathParam("token") String token) {
		
		try {
			accountProfileService.removeCreditCard(id, token);
		} catch (ServiceException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return Response
				.ok()
				.build();
	}
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountProfileBySubject(@QueryParam("subject") String subject) {		
		AccountProfileDTO resource = accountProfileService.findAccountProfileBySubject( subject );
		
		if (resource == null) {
			throw new WebApplicationException( String.format( "Account Profile for subject: %s does not exist or you do not have access to view", subject ), Status.NOT_FOUND );
		}
		
		return Response.ok(resource)
				.build();
	}
	
	@DELETE
	@Path("{id}/photo")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response removeProfilePicture(@PathParam("id") String id) {
		
		AccountProfileDTO resource = accountProfileService.findAccountProfile(id);
		
		AmazonS3 s3Client = new AmazonS3Client();
		
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest("nowellpoint-profile-pictures", resource.getId());
		
		s3Client.deleteObject(deleteObjectRequest);
		
		Photos photos = new Photos();
		photos.setProfilePicture("/images/person-generic.jpg");
		
		resource.setPhotos(photos);
		
		accountProfileService.updateAccountProfile(resource);
		
		return Response.ok(resource)
				.build();
	}
}