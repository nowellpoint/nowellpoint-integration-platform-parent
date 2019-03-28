/**
 * 
 * Copyright 2015-2016 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.nowellpoint.console;

import static com.nowellpoint.console.util.Exceptions.configureExceptionRoutes;
import static com.nowellpoint.console.util.Filters.setupFilters;
import static com.nowellpoint.console.util.Routes.configureRoutes;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.staticFileLocation;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import com.nowellpoint.console.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;
import spark.servlet.SparkApplication;

public class Bootstrap implements SparkApplication {	

	@Override
	public void init() {
		
		//
		// add static file location
		//
		
		staticFileLocation("public");

		//
		// Configure FreeMarker
		//

		Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);

		//
		// set configuration options
		//

		configuration.setClassForTemplateLoading(Bootstrap.class, "/views");
		configuration.setDefaultEncoding("UTF-8");
		
		//
		// setup filters
		//
		
		setupFilters();

		//
		// setup routes
		//
		
		configureRoutes(configuration);

		//
		// health check route
		//

		get(Path.Route.HEALTH_CHECK, (request, response) 
				-> healthCheck(request, response));
		
		exception(BadRequestException.class, (exception, request, response) -> {
			response.status(400);
			response.body(exception.getMessage());
		});
		
		exception(NotFoundException.class, (exception, request, response) -> {
			response.status(404);
			response.body(exception.getMessage());
		});
		
		configureExceptionRoutes();
		
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */

	private static String healthCheck(Request request, Response response) {
		response.status(200);
		return "";
	}
}