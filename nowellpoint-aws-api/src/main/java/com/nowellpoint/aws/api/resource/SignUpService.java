package com.nowellpoint.aws.api.resource;

import java.time.Instant;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.logging.Logger;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.EventAction;
import com.nowellpoint.aws.model.EventBuilder;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.data.Address;
import com.nowellpoint.aws.model.data.Identity;
import com.nowellpoint.aws.model.sforce.Lead;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

@Path("/signup")
public class SignUpService {
	
	private static final Logger LOGGER = Logger.getLogger(SignUpService.class);
	
	@Context
	private UriInfo uriInfo;
	
	@Context
	private HttpServletRequest servletRequest;
	
	private DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response signUp(
    		@FormParam("leadSource") @NotEmpty String leadSource,
    		@FormParam("firstName") String firstName,
    		@FormParam("lastName") @NotEmpty String lastName,
    		@FormParam("email") @Email String email,
    		@FormParam("phone") String phone,
    		@FormParam("company") String company,
    		@FormParam("title") String title,
    		@FormParam("countryCode") @NotEmpty String countryCode,
    		@FormParam("password") @Pattern.List({
    	        @Pattern(regexp = "(?=.*[0-9]).+", message = "Password must contain one digit."),
    	        @Pattern(regexp = "(?=.*[a-z]).+", message = "Password must contain one lowercase letter."),
    	        @Pattern(regexp = "(?=.*[a-z]).+", message = "Password must contain one upper letter."),
    	        @Pattern(regexp = "(?=.*[!@#$%^&*+=?-_()/\"\\.,<>~`;:]).+", message ="Password must contain one special character."),
    	        @Pattern(regexp = "(?=\\S+$).+", message = "Password must contain no whitespace.") }) String password) {
		
		//
		//
		//
		
		Event event = null;
		
		//
		// create lead
		//
		
		Lead lead = new Lead();
		lead.setLeadSource(leadSource);
		lead.setFirstName(firstName);
		lead.setLastName(lastName);
		lead.setEmail(email);
		lead.setPhone(phone);
		lead.setCompany(company);
		lead.setTitle(title);
		lead.setCountryCode(countryCode);

		try {			
			event = new EventBuilder()
					.withSubject(System.getProperty(Properties.DEFAULT_SUBJECT))
					.withEventAction(EventAction.ACTIVITY)
					.withEventSource(uriInfo.getBaseUri())
					.withPropertyStore(System.getenv("NCS_PROPERTY_STORE"))
					.withPayload(lead)
					.withType(Lead.class)
					.build();
				
			mapper.save(event);
				
		} catch (JsonProcessingException e) {
			LOGGER.error( "Parse Lead Exception", e.getCause() );
			throw new WebApplicationException( e, Status.BAD_REQUEST );
		}
		
		//
		// create account
		//
		
		Account account = new Account();
		account.setGivenName(firstName);
		account.setSurname(lastName);
		account.setEmail(email);
		account.setUsername(email);
		account.setPassword(password);
		account.setStatus("UNVERIFIED");
		
		try {			
			event = new EventBuilder()
					.withSubject(System.getProperty(Properties.DEFAULT_SUBJECT))
					.withEventAction(EventAction.CREATE)
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
		// create identity
		//
		
		Identity identity = new Identity();
		identity.setCreatedDate(Date.from(Instant.now()));
		identity.setLastModifiedDate(Date.from(Instant.now()));
		identity.setCreatedById(System.getProperty(Properties.DEFAULT_SUBJECT));
		identity.setLastModifiedById(System.getProperty(Properties.DEFAULT_SUBJECT));
		identity.setFirstName(firstName);
		identity.setLastName(lastName);
		identity.setEmail(email);
		identity.setUsername(email);
		identity.setName(firstName != null ? firstName.concat(" ").concat(lastName) : lastName);
		identity.setCompany(company);
		identity.setTitle(title);
		identity.setPhone(phone);
		
		Address address = new Address();
		address.setCountryCode(countryCode);
		
		identity.setAddress(address);
					
		try {			
			event = new EventBuilder()
					.withSubject(System.getProperty(Properties.DEFAULT_SUBJECT))
					.withEventAction(EventAction.SIGN_UP)
					.withEventSource(uriInfo.getBaseUri())
					.withPropertyStore(System.getenv("NCS_PROPERTY_STORE"))
					.withPayload(identity)
					.withType(Identity.class)
					.build();
				
			mapper.save( event );
				
		} catch (JsonProcessingException e) {
			LOGGER.error( "Parse Identity Exception", e.getCause() );
			throw new WebApplicationException( e, Status.BAD_REQUEST );
		}
		
		//
		//
		//
		
		return Response.ok().build();
	}
}