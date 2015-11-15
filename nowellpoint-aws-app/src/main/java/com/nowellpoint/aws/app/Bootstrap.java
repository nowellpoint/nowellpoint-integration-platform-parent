package com.nowellpoint.aws.app;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.secure;
import static spark.Spark.staticFileLocation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nowellpoint.aws.idp.model.GetTokenResponse;
import com.nowellpoint.aws.sforce.model.GetAuthorizationRequest;
import com.nowellpoint.aws.sforce.model.GetAuthorizationResponse;
import com.nowellpoint.aws.sforce.model.GetIdentityRequest;
import com.nowellpoint.aws.sforce.model.GetIdentityResponse;
import com.nowellpoint.aws.sforce.model.Token;

import freemarker.template.Configuration;

public class Bootstrap {
	
	private static ObjectMapper objectMapper = new ObjectMapper();

	public static void main(String[] args) {
		
		// 
		// Configure FreeMarker
		//
		
		Configuration cfg = new Configuration();
		
		//
		// set configuration options
		//
		
		cfg.setClassForTemplateLoading(Bootstrap.class, "/views");
		cfg.setDefaultEncoding("UTF-8");
		cfg.setLocale(Locale.US);
		
		//
		// port and keystore
		//
		
		port(getPort());
		//secure(keystoreFile, keystorePassword, truststoreFile, truststorePassword);
		
		//
		// add static file location
		//
		
		staticFileLocation("/public");
		
		//
		//
		//
		
		addRoutes(cfg);
    }	
	
	private static void addRoutes(Configuration cfg) {
		
		//
		// add resource bundle
		//
		
		ResourceBundle messages = ResourceBundle.getBundle("messages", Locale.US);
		
		//
		// add properties to model
		//
		
		Map<String, Object> attributes = new HashMap<>();
        attributes.put("applicationTitle", messages.getString("application.title"));
        attributes.put("services", messages.getString("services"));
        
        //
        //
        //
        
        AWSLambda lambda = new AWSLambdaClient();
		
		//
		// add routes for root
		//
		
		get("/", (request, response) -> {
	        return new ModelAndView(attributes, "index.ftl");
	    }, new FreeMarkerEngine(cfg));
		
		//
		//
		//
		
		get("/callback", (request, response) -> {
			
			GetAuthorizationRequest authorizationRequest = new GetAuthorizationRequest().withCode(request.queryParams("code"));
			
			InvokeRequest invokeRequest = new InvokeRequest();
			invokeRequest.setInvocationType(InvocationType.RequestResponse);
			invokeRequest.setFunctionName("SalesforceTokenRequest");
			invokeRequest.setPayload(authorizationRequest.getAsJson());
			
			InvokeResult invokeResult = lambda.invoke(invokeRequest);
			
			GetAuthorizationResponse getAuthorizationResponse = readInvokeResult(GetAuthorizationResponse.class, invokeResult);
			
			response.status(getAuthorizationResponse.getStatusCode());
			
			if (getAuthorizationResponse.getStatusCode() == 200) {
				Token token = getAuthorizationResponse.getToken();
				String body = JsonNodeFactory.instance.objectNode()
						.put("id", token.getId())
						.put("accessToken", token.getAccessToken())
						.toString();
				
				response.body(body);
				response.redirect("/identity");
			} else {
			    response.body(getAuthorizationResponse.getErrorMessage());
			}
			
			return "ok";
			
		});
		
		//
		//
		//
		
		get("/identity", (request, response) -> {
			
			GetIdentityRequest identityRequest = new GetIdentityRequest().withAccessToken(request.headers("Authorization").replaceFirst("Bearer", "").trim())
					.withId(request.queryParams("id"));
			
			InvokeRequest invokeRequest = new InvokeRequest();
			invokeRequest.setInvocationType(InvocationType.RequestResponse);
			invokeRequest.setFunctionName("SalesforceTokenRequest");
			invokeRequest.setPayload(identityRequest.getAsJson());
			
			InvokeResult invokeResult = lambda.invoke(invokeRequest);
			
			GetIdentityResponse getIdentityResponse = readInvokeResult(GetIdentityResponse.class, invokeResult);
			
			if (getIdentityResponse.getStatusCode() < 400) {
				attributes.put("identity", getIdentityResponse.getIdentity());
			} else {
				attributes.put("exception", getIdentityResponse.getErrorMessage());
			}
			
			return new ModelAndView(attributes, "identity.ftl");
		}, new FreeMarkerEngine(cfg));
        
		//
		//
		//
		
		get("/login", (request, response) -> {
			long start = System.currentTimeMillis();
			String payload = JsonNodeFactory.instance.objectNode()
					.put("username", request.queryParams("username"))
					.put("password", request.queryParams("password"))
					.toString();
			
			InvokeRequest invokeRequest = new InvokeRequest();
			invokeRequest.setInvocationType(InvocationType.RequestResponse);
			invokeRequest.setFunctionName("IDP_UsernamePasswordAuthentication");
			invokeRequest.setPayload(payload);
			
			InvokeResult invokeResult = lambda.invoke(invokeRequest);
			
			GetTokenResponse getTokenResponse = readInvokeResult(GetTokenResponse.class, invokeResult);
			
			response.status(getTokenResponse.getStatusCode());
			
			if (getTokenResponse.getStatusCode() == 200) {
				response.cookie("nowellpoint.token", objectMapper.writeValueAsString(getTokenResponse.getToken()), 0, Boolean.TRUE);
			} else {
				response.body(getTokenResponse.getErrorMessage());
			}
			
			System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
			return new ModelAndView(attributes, "index.ftl");
			
		}, new FreeMarkerEngine(cfg));
	}
	
	private static <T> T readInvokeResult(Class<T> type, InvokeResult invokeResult) throws JsonParseException, JsonMappingException, IOException {
		return objectMapper.readValue(invokeResult.getPayload().array(), type);
	}
	
	private static int getPort() {
		String port = Optional.ofNullable(System.getenv().get("PORT")).orElse("8080");
		return Integer.parseInt(port);
	}
}