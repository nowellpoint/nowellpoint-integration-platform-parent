package com.nowellpoint.api.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Provider
public class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException> {

	@Override
	public Response toResponse(AuthenticationException exception) {
		ObjectNode error = JsonNodeFactory.instance.objectNode();
		error.put("error", exception.getError());
		error.put("error_description", exception.getErrorDescription());
		ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
		builder.entity(error);
		return builder.build();
	}
}