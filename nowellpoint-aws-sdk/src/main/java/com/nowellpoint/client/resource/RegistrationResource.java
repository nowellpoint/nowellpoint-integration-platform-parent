package com.nowellpoint.client.resource;

import com.nowellpoint.client.Environment;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.ProvisionRequesst;
import com.nowellpoint.client.model.Registration;
import com.nowellpoint.client.model.SignUpRequest;
import com.nowellpoint.client.model.UpdateRegistrationRequest;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.exception.NotFoundException;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
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
	
	public Registration get(String id) {
		HttpResponse httpResponse = RestResource.get(environment.getEnvironmentUrl()).accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(id)
				.execute();

		Registration resource = null;

		if (httpResponse.getStatusCode() == Status.OK) {
			resource = httpResponse.getEntity(Registration.class);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
		}

		return resource;
	}
	
	public CreateResult<Registration> signUp(SignUpRequest request) {
		HttpResponse httpResponse = RestResource.post(environment.getEnvironmentUrl())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.parameter("firstName", request.getFirstName())
				.parameter("lastName", request.getLastName()).parameter("email", request.getEmail())
				.parameter("countryCode", request.getCountryCode()).parameter("domain", request.getDomain())
				.parameter("planId", request.getPlanId()).parameter("phone", request.getPhone())
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
	
	public UpdateResult<Registration> update(String id, UpdateRegistrationRequest request) {
		HttpResponse httpResponse = RestResource.post(environment.getEnvironmentUrl())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(id)
				.parameter("domain", request.getDomain())
				.parameter("planId", request.getPlanId())
				.execute();

		UpdateResult<Registration> result = null;

		if (httpResponse.getStatusCode() == Status.OK) {
			Registration resource = httpResponse.getEntity(Registration.class);
			result = new UpdateResultImpl<Registration>(resource);
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new UpdateResultImpl<Registration>(error);
		}

		return result;
	}
	
	public UpdateResult<Registration> verifyRegistration(String emailVerificationToken) {
		HttpResponse httpResponse = RestResource.post(environment.getEnvironmentUrl())
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path("verify-email")
				.path(emailVerificationToken).execute();

		UpdateResult<Registration> result = null;

		if (httpResponse.getStatusCode() == Status.OK) {
			Registration resource = httpResponse.getEntity(Registration.class);
			result = new UpdateResultImpl<Registration>(resource);
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new UpdateResultImpl<Registration>(error);
		}

		return result;
	}
	
	public UpdateResult<Registration> provisionFreePlan(String id) {
		HttpResponse httpResponse = RestResource.post(environment.getEnvironmentUrl())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(id)
				.path("provision")
				.execute();

		UpdateResult<Registration> result = null;

		if (httpResponse.getStatusCode() == Status.OK) {
			Registration resource = httpResponse.getEntity(Registration.class);
			result = new UpdateResultImpl<Registration>(resource);
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new UpdateResultImpl<Registration>(error);
		}

		return result;
	}
	
	public UpdateResult<Registration> provisionPaidPlan(String id, ProvisionRequesst request) {
		HttpResponse httpResponse = RestResource.post(environment.getEnvironmentUrl())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path(RESOURCE_CONTEXT)
				.path(id)
				.path("provision")
				.parameter("cardholderName", request.getCardholderName())
				.parameter("expirationMonth", request.getExpirationMonth())
				.parameter("expirationYear", request.getExpirationYear())
				.parameter("cardNumber", request.getCardNumber())
				.parameter("cvv", request.getCvv())
				.execute();

		UpdateResult<Registration> result = null;

		if (httpResponse.getStatusCode() == Status.OK) {
			Registration resource = httpResponse.getEntity(Registration.class);
			result = new UpdateResultImpl<Registration>(resource);
		} else {
			Error error = httpResponse.getEntity(Error.class);
			result = new UpdateResultImpl<Registration>(error);
		}

		return result;
	}
	
	public DeleteResult delete(String id) {
		HttpResponse httpResponse = RestResource.delete(environment.getEnvironmentUrl())
				.path(RESOURCE_CONTEXT)
				.path(id)
				.execute();
		
		DeleteResult result = new DeleteResultImpl(httpResponse);
		
		return result;
	}
}