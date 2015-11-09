package com.nowellpoint.aws.app;

import static spark.Spark.get;
import static spark.Spark.port;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

public class Application {

	public static void main(String[] args) {
		
		port(8443);
		
		get("/", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("message", "Hello World!");
            return new ModelAndView(attributes, "hello.ftl");
        }, new FreeMarkerEngine());
        
    }	
}