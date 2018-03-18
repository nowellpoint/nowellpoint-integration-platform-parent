package com.nowellpoint.signup.provider;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.nowellpoint.signup.model.ErrorMessage;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

	@Override
	public Response toResponse(IllegalArgumentException exception) {
		ErrorMessage message = ErrorMessage.builder().errorCode("INVALID_VALUE").message(exception.getMessage()).build();
		return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
	}
}