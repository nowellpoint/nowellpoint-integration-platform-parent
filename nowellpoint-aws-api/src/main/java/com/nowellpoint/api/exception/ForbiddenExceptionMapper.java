package com.nowellpoint.api.exception;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {

	@Override
	public Response toResponse(ForbiddenException exception) {
		ResponseBuilder builder = Response.status(Response.Status.FORBIDDEN);
		builder.entity(exception.getMessage());
		return builder.build();
	}
}