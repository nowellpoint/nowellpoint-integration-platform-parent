package com.nowellpoint.aws.api.resource;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.nowellpoint.aws.api.dto.sforce.UserInfo;
import com.nowellpoint.aws.api.service.SalesforceService;
import com.nowellpoint.aws.api.util.HttpServletRequestUtil;
import com.nowellpoint.aws.client.SalesforceClient;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.sforce.GetAuthorizationRequest;
import com.nowellpoint.aws.model.sforce.GetAuthorizationResponse;

@Path("/salesforce")
public class SalesforceResource {
	
	@Inject
	private SalesforceService salesforceService;
	
	@Context
	private HttpServletRequest servletRequest;
	
	private static SalesforceClient salesforceClient = new SalesforceClient();
	
	@GET
	@Path("/oauth")
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
		
		return Response.accepted()
				.header("Location", url)
				.build();
	}

	@GET
	@Path("/token")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getToken(@QueryParam(value="code") String code) {
		
		//
		// build the get authorization request
		//
		
		GetAuthorizationRequest authorizationRequest = new GetAuthorizationRequest()
				.withTokenUri(System.getProperty(Properties.SALESFORCE_TOKEN_URI))
				.withClientId(System.getProperty(Properties.SALESFORCE_CLIENT_ID))
				.withClientSecret(System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
				.withRedirectUri(System.getProperty(Properties.SALESFORCE_REDIRECT_URI))
				.withCode(code);
		
		//
		// execute the get authorization request
		//
		
		GetAuthorizationResponse authorizationResponse = salesforceClient.authorize(authorizationRequest);
		
		//
		// throw WebApplicationException if the response is not ok
		//
		
		if (authorizationResponse.getStatusCode() >= 400) {
			throw new WebApplicationException(authorizationResponse.getErrorMessage(), authorizationResponse.getStatusCode());
		}
		
		//
		// return the result
		//
		
		return Response.status(authorizationResponse.getStatusCode())
				.entity(authorizationResponse.getToken())
				.type(MediaType.APPLICATION_JSON)
				.build();
	}
	
	@GET
	@Path("/user-info")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response userInfo(@QueryParam(value="code") String code) {
		
		//
		// get the subject from the reqeust
		//
		
		String subject = HttpServletRequestUtil.getSubject(servletRequest);
		
		//
		// use the returned oauth code to get UserInfo
		//
		
		UserInfo userInfo = salesforceService.getUserInfo(subject, code);
		
		//
		// return the result
		//
		
		return Response.ok()
				.entity(userInfo)
				.type(MediaType.APPLICATION_JSON)
				.build();
	}
}