package com.nowellpoint.client.resource;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.nowellpoint.client.Environment;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.SignUpRequest;
import com.nowellpoint.client.model.SignUpResult;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.User;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

/**
 * @author jherson
 *
 */
public class UserResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "signup";
	
	public UserResource(Token token) {
		super(token);
	}
	
	public SignUpResult<User> signUp(SignUpRequest signUpRequest) {
		try {
			HttpResponse httpResponse = RestResource.post(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")).getEnvironmentUrl())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.acceptCharset("UTF-8")
					.path(RESOURCE_CONTEXT)
					.parameter("firstName", URLEncoder.encode(signUpRequest.getFirstName(), "UTF-8"))
					.parameter("lastName", URLEncoder.encode(signUpRequest.getLastName(), "UTF-8"))
					.parameter("email", signUpRequest.getEmail())
					.parameter("countryCode", signUpRequest.getCountryCode())
					.parameter("password", URLEncoder.encode(signUpRequest.getPassword(), "UTF-8"))
					.parameter("confirmPassword", URLEncoder.encode(signUpRequest.getConfirmPassword(), "UTF-8"))
					.parameter("planId", signUpRequest.getPlanId())
					.parameter("cardNumber", signUpRequest.getCardNumber())
					.parameter("expirationMonth", signUpRequest.getExpirationMonth())
					.parameter("expirationYear", signUpRequest.getExpirationYear())
					.parameter("securityCode", signUpRequest.getSecurityCode())
					.execute();
			
			SignUpResult<User> result = null;
	    	
	    	if (httpResponse.getStatusCode() == Status.OK) {
	    		User resource = httpResponse.getEntity(User.class);
	    		result = new SignUpResultImpl<User>(resource); 
	    	} else {
	    		Error error = httpResponse.getEntity(Error.class);
				result = new SignUpResultImpl<User>(error);
	    	}
	    	
	    	return result;
	    	
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public SignUpResult<User> verifyEmail(String emailVerificationToken) {
		HttpResponse httpResponse = RestResource.post(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")).getEnvironmentUrl())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path("signup")
				.path("verify-email")
				.path(emailVerificationToken)
				.execute();
		
		SignUpResult<User> result = null;
    	
    	if (httpResponse.getStatusCode() == Status.OK) {
    		User resource = httpResponse.getEntity(User.class);
    		result = new SignUpResultImpl<User>(resource); 
    	} else {
    		Error error = httpResponse.getEntity(Error.class);
			result = new SignUpResultImpl<User>(error);
    	}
    	
    	return result;
	}
}