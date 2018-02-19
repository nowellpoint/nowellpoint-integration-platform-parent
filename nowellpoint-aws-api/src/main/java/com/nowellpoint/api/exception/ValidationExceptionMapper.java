package com.nowellpoint.api.exception;

import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.nowellpoint.api.rest.domain.Error;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

	@Override
	public Response toResponse(ValidationException exception) {
		Error error = Error.builder()
				.code("VALIDATION_ERROR")
				.addMessage(exception.getMessage())
				.build();
		
		Response response = Response.status(Response.Status.BAD_REQUEST)
				.entity(error)
				.build();
		
		return response;
	}
}