package com.nowellpoint.console.view;

import static spark.Spark.get;

import java.nio.charset.StandardCharsets;

import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.console.model.ConnectionRequest;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.EnvironmentVariables;
import com.nowellpoint.console.util.Path;
import com.nowellpoint.console.util.SecretsManager;
import com.nowellpoint.http.HttpRequestException;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;

import spark.Request;
import spark.Response;

public class SalesforceOauthController extends BaseController {
	
	public static void configureRoutes() {
		get(Path.Route.SALESFORCE_OAUTH_CALLBACK, (request, response) 
				-> oauthCallback(request, response));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String oauthCallback(Request request, Response response) {

        String organizationId = request.queryParams("state");
        String authorizationCode = request.queryParams("code");
        
        try {

            HttpResponse tokenResponse = RestResource.get(EnvironmentVariables.getSalesforceTokenUri())
					.acceptCharset(StandardCharsets.UTF_8)
					.accept(MediaType.APPLICATION_JSON)
                    .queryParameter("grant_type", "authorization_code")
                    .queryParameter("code", authorizationCode)
                    .queryParameter("client_id", SecretsManager.getSalesforceClientId())
                    .queryParameter("client_secret", SecretsManager.getSalesforceClientSecret())
                    .queryParameter("redirect_uri", EnvironmentVariables.getSalesforceRedirectUri())
                    .execute();
            
            Token token = tokenResponse.getEntity(Token.class);
			
			HttpResponse identityResponse = RestResource.get(token.getId())
					.acceptCharset(StandardCharsets.UTF_8)
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
					.queryParameter("version", "latest")
					.execute();
			
			Identity identity = identityResponse.getEntity(Identity.class);
			
			HttpResponse organizationResponse = RestResource.get(identity.getUrls().getSobjects())
					.acceptCharset(StandardCharsets.UTF_8)
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
	     			.path("Organization")
	     			.path(identity.getOrganizationId())
	     			.queryParameter("fields", "Id,Name")
	     			.queryParameter("version", "latest")
	     			.execute();
			
			Organization organization = organizationResponse.getEntity(Organization.class);
			
			ConnectionRequest connectorRequest = ConnectionRequest.builder()
					.accessToken(token.getAccessToken())
					.domain(organization.getId())
					.identityUrl(token.getId())
					.instanceUrl(token.getInstanceUrl())
					.issuedAt(token.getIssuedAt())
					.name(organization.getName())
					.refreshToken(token.getRefreshToken())
					.tokenType(token.getTokenType())
					.username(identity.getUsername())
					.build();
			
			ServiceClient.getInstance().organization().update(
					organizationId, 
					connectorRequest);
			
        } catch (HttpRequestException e) {
        	e.printStackTrace();
        }
		
		response.redirect(Path.Route.START);
	   
		return "";
	}
}