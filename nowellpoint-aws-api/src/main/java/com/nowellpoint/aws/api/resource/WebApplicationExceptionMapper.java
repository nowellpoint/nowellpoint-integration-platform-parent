package com.nowellpoint.aws.api.resource;

import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {
	
	private static final Logger logger = Logger.getLogger( WebApplicationExceptionMapper.class.getName() );

	@Override
	public Response toResponse(WebApplicationException exception) {
		logger.warning("WebApplicationException thrown [statusCode: {}]" + exception.getResponse().getStatus());  
		return Response.status(exception.getResponse().getStatus())
				.entity(exception.getMessage())
				.type(MediaType.APPLICATION_JSON).build();
	}
}