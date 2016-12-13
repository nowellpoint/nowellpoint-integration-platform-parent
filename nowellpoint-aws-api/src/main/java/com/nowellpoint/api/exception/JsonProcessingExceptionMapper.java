package com.nowellpoint.api.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class JsonProcessingExceptionMapper implements ExceptionMapper<JsonProcessingException> {

	@Override
	public Response toResponse(JsonProcessingException exception) {
		ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
		builder.entity("Invalid resource format: " + exception.getMessage());
		return builder.build();
	}
}