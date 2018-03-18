package com.nowellpoint.signup.provider;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.nowellpoint.signup.model.ErrorMessage;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

	@Override
	public Response toResponse(NotFoundException exception) {
		ErrorMessage message = ErrorMessage.builder().errorCode("NOT_FOUND").message(exception.getMessage()).build();
		return Response.status(Response.Status.NOT_FOUND).entity(message).build();
	}
}