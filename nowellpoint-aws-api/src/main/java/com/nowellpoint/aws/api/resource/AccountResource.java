package com.nowellpoint.aws.api.resource;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.nowellpoint.aws.api.data.CacheManager;
import com.nowellpoint.aws.api.util.HttpServletRequestUtil;
import com.nowellpoint.aws.idp.client.IdentityProviderClient;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.GetAccountRequest;
import com.nowellpoint.aws.idp.model.GetAccountResponse;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.EventAction;
import com.nowellpoint.aws.model.EventBuilder;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;
import com.nowellpoint.aws.tools.TokenParser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

@Path("/account")
public class AccountResource {
	
	private static final IdentityProviderClient identityProviderClient = new IdentityProviderClient();
	
	private static final DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();
	
	@Inject
	private CacheManager cacheManager;
	
	@Context
	private UriInfo uriInfo;
	
	@Context
	private HttpServletRequest servletRequest;
	
	/**
	 * 
	 * @return
	 */
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	public Response getAccount() {		
		String bearerToken = HttpServletRequestUtil.getBearerToken(servletRequest);
		
		if (bearerToken == null || bearerToken.trim().isEmpty()) {
			throw new BadRequestException("Missing bearer token");
		}
		
		Account account = cacheManager.get(bearerToken);
		
		if (account != null) {			
			return Response.ok(account)
					.type(MediaType.APPLICATION_JSON)
					.build();
		}
		
		Jws<Claims> jws = TokenParser.parseToken(System.getProperty(Properties.STORMPATH_API_KEY_SECRET), bearerToken);
		
		String href = jws.getBody().getSubject();
		
		GetAccountRequest getAccountRequest = new GetAccountRequest().withApiKeyId(System.getProperty(Properties.STORMPATH_API_KEY_ID))
				.withApiKeySecret(System.getProperty(Properties.STORMPATH_API_KEY_SECRET))
				.withHref(href);
		
		GetAccountResponse getAccountResponse = identityProviderClient.account(getAccountRequest);
		
		Long exp = TimeUnit.MILLISECONDS.toSeconds(jws.getBody().getExpiration().getTime() - System.currentTimeMillis());
		
		cacheManager.setex(bearerToken, exp.intValue(), getAccountResponse.getAccount());
		
		return Response.status(getAccountResponse.getStatusCode())
				.entity((getAccountResponse.getStatusCode() != 200 ? getAccountResponse.getErrorMessage() : getAccountResponse.getAccount()))
				.type(MediaType.APPLICATION_JSON)
				.build();
	}
	
	/**
	 * 
	 * @param resource
	 * @return
	 */
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(Account resource) {
				
		Event event = null;
		try {			
			event = new EventBuilder().withSubject(System.getProperty(Properties.DEFAULT_SUBJECT))
					.withEventAction(EventAction.CREATE)
					.withEventSource(uriInfo.getRequestUri())
					.withPayload(resource)
					.withPropertyStore(System.getenv("PROPERTY_STORE"))
					.withType(Account.class)
					.build();
			
			mapper.save(event);
			
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(AccountResource.class)
				.path("/{id}")
				.build(event.getId());
		
		return Response.created(uri).build();	
	}
}