package com.nowellpoint.aws.api.resource;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.nowellpoint.aws.api.exception.ServiceException;
import com.nowellpoint.aws.api.service.SalesforceService;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.client.sforce.model.Token;

@Path("/salesforce")
public class SalesforceResource {
	
	@Inject
	private SalesforceService salesforceService;
	
	@Context
	private SecurityContext securityContext;
	
	@GET
	@Path("/oauth")
	@PermitAll
	public Response oauth(@QueryParam(value="state") String state) {
		
		String url = null;
		try {
			url = new StringBuilder().append(System.getProperty(Properties.SALESFORCE_AUTHORIZE_URI))
					.append("?")
					.append("response_type=code")
					.append("&")
					.append("client_id=")
					.append(System.getProperty(Properties.SALESFORCE_CLIENT_ID))
					.append("&")
					.append("client_secret=")
					.append(System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
					.append("&")
					.append("display=touch")
					.append("&")
					.append("prompt=login")
					.append("&")
					.append("redirect_uri=")
					.append(URLEncoder.encode(System.getProperty(Properties.SALESFORCE_REDIRECT_URI), "UTF-8"))
					.append("&")
					.append("scope=web%20api%20refresh_token")
					.append("&")
					.append("state=")
					.append(URLEncoder.encode(state, "UTF-8"))
					.toString();
			
		} catch (UnsupportedEncodingException e) {
			return Response.serverError()
					.entity(e.getMessage())
					.build();
		}
		
		return Response.accepted()
				.header("Location", url)
				.build();
	}

	@GET
	@Path("/token")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getToken(@QueryParam(value="code") String code) {
		
		String subject = securityContext.getUserPrincipal().getName();
		
		Token token = null; 
				
		try {
			salesforceService.getToken(subject, code);
		} catch (ServiceException e) {
			throw new WebApplicationException(e.getMessage(), e.getStatusCode());
		}
				
		return Response.ok()
				.entity(token)
				.build();
	}
}