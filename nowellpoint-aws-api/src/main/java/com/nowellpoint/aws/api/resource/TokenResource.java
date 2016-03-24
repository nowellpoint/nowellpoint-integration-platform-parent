package com.nowellpoint.aws.api.resource;

import javax.annotation.security.PermitAll;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.aws.api.dto.idp.Token;
import com.nowellpoint.aws.api.event.LoggedInEvent;
import com.nowellpoint.aws.api.service.IdentityProviderService;
import com.nowellpoint.aws.api.util.AuthorizationHeader;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.tools.TokenParser;

@Path("/oauth")
public class TokenResource {
	
	@Inject
	private IdentityProviderService identityProviderService;
	
	@Inject
	private Event<LoggedInEvent> loggedInEvent;
	
	@Context
	private UriInfo uriInfo;
	
	@Context
	private HttpServletRequest servletRequest;
	
	@POST
	@Path("/token")
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public Response authenticate() {
		String basicToken = AuthorizationHeader.getBasicToken(servletRequest);
		
		String[] params = basicToken.split(":");
		
		if (params.length != 2) {
			throw new WebApplicationException("Invalid Request - Missing username and/or password", Status.BAD_REQUEST);
		}
		
		Token token = identityProviderService.authenticate(params[0], params[1]);
		
		params = null;
			
		loggedInEvent.fire(new LoggedInEvent(uriInfo.getBaseUri(), TokenParser.parseToken(System.getProperty(Properties.STORMPATH_API_KEY_SECRET), token.getAccessToken())));
		
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