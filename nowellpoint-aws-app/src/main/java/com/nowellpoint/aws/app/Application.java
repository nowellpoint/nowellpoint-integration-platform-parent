package com.nowellpoint.aws.app;

import static spark.Spark.staticFileLocation;
import static spark.Spark.port;

import com.nowellpoint.aws.app.route.Index;

public class Application {

	public static void main(String[] args) {
		
		port(8443);
		
		staticFileLocation("/public");
		
		Index.buildRoutes();
        
    }	
}