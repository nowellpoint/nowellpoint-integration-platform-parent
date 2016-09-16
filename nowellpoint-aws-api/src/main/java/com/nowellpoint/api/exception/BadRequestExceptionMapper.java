package com.nowellpoint.api.exception;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

	@Override
	public Response toResponse(BadRequestException exception) {
		ResponseBuilder builder = Response.status(exception.getResponse().getStatusInfo());
		builder.entity(exception.getMessage());
		return builder.build();
	}
}