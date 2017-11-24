package com.nowellpoint.api.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.nowellpoint.api.rest.domain.Error;
import com.nowellpoint.api.rest.domain.ValidationException;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

	@Override
	public Response toResponse(ValidationException exception) {
		Error error = new Error(exception.getCode(), exception.getMessages().toArray(new String[exception.getMessages().size()]));
		ResponseBuilder builder = Response.status(Status.BAD_REQUEST);
		builder.entity(error);
		return builder.build();
	}
}