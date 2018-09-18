package com.nowellpoint.console.api;

import static spark.Spark.get;
import static spark.Spark.delete;

import com.nowellpoint.console.model.Identity;
import com.nowellpoint.console.service.ServiceClient;
import com.nowellpoint.console.util.JsonTransformer;
import com.nowellpoint.www.app.util.Path;

import spark.Request;
import spark.Response;

public class IdentityResource {
	
	public static void configureRoutes() {
		
		get(Path.Resource.IDENTITIES, (request, response) 
				-> getIdentity(request, response), new JsonTransformer());
		
		delete(Path.Resource.IDENTITIES, (request, response)
				-> deleteIdentity(request, response));

	}
	
	private static Identity getIdentity(Request request, Response response) {
		
		String id = request.params(":id");
		
		Identity identity = ServiceClient.getInstance()
				.identity()
				.get(id);
		
		return identity;
	}
	
	private static String deleteIdentity(Request request, Response response) {
		
		String id = request.params(":id");
		
		ServiceClient.getInstance().identity().delete(id);
		
		response.status(204);
		
		return "";
	}
}