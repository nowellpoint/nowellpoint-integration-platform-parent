package com.nowellpoint.aws.api.resource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.nowellpoint.aws.api.util.HttpServletRequestUtil;
import com.nowellpoint.aws.client.SalesforceClient;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.sforce.GetAuthorizationRequest;
import com.nowellpoint.aws.model.sforce.GetAuthorizationResponse;
import com.nowellpoint.aws.model.sforce.Identity;
import com.nowellpoint.aws.model.sforce.Organization;

@Path("/salesforce")
public class SalesforceResource {
	
	private static final Logger LOGGER = Logger.getLogger(SalesforceResource.class);
	
	@Context
	private HttpServletRequest servletRequest;
	
	private static SalesforceClient salesforceClient = new SalesforceClient();
	
	@GET
	@Path("/oauth")
	public Response oauth() {
		
		//
		// build the oauth url
		//
		
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
					.append("display=popup")
					.append("&")
					.append("prompt=touch")
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
		
		//
		// return the for location redirect
		//
		
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
		
		GetAuthorizationRequest authorizationRequest = new GetAuthorizationRequest().withTokenUri(System.getProperty(Properties.SALESFORCE_TOKEN_URI))
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
	@Path("/organization")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrganizationByTokenId(@QueryParam(value="id") String id) {
		
		//
		// get the bearer token from the header
		//
		
		String bearerToken = HttpServletRequestUtil.getBearerToken(servletRequest);
		
		//
		//
		//
		
		Organization organization = null;
		try {
			HttpResponse httpResponse = RestResource.get(id)
					.acceptCharset(StandardCharsets.UTF_8)
					.bearerAuthorization(bearerToken)
					.accept(MediaType.APPLICATION_JSON)
					.queryParameter("version", "latest")
					.execute();
			
			LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " : " + httpResponse.getURL());
	    	
	    	if (httpResponse.getStatusCode() >= 400) {
				throw new WebApplicationException(httpResponse.getEntity(), httpResponse.getStatusCode());
			}
	    	
	    	Identity identity = httpResponse.getEntity(Identity.class);
	    	
	    	final String ORGANIZATION_FIELDS = "Id,Division,Fax,DefaultLocaleSidKey,FiscalYearStartMonth,"
	     			+ "InstanceName,IsSandbox,LanguageLocaleKey,Name,OrganizationType,Phone,PrimaryContact,"
	     			+ "UsesStartDateAsFiscalYearName";
	     	
			httpResponse = RestResource.get(identity.getUrls().getSobjects())
	     			.bearerAuthorization(bearerToken)
	     			.path("Organization")
	     			.path(identity.getOrganizationId())
	     			.queryParameter("fields", ORGANIZATION_FIELDS)
	     			.queryParameter("version", "latest")
	     			.execute();
	     	
			LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " : " + httpResponse.getURL());
			
			if (httpResponse.getStatusCode() >= 400) {
				throw new WebApplicationException(httpResponse.getEntity(), httpResponse.getStatusCode());
			}
	     	
	     	organization = httpResponse.getEntity(Organization.class);
	     	
		} catch (IOException e) {
			throw new WebApplicationException(e, Status.BAD_REQUEST);
		}
		
		//
		// return the result
		//
		
		return Response.ok(organization)
				.type(MediaType.APPLICATION_JSON)
				.build();
	}
}