package com.nowellpoint.aws.app;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;
import static spark.Spark.port;


import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;


import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.nowellpoint.aws.lambda.idp.model.GetTokenResponse;
import com.nowellpoint.aws.lambda.sforce.model.Token;


import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

public class Application {

	public static void main(String[] args) {
		
		// 
		// Configure FreeMarker
		//
		
		Configuration cfg = new Configuration();
		
		//
		// set configuration options
		//
		
		cfg.setClassForTemplateLoading(Application.class, "/views");
		cfg.setDefaultEncoding("UTF-8");
		cfg.setLocale(Locale.US);
		
		//
		// set port
		//
		
		port(8443);
		
		//
		// add static file location
		//
		
		staticFileLocation("/public");
		
		//
		// add resource bundle
		//
		
		ResourceBundle messages = ResourceBundle.getBundle("messages", Locale.US);
		
		//
		//
		//
		
		Map<String, Object> attributes = new HashMap<>();
        attributes.put("applicationTitle", messages.getString("application.title"));
        
        //
        //
        //
        
        AWSLambda lambda = new AWSLambdaClient(new EnvironmentVariableCredentialsProvider());
		
		//
		// add routes for root
		//
		
		get("/", (request, response) -> {
	        return new ModelAndView(attributes, "index.ftl");
	    }, new FreeMarkerEngine(cfg));
		
		//
		//
		//
		
		post("/callback", (request, response) -> {
			Token token = new ObjectMapper().readValue(request.body(),Token.class);
			System.out.println(token.getAccessToken());
			return new ModelAndView(attributes, "hello.ftl");
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
			
			GetTokenResponse getTokenResponse = new ObjectMapper().readValue(invokeResult.getPayload().array(), GetTokenResponse.class);
			
			if (getTokenResponse.getStatusCode() == 200) {
				response.cookie("nowellpoint.token", new ObjectMapper().writeValueAsString(getTokenResponse.getToken()), 0, Boolean.TRUE);
			} else {
				System.out.println(getTokenResponse.getErrorMessage());
			}
			
			System.out.println("execution time: " + String.valueOf(System.currentTimeMillis() - start));
			return new ModelAndView(attributes, "index.ftl");
			
		}, new FreeMarkerEngine(cfg));
    }	
}