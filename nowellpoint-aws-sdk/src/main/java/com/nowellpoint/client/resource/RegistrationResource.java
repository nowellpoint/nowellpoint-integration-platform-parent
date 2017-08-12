package com.nowellpoint.client.resource;

import com.nowellpoint.client.Environment;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.Registration;
import com.nowellpoint.client.model.SignUpRequest;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;

public class RegistrationResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "signup";

	/**
	 * 
	 * @param environment
	 */
	
	public RegistrationResource(Environment environment) {
		super(environment);
	}
	
	public CreateResult<Registration> signUp(SignUpRequest signUpRequest) {
		HttpResponse httpResponse = RestResource.post(environment.getEnvironmentUrl())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path(RESOURCE_CONTEXT)
				.parameter("firstName", signUpRequest.getFirstName())
				.parameter("lastName", signUpRequest.getLastName())
				.parameter("email", signUpRequest.getEmail())
				.parameter("countryCode", signUpRequest.getCountryCode())
				.parameter("domain", signUpRequest.getDomain())
				.parameter("planId", signUpRequest.getDomain())
    			.execute();
		
		CreateResult<Registration> result = null;
    	
    	if (httpResponse.getStatusCode() == Status.CREATED) {
    		Registration resource = httpResponse.getEntity(Registration.class);
    		result = new CreateResultImpl<Registration>(resource);
    	} else {
    		Error error = httpResponse.getEntity(Error.class);
    		result = new CreateResultImpl<Registration>(error);
    	}
    	
    	return result;
	}
}