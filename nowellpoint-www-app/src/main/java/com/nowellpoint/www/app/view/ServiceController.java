package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response.Status;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.AccountProfile;
import com.nowellpoint.www.app.model.ServiceInstance;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class ServiceController {
	
	private static final Logger LOGGER = Logger.getLogger(ServiceController.class.getName());

	public ServiceController(Configuration cfg) {
		
		get("/app/services", (request, response) -> getServices(request, response), new FreeMarkerEngine(cfg));
		
		post("/app/services", (request, response) -> saveService(request, response), new FreeMarkerEngine(cfg));
		
	}
	
	private static ModelAndView getServices(Request request, Response response) {
		return null;
	}
	
	private static ModelAndView saveService(Request request, Response response) throws IOException {
		
		Token token = request.attribute("token");
		Account account = request.attribute("account");
		
		AccountProfile owner = new AccountProfile();
		//owner.setId(request.queryParams("ownerId").trim().isEmpty() ? null : request.queryParams("ownerId"));
		owner.setHref(account.getHref());
		
		ServiceInstance serviceInstance = new ServiceInstance();
		serviceInstance.setType(request.queryParams("type"));
		serviceInstance.setInstanceId(request.queryParams("instanceId"));
		serviceInstance.setAccount(request.queryParams("account"));
		//serviceInstance.setDescription(description);
		serviceInstance.setInstanceName(request.queryParams("instanceName"));
		serviceInstance.setInstanceUrl(request.queryParams("instanceUrl"));
		serviceInstance.setIsActive(Boolean.FALSE);
		serviceInstance.setIsSandbox(Boolean.valueOf(request.queryParams("isSandbox")));
		serviceInstance.setName(request.queryParams("name"));
		serviceInstance.setPrice(Double.valueOf(request.queryParams("price")));
		serviceInstance.setUom(request.queryParams("uom"));
		serviceInstance.setServiceProviderId(request.queryParams("serviceProviderId"));
		serviceInstance.setOwner(owner);
		
		HttpResponse httpResponse = null;
		
		if (request.queryParams("id").trim().isEmpty()) {
			
			httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_JSON)
					.path("services")
					.body(serviceInstance)
					.execute();
			
		} else {
			
			httpResponse = RestResource.put(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_JSON)
					.path("services")
					.path(request.queryParams("id"))
					.body(serviceInstance)
					.execute();
		}
		
		if (httpResponse.getStatusCode() != Status.OK.getStatusCode() && httpResponse.getStatusCode() != Status.CREATED.getStatusCode()) {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		serviceInstance = httpResponse.getEntity(ServiceInstance.class);
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo() + " : " + httpResponse.getHeaders().get("Location"));
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", account);
		model.put("service", serviceInstance);
		
		return new ModelAndView(model, "secure/project.html");
	}
}