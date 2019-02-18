package com.nowellpoint.api.rest;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nowellpoint.api.rest.domain.Token;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("oauth")
@Api(value = "/oauth")
public interface TokenResource {
	
	@POST
	@PermitAll
	@Path("token")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@ApiOperation(value = "Authenticate with the API", notes = "Returns the OAuth Token", response = Token.class)
	public Response authenticate(@ApiParam(value = "basic authorization header", required = true) @HeaderParam("Authorization") String authorization, @ApiParam(value = "set the grant type. options are: password", required = true) @FormParam("grant_type") String grantType);
	
	@DELETE
	@Path("token")
	@ApiOperation(value = "Expire the OAuth token", notes = "Access to the API will be revoked until a new token is issued")
	@ApiResponses(value = { 
		      @ApiResponse(code = 204, message = "successful operation") 
		  })
	public Response revokeToken(@ApiParam(value = "bearer authorization header", required = true) @HeaderParam("Authorization") String authorization);
}
