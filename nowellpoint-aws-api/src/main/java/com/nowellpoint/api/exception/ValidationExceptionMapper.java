package com.nowellpoint.api.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.nowellpoint.api.rest.domain.Error;
import com.nowellpoint.api.rest.domain.ValidationException;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

	@Override
	public Response toResponse(ValidationException exception) {
		Error error = Error.builder()
				.code("VALIDATION_ERROR")
				.messages(exception.getMessages())
				.build();
				
		Response response = Response.status(Status.BAD_REQUEST)
				.entity(error)
				.build();
		
		return response;
	}
}