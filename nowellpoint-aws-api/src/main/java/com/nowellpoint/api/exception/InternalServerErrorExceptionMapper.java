package com.nowellpoint.api.exception;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InternalServerErrorExceptionMapper implements ExceptionMapper<InternalServerErrorException> {
	
	@Override
	public Response toResponse(InternalServerErrorException exception) {
		ResponseBuilder builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
		builder.entity(exception.getMessage());
		return builder.build();
	}
}