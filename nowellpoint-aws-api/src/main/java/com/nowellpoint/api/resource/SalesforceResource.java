package com.nowellpoint.api.resource;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.hibernate.validator.constraints.NotEmpty;

import com.nowellpoint.api.service.SalesforceService;
import com.nowellpoint.util.Properties;
import com.nowellpoint.client.sforce.model.LoginResult;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalSobjectsResult;

@Path("/salesforce")
public class SalesforceResource {
	
	@Inject
	private SalesforceService salesforceService;
	
	@Context
	private SecurityContext securityContext;
	
	@Context
	private UriInfo uriInfo;
	
	@GET
	@Path("oauth")
	@PermitAll
	public Response oauth() {
		
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
					.toString();
			
		} catch (UnsupportedEncodingException e) {
			return Response.serverError()
					.entity(e.getMessage())
					.build();
		}
		
		return Response.status(Response.Status.ACCEPTED)
				.header("Location", url)
				.build();
	}

	@GET
	@Path("oauth/token")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getToken(@QueryParam(value="code") String code) {
		
		Token token = salesforceService.authenticate(code).getToken();
				
		return Response.ok()
				.entity(token)
				.build();
	}
	
	@POST
	@Path("login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(
			@FormParam(value="authEndpoint") @NotEmpty String authEndpoint,
			@FormParam(value="username") @NotEmpty String username,
			@FormParam(value="password") @NotEmpty String password,
			@FormParam(value="securityToken") @NotEmpty String securityToken) {
		
		LoginResult result = salesforceService.login(authEndpoint, username, password, securityToken);
		
		return Response.ok(result)
				.build();
	}
	
	@GET
	@Path("sobjects")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSobjects(@QueryParam(value="id") String id) {
		
		DescribeGlobalSobjectsResult result = salesforceService.describe(id);
		
		return Response.ok(result.getSobjects())
				.build();
	}
}