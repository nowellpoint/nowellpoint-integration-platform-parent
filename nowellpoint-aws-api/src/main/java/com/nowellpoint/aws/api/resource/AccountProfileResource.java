package com.nowellpoint.aws.api.resource;

import java.io.IOException;
import java.net.URI;

import javax.annotation.security.PermitAll;
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
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.nowellpoint.aws.api.dto.AccountProfileDTO;
import com.nowellpoint.aws.api.model.Address;
import com.nowellpoint.aws.api.model.CreditCard;
import com.nowellpoint.aws.api.model.Photos;
import com.nowellpoint.aws.api.service.AccountProfileService;
import com.nowellpoint.aws.api.service.IdentityProviderService;
import com.nowellpoint.aws.api.service.ServiceException;
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
	
	@PUT
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response udpateAccountProfile(
			@FormParam("id") @NotEmpty String id,
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
    		@FormParam("street") String street,
    		@FormParam("city") String city,
    		@FormParam("state") String state,
    		@FormParam("postalCode") String postalCode,
    		@FormParam("countryCode") @NotEmpty String countryCode) {
				
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
		resource.setSubject(subject);
		
		Address address = new Address();
		address.setStreet(street);
		address.setCity(city);
		address.setState(state);
		address.setPostalCode(postalCode);
		address.setCountryCode(countryCode);
		
		resource.setAddress(address);
		
		accountProfileService.updateAccountProfile(resource);
		
		return Response.ok(resource)
				.build();
	}
	
	/**
	 * @api {get} /identity/:id/picture Get Profile Picture
	 * @apiName getPicture
	 * @apiVersion 1.0.0
	 * @apiGroup Identity
	 * 
	 * @apiParam {String} id The Identity's unique id
	 */
	
	@GET
	@Path("/{id}/picture")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	@PermitAll
	public Response getPicture(@PathParam(value="id") String id) {
		
		AmazonS3 s3Client = new AmazonS3Client();
		
		GetObjectRequest getObjectRequest = new GetObjectRequest("aws-microservices", id);
    	
    	S3Object image = s3Client.getObject(getObjectRequest);
    	
    	String contentType = image.getObjectMetadata().getContentType();
    	
    	byte[] bytes = null;
    	try {
    		bytes = IOUtils.toByteArray(image.getObjectContent());    		
		} catch (IOException e) {
			throw new WebApplicationException( e.getMessage(), Status.INTERNAL_SERVER_ERROR );
		} finally {
			try {
				image.close();
			} catch (IOException ignore) {

			}
		}
    	
    	return Response.ok().entity(bytes)
    			.header("Content-Disposition", "inline; filename=\"" + id + "\"")
    			.header("Content-Length", bytes.length)
    			.header("Content-Type", contentType)
    			.build();
	}
	
	/**
	 * @api {get} /identity/:id Get Identity
	 * @apiName getIdentity
	 * @apiVersion 1.0.0
	 * @apiGroup Identity
	 * @apiHeader {String} authorization Authorization with the value of Bearer access_token from authenticate
	 * 
	 * @apiParam {String} id The Identity's unique id
	 */
	
	@GET
	@Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccountProfile(@PathParam("id") String id) {
		String subject = securityContext.getUserPrincipal().getName();
		
		AccountProfileDTO resource = accountProfileService.findAccountProfile( id, subject );
		
		return Response.ok(resource)
				.build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAccountProfile(AccountProfileDTO resource) {
		String subject = securityContext.getUserPrincipal().getName();
		
		resource.setSubject(subject);
		
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
		String subject = securityContext.getUserPrincipal().getName();
		
		resource.setId(id);
		resource.setSubject(subject);
		
		accountProfileService.updateAccountProfile(resource);
		
		return Response.ok(resource).build();
	}
	
	@DELETE
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response disableAccountProfile(@PathParam("id") String id) {
		String subject = securityContext.getUserPrincipal().getName();
		
		AccountProfileDTO resource = accountProfileService.findAccountProfile( id, subject );
		
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
		String subject = securityContext.getUserPrincipal().getName();
		
		CreditCard creditCard = accountProfileService.getCreditCard(subject, id, token);
		
		if (creditCard == null) {
			throw new NotFoundException(String.format("Credit Card for token %s was not found",token));
		}
		
		return Response
				.ok(creditCard)
				.build();
	}
	
	@POST
	@Path("{id}/credit-card")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addCreditCard(@PathParam("id") String id, CreditCard creditCard) {
		String subject = securityContext.getUserPrincipal().getName();
		
		try {
			accountProfileService.addCreditCard(subject, id, creditCard);
		} catch (ServiceException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return Response
				.ok(creditCard)
				.build();
	}
	
	@POST
	@Path("{id}/credit-card/{token}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCreditCard(@PathParam("id") String id, @PathParam("token") String token, MultivaluedMap<String, String> parameters) {
		String subject = securityContext.getUserPrincipal().getName();
		
		CreditCard creditCard = null;
		try {
			creditCard = accountProfileService.updateCreditCard(subject, id, token, parameters);
		} catch (ServiceException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return Response
				.ok(creditCard)
				.build();
	}
	
	@PUT
	@Path("{id}/credit-card/{token}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCreditCard(@PathParam("id") String id, @PathParam("token") String token, CreditCard creditCard) {
		String subject = securityContext.getUserPrincipal().getName();
		
		try {
			accountProfileService.updateCreditCard(subject, id, token, creditCard);
		} catch (ServiceException e) {
			throw new BadRequestException(e.getMessage());
		}
		
		return Response
				.ok(creditCard)
				.build();
	}
	
	@DELETE
	@Path("{id}/credit-card/{token}")
	public Response removeCreditCard(@PathParam("id") String id, @PathParam("token") String token) {
		String subject = securityContext.getUserPrincipal().getName();
		
		try {
			accountProfileService.removeCreditCard(subject, id, token);
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
		
		return Response.ok(resource)
				.build();
	}
	
	@DELETE
	@Path("{id}/photo")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response removeProfilePicture(@PathParam("id") String id) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		AccountProfileDTO resource = accountProfileService.findAccountProfile(id, subject);
		
		AmazonS3 s3Client = new AmazonS3Client();
		
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest("aws-microservices", resource.getId());
		
		s3Client.deleteObject(deleteObjectRequest);
		
		Photos photos = new Photos();
		photos.setProfilePicture("/images/person-generic.jpg");
		
		resource.setSubject(subject);
		resource.setPhotos(photos);
		
		accountProfileService.updateAccountProfile(resource);
		
		return Response.ok(resource)
				.build();
	}
}