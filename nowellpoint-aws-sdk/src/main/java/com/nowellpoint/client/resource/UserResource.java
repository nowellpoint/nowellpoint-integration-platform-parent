package com.nowellpoint.client.resource;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.SignUpRequest;
import com.nowellpoint.client.model.SignUpResult;
import com.nowellpoint.client.model.User;

/**
 * @author jherson
 *
 */
public class UserResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "signup";
	
	public UserResource() {
		super();
	}
	
	public SignUpResult<User> signUp(SignUpRequest signUpRequest) {
		try {
			HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
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
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path("signup")
				.path("verify-email")
				.parameter("emailVerificationToken", emailVerificationToken)
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