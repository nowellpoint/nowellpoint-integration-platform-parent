package com.nowellpoint.api.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.nowellpoint.api.model.domain.Error;
import com.nowellpoint.mongodb.document.DocumentNotFoundException;

@Provider
public class DocumentNotFoundExceptionMapper implements ExceptionMapper<DocumentNotFoundException> {

	@Override
	public Response toResponse(DocumentNotFoundException exception) {
		Error error = new Error(3000, exception.getMessage());
		ResponseBuilder builder = Response.status(Response.Status.NOT_FOUND);
		builder.entity(error);
		return builder.build();
	}
}