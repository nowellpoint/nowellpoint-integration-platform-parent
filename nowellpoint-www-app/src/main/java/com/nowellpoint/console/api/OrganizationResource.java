package com.nowellpoint.console.api;

import static spark.Spark.get;

import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.JsonTransformer;
import com.nowellpoint.console.util.Path;

import spark.Request;
import spark.Response;

public class OrganizationResource {
	
	public static void configureRoutes() {
		
		get(Path.Resource.ORANIZATIONS, (request, response) 
				-> getOrganization(request, response), new JsonTransformer());

	}
	
	private static Organization getOrganization(Request request, Response response) {
		
		String id = request.params(":id");
		
		Organization organization = ServiceClient.getInstance()
				.organization()
				.get(id);
		
		return organization;
	}
}