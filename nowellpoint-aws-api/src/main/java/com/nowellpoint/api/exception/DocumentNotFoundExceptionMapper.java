package com.nowellpoint.api.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.nowellpoint.aws.data.mongodb.DocumentNotFoundException;

@Provider
public class DocumentNotFoundExceptionMapper implements ExceptionMapper<DocumentNotFoundException> {

	@Override
	public Response toResponse(DocumentNotFoundException exception) {
		ResponseBuilder builder = Response.status(Status.NOT_FOUND);
		builder.entity(exception.getMessage());
		return builder.build();
	}
}