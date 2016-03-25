package com.nowellpoint.aws.api.resource;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.nowellpoint.aws.api.dto.IdentityDTO;
import com.nowellpoint.aws.api.dto.idp.AccountDTO;
import com.nowellpoint.aws.api.service.IdentityProviderService;
import com.nowellpoint.aws.api.service.IdentityService;
import com.nowellpoint.aws.data.mongodb.Address;
import com.nowellpoint.aws.data.mongodb.Photos;

@Path("/user-profile")
public class UserProfileService {
	
	@Inject
	private IdentityService identityService;
	
	@Inject
	private IdentityProviderService identityProviderService;
	
	@Context
	private UriInfo uriInfo;
	
	@Context
	private SecurityContext securityContext;
	
	@PUT
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response udpate(
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
		
		
		AccountDTO account = new AccountDTO();
		account.setGivenName(firstName);
		account.setMiddleName(null);
		account.setSurname(lastName);
		account.setEmail(email);
		account.setUsername(email);
		account.setHref(subject);

		identityProviderService.updateAccount(account);
		
		//
		// update identity
		//
		
		IdentityDTO resource = new IdentityDTO();
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
		
		Address address = new Address();
		address.setStreet(street);
		address.setCity(city);
		address.setState(state);
		address.setPostalCode(postalCode);
		address.setCountryCode(countryCode);
		
		resource.setAddress(address);
		
		identityService.updateIdentity( subject, resource, uriInfo.getBaseUri() );
		
		return Response.ok(resource)
				.build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserProfile() {
		String subject = securityContext.getUserPrincipal().getName();
		
		IdentityDTO resource = identityService.findIdentityBySubject( subject );
		
		return Response.ok(resource)
				.build();
	}
	
	@DELETE
	@Path("/photo")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response removeProfilePicture() {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		IdentityDTO resource = identityService.findIdentityBySubject( subject );
		
		AmazonS3 s3Client = new AmazonS3Client();
		
		DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest("aws-microservices", resource.getId());
		
		s3Client.deleteObject(deleteObjectRequest);
		
		Photos photos = new Photos();
		photos.setProfilePicture("/images/person-generic.jpg");
		
		resource.setPhotos(photos);
		
		identityService.updateIdentity(subject, resource, uriInfo.getBaseUri());
		
		return Response.ok(resource)
				.build();
	}
}