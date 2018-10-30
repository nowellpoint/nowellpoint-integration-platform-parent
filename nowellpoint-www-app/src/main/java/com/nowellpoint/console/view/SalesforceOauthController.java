package com.nowellpoint.console.view;

import static spark.Spark.get;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.console.model.ConnectionRequest;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.EnvironmentVariables;
import com.nowellpoint.console.util.Path;
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
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {

            HttpResponse tokenResponse = RestResource.get(EnvironmentVariables.getSalesforceTokenUri())
					.acceptCharset(StandardCharsets.UTF_8)
					.accept(MediaType.APPLICATION_JSON)
                    .queryParameter("grant_type", "authorization_code")
                    .queryParameter("code", authorizationCode)
                    .queryParameter("client_id", EnvironmentVariables.getSalesforceClientId())
                    .queryParameter("client_secret", EnvironmentVariables.getSalesforceClientSecret())
                    .queryParameter("redirect_uri", EnvironmentVariables.getSalesforceCallbackUri())
                    .execute();
            
            //Token token = tokenResponse.getEntity(Token.)
                    
            JsonNode tokenNode = mapper.readTree(tokenResponse.getAsString());
			
			String accessToken = tokenNode.get("access_token").asText();
			
			HttpResponse identityResponse = RestResource.get(tokenNode.get("id").asText())
					.acceptCharset(StandardCharsets.UTF_8)
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(accessToken)
					.queryParameter("version", "latest")
					.execute();
			
			JsonNode identityNode = mapper.readTree(identityResponse.getAsString());
			
			HttpResponse organizationResponse = RestResource.get(identityNode.get("urls").get("sobjects").asText())
					.acceptCharset(StandardCharsets.UTF_8)
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(accessToken)
	     			.path("Organization")
	     			.path(identityNode.get("organization_id").asText())
	     			.queryParameter("fields", "Id,Name")
	     			.queryParameter("version", "latest")
	     			.execute();
			
			JsonNode organizationNode = mapper.readTree(organizationResponse.getAsString());
			
			ConnectionRequest connectorRequest = ConnectionRequest.builder()
					.connectedUser(identityNode.get("username").asText())
					.domain(organizationNode.get("Id").asText())
					.encryptedToken(Base64.getEncoder().encodeToString(tokenNode.toString().getBytes()))
					.instanceUrl(tokenNode.get("instance_url").asText())
					.name(organizationNode.get("Name").asText())
					.build();
			
			ServiceClient.getInstance().organization().update(
					organizationId, 
					connectorRequest);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		response.redirect(Path.Route.START);
	   
		return "";
	}
}