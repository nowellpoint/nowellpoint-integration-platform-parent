package com.nowellpoint.aws.app;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;
import static spark.Spark.port;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.fasterxml.jackson.databind.ObjectMapper;
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
		
		/**
		 * 
		 */
		
		Map<String, Object> attributes = new HashMap<>();
        attributes.put("applicationTitle", messages.getString("application.title"));
		
		//
		// add routes for root
		//
		
		get("/", (request, response) -> {
	        return new ModelAndView(attributes, "index.ftl");
	    }, new FreeMarkerEngine());
		
		post("/callback", (request, response) -> {
			Token token = new ObjectMapper().readValue(request.body(),Token.class);
			System.out.println(token.getAccessToken());
			return new ModelAndView(attributes, "hello.ftl");
		}, new FreeMarkerEngine());
        
		get("/login", (request, response) -> {
			System.out.println(request.queryParams("username"));
			System.out.println(request.queryParams("password"));
			return new ModelAndView(attributes, "index.ftl");
		}, new FreeMarkerEngine());
    }	
}