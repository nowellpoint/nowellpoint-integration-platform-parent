package com.nowellpoint.aws.api.resource;

import static com.nowellpoint.aws.data.CacheManager.getCache;
import static com.nowellpoint.aws.data.CacheManager.deserialize;
import static com.nowellpoint.aws.data.CacheManager.serialize;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Set;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.nowellpoint.aws.api.dto.AccountProfileDTO;
import com.nowellpoint.aws.api.dto.SalesforceInstanceDTO;
import com.nowellpoint.aws.api.service.AccountProfileService;
import com.nowellpoint.aws.api.service.SalesforceInstanceService;
import com.nowellpoint.aws.api.service.SalesforceService;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.model.Token;

import redis.clients.jedis.Jedis;

@Path("/salesforce")
public class SalesforceResource {
	
	@Inject
	private SalesforceService salesforceService;
	
	@Inject
	private SalesforceInstanceService salesforceInstanceService;
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@Context
	private SecurityContext securityContext;
	
	@Context
	private UriInfo uriInfo;
	
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
	@Path("/instances")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
		String subject = securityContext.getUserPrincipal().getName();
		
		Set<SalesforceInstanceDTO> resources = salesforceInstanceService.getAll(subject);
		
		return Response.ok(resources)
				.build();
    }
	
	@GET
	@Path("/instance")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSalesforceInstance(@QueryParam(value="code") String code) {
		String subject = securityContext.getUserPrincipal().getName();
		
		OauthAuthenticationResponse response = salesforceService.authenticate(code);
		
		Token token = response.getToken();
		
		String userId = parseUserId(token.getId());
		
		putToken(subject, userId, token);
		
		SalesforceInstanceDTO resource = salesforceService.getSalesforceInstance(token.getAccessToken(), token.getId());
		
		return Response.ok(resource).build();
	}
	
	@POST
	@Path("/instance")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response saveSalesforceInstance(@FormParam(value="id") String id) {
		String subject = securityContext.getUserPrincipal().getName();
		
		String userId = parseUserId(id);
		
		Token token = getToken(subject, userId);
		
		AccountProfileDTO owner = accountProfileService.findAccountProfileBySubject(subject);	
		
		SalesforceInstanceDTO resource = salesforceService.getSalesforceInstance(token.getAccessToken(), token.getId());
		resource.setOwner(owner);
		resource.setSubject(subject);
		resource.setEventSource(uriInfo.getBaseUri());
		
		salesforceInstanceService.createSalesforceInstance(resource);
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(SalesforceResource.class)
				.path("/{id}")
				.build(resource.getId());
		
		return Response.created(uri)
				.entity(resource)
				.build(); 
	}

	@GET
	@Path("/token")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getToken(@QueryParam(value="code") String code) {
		Token token = salesforceService.authenticate(code).getToken();
				
		return Response.ok()
				.entity(token)
				.build();
	}
	
	private void putToken(String subject, String userId, Token token) {
		Jedis jedis = getCache();
		try {
			jedis.hset(subject.getBytes(), Token.class.getName().concat( userId ).getBytes(), serialize(token));
		} finally {
			jedis.close();
		}
	}
	
	private Token getToken(String subject, String userId) {
		Jedis jedis = getCache();
		byte[] bytes = null;
		try {
			bytes = jedis.hget(subject.getBytes(), Token.class.getName().concat( userId ).getBytes());
		} finally {
			jedis.close();
		}
		
		Token token = null;
		if (bytes != null) {
			token = deserialize(bytes, Token.class);
		}
		
		return token;
	}
	
	private String parseUserId(String id) {
		return id.substring(id.lastIndexOf("/") + 1);
	}
}