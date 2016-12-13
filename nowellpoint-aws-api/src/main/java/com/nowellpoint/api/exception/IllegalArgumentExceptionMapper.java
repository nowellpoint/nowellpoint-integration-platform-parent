package com.nowellpoint.api.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
	
	@Override
	public Response toResponse(IllegalArgumentException exception) {
		ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
		builder.entity(exception.getMessage());
		return builder.build();
	}
}