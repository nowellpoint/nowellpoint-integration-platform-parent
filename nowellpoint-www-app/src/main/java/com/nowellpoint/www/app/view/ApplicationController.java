package com.nowellpoint.www.app.view;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.jboss.logging.Logger;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.PostRequest;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.TokenCredentials;
import com.nowellpoint.client.model.AccountProfile;
import com.nowellpoint.client.model.Application;
import com.nowellpoint.client.model.Environment;
import com.nowellpoint.client.model.ExceptionResponse;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.ServiceInstance;
import com.nowellpoint.client.model.ServiceProvider;
import com.nowellpoint.client.model.idp.Token;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;
import spark.Route;

public class ApplicationController extends AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(ApplicationController.class);
	
	public ApplicationController(Configuration configuration) {
		super(ApplicationController.class, configuration);
	}
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * selectSalesforceConnector
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route selectSalesforceConnector = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
    			.execute();
		
		List<SalesforceConnector> salesforceConnectors = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			salesforceConnectors = httpResponse.getEntityList(SalesforceConnector.class);
		} else {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		Map<String, Object> model = getModel();
    	model.put("salesforceConnectorsList", salesforceConnectors);
		
		return render(request, model, Path.Template.APPLICATION_CONNECTOR_SELECT);
	};

	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * newApplication
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route newApplication = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		String id = request.queryParams("id");
		
		SalesforceConnector salesforceConnector = new NowellpointClient(new TokenCredentials(token))
				.salesforceConnector()
				.getSalesforceConnector(id);
		
		Map<String, Object> model = getModel();
		model.put("mode", "new");
    	model.put("salesforceConnector", salesforceConnector);
		
		return render(request, model, Path.Template.APPLICATION_EDIT);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * viewApplication
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route viewApplication = (Request request, Response response) -> {
		
		String id = request.params(":id");
		
		Token token = getToken(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("applications")
				.path(id)
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		Application application = httpResponse.getEntity(Application.class);
		
		Map<String, Object> model = getModel();
		model.put("application", application);
		model.put("successMessage", request.cookie("successMessage"));
		
		return render(request, model, Path.Template.APPLICATION);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * editApplication
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route editApplication = (Request request, Response response) -> {
		
		String id = request.params(":id");
		String view = request.queryParams("view");
		
		Token token = getToken(request);
		
		Application application = new NowellpointClient(new TokenCredentials(token))
				.application()
				.getApplication(id);
		
		Map<String, Object> model = getModel();
		model.put("application", application);
		model.put("mode", "edit");
		
		if (view != null && view.equals("1")) {
			model.put("cancel", Path.Route.APPLICATION_LIST);
		} else {
			model.put("cancel", Path.Route.APPLICATION_VIEW.replace(":id", id));
		}
		
		return render(request, model, Path.Template.APPLICATION_EDIT);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * getApplications
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route getApplications = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("applications")
				.execute();
		
		List<Application> applications = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			applications = httpResponse.getEntityList(Application.class);
		} else {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		Map<String, Object> model = getModel();
		model.put("applicationList", applications);
		
		return render(request, model, Path.Template.APPLICATIONS_LIST);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * createApplication
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route createApplication = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path("applications")
				.parameter("name", request.queryParams("name"))
				.parameter("description", request.queryParams("description"))
				.parameter("importSandboxes", request.queryParams("importSandboxes") != null ? "true" : "false")
				.parameter("importServices", request.queryParams("importServices") != null ? "true" : "false")
				.parameter("connectorId", request.queryParams("connectorId"))
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		Application application = httpResponse.getEntity(Application.class);
		
		response.redirect(Path.Route.APPLICATION_VIEW.replace(":id", application.getId()));
		
		return "";
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * updateApplication
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route updateApplication = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		HttpResponse httpResponse = RestResource.put(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path("applications")
				.path(id)
				.parameter("name", request.queryParams("name"))
				.parameter("description", request.queryParams("description"))
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		Application application = httpResponse.getEntity(Application.class);
		
		response.redirect(Path.Route.APPLICATION_VIEW.replace(":id", application.getId()));
		
		return "";
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * deleteApplication
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route deleteApplication = (Request request, Response response) -> {
		
		String id = request.params(":id");
		
		Token token = request.attribute("token");
		
		NowellpointClient client = new NowellpointClient(new TokenCredentials(token));
		client.application().deleteApplication(id);
		
		return "";	
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * viewEnvironment
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route viewEnvironment = (Request request, Response response) -> {		
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path("applications")
    			.path(id)
    			.path("environment")
    			.path(key)
    			.execute();
		
		Environment environment = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			environment = httpResponse.getEntity(Environment.class);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		Map<String, Object> model = getModel();
		model.put("id", id);
		model.put("mode", "view");
		model.put("environment", environment);
		
		return render(request, model, Path.Template.ENVIRONMENT);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * editEnvironment
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */

	public Route editEnvironment = (Request request, Response response) -> {		
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path("applications")
    			.path(id)
    			.path("environment")
    			.path(key)
    			.execute();
		
		Environment environment = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			environment = httpResponse.getEntity(Environment.class);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		Map<String, Object> model = getModel();
		model.put("id", id);
		model.put("mode", "edit");
		model.put("action", String.format("/app/applications/%s/environments/%s", id, key));
		model.put("environment", environment);
		
		return render(request, model, Path.Template.ENVIRONMENT);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * newEnvironment
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route newEnvironment = (Request request, Response response) -> {			
		String id = request.params(":id");
		
		Environment environment = new Environment();
		environment.setAuthEndpoint("https://test.salesforce.com");
		
		Map<String, Object> model = getModel();
		model.put("id", id);
		model.put("mode", "add");
		model.put("action", String.format("/app/applications/%s/environments", id));
		model.put("environment", environment);
		
		return render(request, model, Path.Template.ENVIRONMENT);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * addEnvironment
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */

	public Route addEnvironment = (Request request, Response response) -> {		
		Token token = getToken(request);
		
		String id = request.params(":id");
		String active = request.queryParams("active");
		String authEndpoint = request.queryParams("authEndpoint");
		String name = request.queryParams("environmentName");
		String password = request.queryParams("password");
		String username = request.queryParams("username");
		String securityToken = request.queryParams("securityToken");
		
		Environment environment = new Environment()
				.withIsActive(Boolean.valueOf(active))
				.withAuthEndpoint(authEndpoint)
				.withEnvironmentName(name)
				.withPassword(password)
				.withUsername(username)
				.withSecurityToken(securityToken);
		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path("applications")
    			.path(id)
    			.path("environment")
    			.body(environment)
				.execute();

		if (httpResponse.getStatusCode() != Status.OK) {
			ExceptionResponse error = httpResponse.getEntity(ExceptionResponse.class);
			
			Map<String, Object> model = getModel();
			model.put("id", id);
			model.put("mode", "add");
			model.put("action", String.format("/app/applications/%s/environments", id));
			model.put("environment", environment);
			model.put("errorMessage", error.getMessage());
			
			String output = render(request, model, Path.Template.ENVIRONMENT);
			
			throw new BadRequestException(output);
		}

		response.cookie(Path.Route.APPLICATION_VIEW.replace(":id", id), "successMessage", MessageProvider.getMessage(getDefaultLocale(getAccount(request)), "add.environment.success"), 3, Boolean.FALSE);
		response.redirect(Path.Route.APPLICATION_VIEW.replace(":id", id));
		
		return "";		
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * updateEnvironment
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */

	public Route updateEnvironment = (Request request, Response response) -> {		
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		String active = request.queryParams("active");
		String authEndpoint = request.queryParams("authEndpoint");
		String environmentName = request.queryParams("environmentName");
		String password = request.queryParams("password");
		String username = request.queryParams("username");
		String securityToken = request.queryParams("securityToken");
		
		Environment environment = new Environment()
				.withIsActive(Boolean.valueOf(active))
				.withAuthEndpoint(authEndpoint)
				.withEnvironmentName(environmentName)
				.withPassword(password)
				.withUsername(username)
				.withSecurityToken(securityToken);
		
		HttpResponse httpResponse = RestResource.put(API_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path("applications")
    			.path(id)
    			.path("environment")
    			.path(key)
    			.body(environment)
				.execute();
		
		if (httpResponse.getStatusCode() != Status.OK) {
			ExceptionResponse error = httpResponse.getEntity(ExceptionResponse.class);
			
			Map<String, Object> model = getModel();
			model.put("id", id);
			model.put("mode", "edit");
			model.put("action", String.format("/app/applications/%s/environments/%s", id, key));
			model.put("environment", environment);
			model.put("errorMessage", error.getMessage());
			
			String output = render(request, model, Path.Template.ENVIRONMENT);
			
			throw new BadRequestException(output);
		}
		
		response.cookie("successMessage", MessageProvider.getMessage(getDefaultLocale(getAccount(request)), "update.environment.success"), 3);
		response.redirect(Path.Route.APPLICATION_VIEW.replace(":id", id));
		
		return "";		
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * removeEnvironment
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route removeEnvironment = (Request request, Response response) -> {		
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("applications")
    			.path(id)
    			.path("environment")
    			.path(key)
				.execute();
		
		if (httpResponse.getStatusCode() != Status.OK) {
			ExceptionResponse error = httpResponse.getEntity(ExceptionResponse.class);
			throw new BadRequestException(error.getMessage());
		}
		
		response.cookie("successMessage", MessageProvider.getMessage(getDefaultLocale(getAccount(request)), "remove.environment.success"), 3);
		response.header("Location", Path.Route.APPLICATION_VIEW.replace(":id", id));
		
		return "";
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * testConnection
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */

	public Route testConnection = (Request request, Response response) -> {	
		Token token = getToken(request);
		
		AccountProfile accountProfile = getAccount(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
    			.path("applications")
				.path(id)
				.path("environment")
    			.path(key)
				.parameter("test", Boolean.TRUE.toString())
    			.execute();
		
		if (httpResponse.getStatusCode() != Status.OK) {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		Environment environment = httpResponse.getEntity(Environment.class);
		
		if (environment.getIsValid()) {
			environment.setTestMessage(MessageProvider.getMessage(getDefaultLocale(accountProfile), "test.connection.success"));
		} else {
			environment.setTestMessage(String.format("%s: %s", MessageProvider.getMessage(getDefaultLocale(accountProfile), "test.connection.fail"), environment.getTestMessage()));
		}
		
		return objectMapper.writeValueAsString(environment);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * viewServiceInstance
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route viewServiceInstance = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path("applications")
    			.path(id)
    			.path("service")
    			.path(key)
    			.execute();
		
		ServiceInstance serviceInstance = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			serviceInstance = httpResponse.getEntity(ServiceInstance.class);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		Map<String, Object> model = getModel();
		model.put("id", id);
		model.put("mode", "view");
		model.put("serviceInstance", serviceInstance);
		model.put("successMessage", request.cookie("successMessage"));
		
		System.out.println(serviceInstance.getConfigurationPage());
		
		return render(request, model, String.format(Path.APPLICATION_CONTEXT, serviceInstance.getConfigurationPage()));
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * newServiceInstance
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */

	public Route newServiceInstance = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		List<ServiceProvider> providers = Collections.emptyList();
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("providers")
				.queryParameter("localeSidKey", "en_US")
				.queryParameter("languageLocaleKey", "en_US")
				.execute();
			
		if (httpResponse.getStatusCode() == Status.OK) {
			providers = httpResponse.getEntityList(ServiceProvider.class);
		} else {
			throw new BadRequestException(httpResponse.getAsString());
		}
    	
		Map<String, Object> model = getModel();
		model.put("serviceProviders", providers);
		model.put("id", id);
		model.put("mode", "add");
    	
		return render(request, model, Path.Template.SERVICE_CATALOG);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * addServiceInstance
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route addServiceInstance = (Request request, Response response) -> {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String serviceKey = request.queryParams("serviceKey");
		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.path("applications")
				.path(id)
				.path("service")
				.parameter("key", serviceKey)
				.execute();
		
		if (httpResponse.getStatusCode() != Status.OK) {
			ExceptionResponse error = httpResponse.getEntity(ExceptionResponse.class);
			
			List<ServiceProvider> providers = Collections.emptyList();
			
			httpResponse = RestResource.get(API_ENDPOINT)
					.bearerAuthorization(token.getAccessToken())
					.path("providers")
					.queryParameter("localeSidKey", "en_US")
					.queryParameter("languageLocaleKey", "en_US")
					.execute();
				
			if (httpResponse.getStatusCode() == Status.OK) {
				providers = httpResponse.getEntityList(ServiceProvider.class);
			} else {
				throw new BadRequestException(httpResponse.getAsString());
			}
			
			Map<String, Object> model = getModel();
			model.put("id", id);
			model.put("serviceProviders", providers);
			model.put("errorMessage", error.getMessage());
	    	
			return render(request, model, Path.Template.SERVICE_CATALOG);
		}
		
		response.cookie(Path.Route.APPLICATION_VIEW.replace(":id", id), "successMessage", MessageProvider.getMessage(getDefaultLocale(getAccount(request)), "add.service.success"), 3, Boolean.FALSE);
		response.redirect(Path.Route.APPLICATION_VIEW.replace(":id", id));
		
		return "";		
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * editServiceInstance
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route editServiceInstance = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path("applications")
    			.path(id)
    			.path("service")
    			.path(key)
    			.execute();
		
		ServiceInstance serviceInstance = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			serviceInstance = httpResponse.getEntity(ServiceInstance.class);
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		Map<String, Object> model = getModel();
		model.put("id", id);
		model.put("mode", "edit");
		model.put("action", String.format("/app/applications/%s/services/%s", id, key));
		model.put("serviceInstance", serviceInstance);
		model.put("errorMessage", request.cookie("errorMessage"));
		
		return render(request, model, String.format(Path.APPLICATION_CONTEXT, serviceInstance.getConfigurationPage()));
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * updateServiceInstance
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route updateServiceInstance = (Request request, Response response) -> {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String key = request.params(":key");
		String name = request.queryParams("name");
		String tag = request.queryParams("tag");
		String bucketName = request.queryParams("bucketName");
		String awsAccessKey = request.queryParams("awsAccessKey");
		String awsSecretAccessKey = request.queryParams("awsSecretAccessKey");
		
		PostRequest httpRequest = RestResource.post(API_ENDPOINT)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path("applications")
				.path(id)
				.path("service")
				.path(key);
		
		if (name != null) {
			httpRequest.parameter("name", name);
		}
		
		if (tag != null) {
			httpRequest.parameter("tag", tag);
		}
		
		if (bucketName != null) {
			httpRequest.parameter("bucketName", bucketName);
		}
		
		if (awsAccessKey != null) {
			httpRequest.parameter("awsAccessKey", awsAccessKey);
		}
		
		if (awsSecretAccessKey != null) {
			httpRequest.parameter("awsSecretAccessKey", awsSecretAccessKey);
		}
				
		HttpResponse httpResponse = httpRequest.execute();
		
		if (httpResponse.getStatusCode() != Status.OK) {
			ExceptionResponse error = httpResponse.getEntity(ExceptionResponse.class);
			response.cookie("errorMessage", error.getMessage(), 3, Boolean.FALSE);
			response.redirect(Path.Route.APPLICATION_VIEW.replace(":id", id).concat("/services/:key/edit".replace(":key", key)));
			return "";
		}

		response.cookie(Path.Route.APPLICATION_VIEW.replace(":id", id), "successMessage", MessageProvider.getMessage(getDefaultLocale(getAccount(request)), "update.service.success"), 3, Boolean.FALSE);
		response.redirect(Path.Route.APPLICATION_VIEW.replace(":id", id));
		
		return "";		
	};
}