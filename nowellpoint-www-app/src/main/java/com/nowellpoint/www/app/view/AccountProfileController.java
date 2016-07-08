package com.nowellpoint.www.app.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.AccountProfile;
import com.nowellpoint.www.app.model.CreditCard;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class AccountProfileController extends AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(AccountProfileController.class.getName());
	
	public AccountProfileController(Configuration configuration) {
		super(AccountProfileController.class, configuration);
	}
	
	/**
	 * 
	 */
	
	public void configureRoutes(Configuration configuration) {
		get("/app/account-profile", (request, response) -> getAccountProfile(request, response), new FreeMarkerEngine(configuration));
		
		post("/app/account-profile", (request, response) -> updateAccountProfile(request, response), new FreeMarkerEngine(configuration));
		
		get("/app/payment-methods", (request, response) -> getPaymentMethods(request, response), new FreeMarkerEngine(configuration));
		
		post("/app/payment-methods", (request, response) -> addCreditCard(request, response), new FreeMarkerEngine(configuration));
		
		delete("/app/payment-methods", (request, response) -> removeCreditCard(request, response), new FreeMarkerEngine(configuration));
		
		/////////////////////////////////

		post("/app/account-profile/picture/salesforce", (request, response) -> setSalesforceProfilePicture(request, response));
		
		delete("/app/account-profile/picture", (request, response) -> removeProfilePicture(request, response));
		
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private ModelAndView getAccountProfile(Request request, Response response) throws IOException {
		
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.header("x-api-key", API_KEY)
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.path("me")
				.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
			
		if (httpResponse.getStatusCode() != Status.OK) {
			throw new NotFoundException(httpResponse.getAsString());
		}
			
		AccountProfile accountProfile = httpResponse.getEntity(AccountProfile.class);
			
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("accountProfile", accountProfile);
			
		return new ModelAndView(model, "secure/account-profile.html");			
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	public ModelAndView updateAccountProfile(Request request, Response response) throws IOException {
		
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		StringBuilder body = new StringBuilder();
		request.queryParams().stream().filter(param -> ! request.queryParams(param).isEmpty()).limit(1).forEach(param -> {
			body.append(param).append("=").append(request.queryParams(param));
		});
		request.queryParams().stream().filter(param -> ! request.queryParams(param).isEmpty()).skip(1).forEach(param -> {
			body.append("&").append(param).append("=").append(request.queryParams(param));
    	});

		HttpResponse httpResponse = RestResource.put(API_ENDPOINT)
				.header("x-api-key", API_KEY)
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
    			.acceptCharset("UTF-8")
				.path("account-profile")
				.body(body.toString())
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo() + " : " + httpResponse.getHeaders().get("Location"));
		
		if (httpResponse.getStatusCode() != Status.OK && httpResponse.getStatusCode() != Status.CREATED) {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		AccountProfile accountProfile = httpResponse.getEntity(AccountProfile.class);
		
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("accountProfile", accountProfile);
		
		return new ModelAndView(model, "secure/account-profile.html");		
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private String setSalesforceProfilePicture(Request request, Response response) throws IOException {
		
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.header("x-api-key", API_KEY)
    			.bearerAuthorization(token.getAccessToken())
    			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
        		.acceptCharset("UTF-8")
        		.path("account-profile")
        		.path("photo")
        		.path("salesforce")
        		.body("photoUrl=".concat(request.queryParams("photoUrl")))
        		.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL() + " Location: " + httpResponse.getHeaders().get("Location"));
		
		Map<String, Object> model = getModel();
		model.put("account", account);
		
		return "";	
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private ModelAndView removeProfilePicture(Request request, Response response) {
		
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
				.header("x-api-key", API_KEY)
    			.bearerAuthorization(token.getAccessToken())
        		.path("account-profile")
        		.path("photo")
        		.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL() + " Location: " + httpResponse.getHeaders().get("Location"));
		
		AccountProfile accountProfile = httpResponse.getEntity(AccountProfile.class);
		
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("accountProfile", accountProfile);
		
		return new ModelAndView(model, "secure/account-profile.html");		
	}
	
	private ModelAndView getPaymentMethods(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.header("x-api-key", API_KEY)
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.path("me")
				.execute();
			
		if (httpResponse.getStatusCode() != Status.OK) {
			throw new NotFoundException(httpResponse.getAsString());
		}

		AccountProfile accountProfile = httpResponse.getEntity(AccountProfile.class);
			
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("accountProfile", accountProfile);
			
		return new ModelAndView(model, "secure/payment-methods.html");	
	}
	
	private ModelAndView addCreditCard(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		String accountProfileId = request.queryParams("accountProfileId");
		String firstName = request.queryParams("firstName");
		String lastName = request.queryParams("lastName");
		String number = request.queryParams("number");
		String month = request.queryParams("month");
		String year = request.queryParams("year");
		String city = request.queryParams("city");
		String countryCode = request.queryParams("countryCode");
		String postalCode = request.queryParams("postalCode");
		String state = request.queryParams("state");
		String street = request.queryParams("street");
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		JsonNode node = objectMapper.createObjectNode()
				.put("firstName", firstName)
				.put("lastName", lastName)
				.put("number", number)
				.put("month", month)
				.put("year", year)
				.set("address",  objectMapper.createObjectNode()
						.put("city", city)
						.put("countryCode", countryCode)
						.put("postalCode", postalCode)
						.put("state", state)
						.put("street", street));
		
		System.out.println(node);
		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-api-key", API_KEY)
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.path(accountProfileId)
				.path("credit-card")
				.body(node)
				.execute();
		
		System.out.println(httpResponse.getURL());
		System.out.println(httpResponse.getStatusCode());
			
		if (httpResponse.getStatusCode() != Status.OK) {
			throw new NotFoundException(httpResponse.getAsString());
		}
			
		AccountProfile accountProfile = httpResponse.getEntity(AccountProfile.class);
		accountProfile.setCreditCard(new CreditCard());
			
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("accountProfile", accountProfile);
			
		return new ModelAndView(model, "secure/payment-methods.html");	
	}
	
	private ModelAndView removeCreditCard(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.header("x-api-key", API_KEY)
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.path("me")
				.execute();
			
		if (httpResponse.getStatusCode() != Status.OK) {
			throw new NotFoundException(httpResponse.getAsString());
		}
			
		AccountProfile accountProfile = httpResponse.getEntity(AccountProfile.class);
		accountProfile.setCreditCard(new CreditCard());
			
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("accountProfile", accountProfile);
			
		return new ModelAndView(model, "secure/payment-methods.html");	
	}
}