package com.nowellpoint.api.exception;

import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.nowellpoint.api.model.domain.ErrorDTO;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

	@Override
	public Response toResponse(ValidationException exception) {
		ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
		ErrorDTO error = new ErrorDTO();
		error.setCode(1000);
		error.setMessage(exception.getMessage());
		builder.entity(error);
		return builder.build();
	}
}