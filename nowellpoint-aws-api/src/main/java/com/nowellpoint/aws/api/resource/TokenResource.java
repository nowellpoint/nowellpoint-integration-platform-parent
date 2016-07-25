package com.nowellpoint.aws.api.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.security.PermitAll;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.aws.api.dto.idp.Token;
import com.nowellpoint.aws.api.service.IdentityProviderService;
import com.nowellpoint.aws.api.util.AuthorizationHeader;

@Path("/oauth")
@Api(value = "/time", tags = "time")
public class TokenResource {
	
	@Inject
	private IdentityProviderService identityProviderService;
	
	@Inject
	private Event<Token> loggedInEvent;
	
	@Context
	private UriInfo uriInfo;
	
	@Context
	private HttpServletRequest servletRequest;
	
	@POST
	@Path("/token")
	@ApiOperation(value = "Get the current time", notes = "Returns the time as a string", response = String.class)
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response authenticate() {
		String basicToken = AuthorizationHeader.getBasicToken(servletRequest);
		
		String[] params = basicToken.split(":");
		
		if (params.length != 2) {
			throw new BadRequestException("Invalid Request - Missing email and/or password");
		}
		
		Token token = identityProviderService.authenticate(params[0], params[1]);
		
		params = null;
			
		loggedInEvent.fire(token);
		
		return Response.ok()
				.entity(token)
				.type(MediaType.APPLICATION_JSON)
				.build();	
	}
	
	@DELETE
	@Path("/token")
	public Response revoke() {
		String bearerToken = AuthorizationHeader.getBearerToken(servletRequest);
		
		identityProviderService.revoke(bearerToken);
		
		return Response.noContent().build();
	}
}