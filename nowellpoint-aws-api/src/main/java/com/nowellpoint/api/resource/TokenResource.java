package com.nowellpoint.api.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.Base64;

import javax.annotation.security.PermitAll;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.api.dto.idp.Token;
import com.nowellpoint.api.service.IdentityProviderService;

@Path("oauth")
@Api(value = "/oauth")
public class TokenResource {
	
	@Inject
	private IdentityProviderService identityProviderService;
	
	@Inject
	private Event<Token> loggedInEvent;
	
	@Context
	private UriInfo uriInfo;
	
	@POST
	@Path("token")
	@ApiOperation(value = "Authenticate with the API", notes = "Returns the OAuth Token", response = Token.class)
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response authenticate(@ApiParam(value = "basic authorization header", required = true) @HeaderParam("Authorization") String authorization) {
		
		//
		// ensure that the authorization header has a basic token
		//
		
		if (! authorization.startsWith("Basic ")) {
			throw new BadRequestException("Invalid authorization. Should be of type Basic");
		}
		
		//
		// parse the authorization token to get the base64 basic token
		//
		
		String basicToken = new String(Base64.getDecoder().decode(authorization.replace("Basic ", "")));
		
		//
		// return the basic token
		//

		String[] params = basicToken.split(":");
		
		//
		// throw exception if params are not equal to 2
		//
		
		if (params.length != 2) {
			throw new BadRequestException("Invalid Request - Missing email and/or password");
		}

		//
		// call the identity service provider to authenticate
		//
		
		Token token = identityProviderService.authenticate(params[0], params[1]);
		
		//
		// clear params
		//
		
		params = null;

		//
		// fire event for handling login functions
		//
		
		loggedInEvent.fire(token);
		
		//
		// return the Response with the token
		//

		return Response.ok()
				.entity(token)
				.type(MediaType.APPLICATION_JSON)
				.build();	
	}
	
	@DELETE
	@Path("token")
	@ApiOperation(value = "Expire the OAuth token", notes = "Access to the API will be revoked until a new token is issued")
	@ApiResponses(value = { 
		      @ApiResponse(code = 204, message = "successful operation") 
		  })
	public Response revoke(@ApiParam(value = "bearer authorization header", required = true) @HeaderParam("Authorization") String authorization) {
		
		//
		// ensure that the authorization header has a bearer token
		//
		
		if (! authorization.startsWith("Bearer ")) {
			throw new BadRequestException("Invalid authorization. Should be of type Bearer");
		}
		
		//
		// parse the authorization header to get the bearer token
		//
		
		String bearerToken = new String(authorization.replace("Bearer ", ""));
		
		//
		// call the identity provider service to revoke the token
		//
		
		identityProviderService.revoke(bearerToken);
		
		//
		// return Response
		//
		
		return Response.noContent().build();
	}
}