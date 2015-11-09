package com.nowellpoint.aws.app.route;

import static spark.Spark.get;

import java.util.HashMap;
import java.util.Map;

import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

public class Index {
	
	private Index() {
		
	}
	
	public static void buildRoutes() {
		get("/", (request, response) -> {
	        Map<String, Object> attributes = new HashMap<>();
	        attributes.put("message", "Hello World!");
	        return new ModelAndView(attributes, "hello.ftl");
	    }, new FreeMarkerEngine());
	}
}