package com.nowellpoint.aws.api.exception;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

	@Override
	public Response toResponse(ConstraintViolationException exception) {
		StringBuffer message = new StringBuffer();
		exception.getConstraintViolations().stream().forEach(violation -> {
			message.append(violation.getMessage());
			message.append(System.getProperty("line.separator"));
		});
		
		return Response.status(Status.BAD_REQUEST)
				.entity(new ExceptionResponse(message.toString()))
				.type(MediaType.APPLICATION_JSON).build();
	}
}