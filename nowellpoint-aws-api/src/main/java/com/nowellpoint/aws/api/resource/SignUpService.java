package com.nowellpoint.aws.api.resource;

import javax.annotation.security.PermitAll;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.data.dynamodb.Event;
import com.nowellpoint.aws.data.dynamodb.EventAction;
import com.nowellpoint.aws.data.dynamodb.EventBuilder;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

@Path("/signup")
public class SignUpService {
	
	private static final Logger LOGGER = Logger.getLogger(SignUpService.class);
	
	@Context
	private UriInfo uriInfo;
	
	private DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();

	@PermitAll
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response signUp(
    		@FormParam("leadSource") @NotEmpty String leadSource,
    		@FormParam("firstName") String firstName,
    		@FormParam("lastName") @NotEmpty(message="Last Name must be filled in") String lastName,
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
		
		ObjectNode signUp = new ObjectMapper().createObjectNode()
				.put("leadSource", leadSource)
				.put("firstName", firstName)
				.put("lastName", lastName)
				.put("email", email)
				.put("phone", phone)
				.put("company", company)
				.put("title", title)
				.put("countryCode", countryCode)
				.put("password", password)
				.put("username", email)
				.putNull("middleName");
		
		try {			
			Event event = new EventBuilder()
					.withSubject(System.getProperty(Properties.DEFAULT_SUBJECT))
					.withEventAction(EventAction.SIGN_UP)
					.withEventSource(uriInfo.getBaseUri())
					.withPropertyStore(System.getenv("NCS_PROPERTY_STORE"))
					.withPayload(signUp)
					.withType(ObjectNode.class)
					.build();
				
			mapper.save(event);
				
		} catch (JsonProcessingException e) {
			LOGGER.error( "Signup Exception", e.getCause() );
			throw new WebApplicationException( e, Status.BAD_REQUEST );
		}
		
		return Response.ok().build();
	}
}