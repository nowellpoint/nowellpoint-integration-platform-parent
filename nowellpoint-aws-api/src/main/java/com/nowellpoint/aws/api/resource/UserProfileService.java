package com.nowellpoint.aws.api.resource;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.logging.Logger;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nowellpoint.aws.api.dto.IdentityDTO;
import com.nowellpoint.aws.api.service.IdentityService;
import com.nowellpoint.aws.api.util.HttpServletRequestUtil;
import com.nowellpoint.aws.data.dynamodb.Event;
import com.nowellpoint.aws.data.dynamodb.EventAction;
import com.nowellpoint.aws.data.dynamodb.EventBuilder;
import com.nowellpoint.aws.data.model.Address;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

@Path("/user-profile")
public class UserProfileService {
	
	private static final Logger LOGGER = Logger.getLogger(UserProfileService.class);
	
	@Inject
	private IdentityService identityService;
	
	@Context
	private UriInfo uriInfo;
	
	@Context
	private HttpServletRequest servletRequest;
	
	private DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();
	
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
		
		//
		//
		//
		
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		//
		//
		//
		
		Event event = null;
				
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
			event = new EventBuilder()
					.withSubject(System.getProperty(Properties.DEFAULT_SUBJECT))
					.withEventAction(EventAction.UPDATE)
					.withEventSource(uriInfo.getRequestUri())
					.withPayload(account)
					.withPropertyStore(System.getenv("NCS_PROPERTY_STORE"))
					.withType(Account.class)
					.build();
			
			mapper.save( event );
			
		} catch (JsonProcessingException e) {
			LOGGER.error( "Parse Account Exception", e.getCause() );
			throw new WebApplicationException( e, Status.BAD_REQUEST );
		}
		
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
		
		//
		//
		//
		
		return Response.ok(resource).build();
	}
}