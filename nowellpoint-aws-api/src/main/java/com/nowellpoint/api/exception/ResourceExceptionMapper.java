package com.nowellpoint.api.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.nowellpoint.api.dto.ErrorDTO;
import com.stormpath.sdk.resource.ResourceException;

@Provider
public class ResourceExceptionMapper implements ExceptionMapper<ResourceException> {

	@Override
	public Response toResponse(ResourceException exception) {
		ErrorDTO error = new ErrorDTO(exception.getCode(), exception.getDeveloperMessage());
		ResponseBuilder builder = Response.status(Status.BAD_REQUEST);
		builder.entity(error);
		return builder.build();
	}
}