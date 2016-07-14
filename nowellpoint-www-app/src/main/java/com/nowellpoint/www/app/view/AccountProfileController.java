package com.nowellpoint.www.app.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.AccountProfile;
import com.nowellpoint.www.app.model.Address;
import com.nowellpoint.www.app.model.Contact;
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
		
		// credit card routes
		
		get("/app/account-profile/:id/payment-methods/:token/view", (request, response) -> getCreditCard(request, response), new FreeMarkerEngine(configuration));
		
		get("/app/account-profile/:id/payment-methods/add", (request, response) -> newCreditCard(request, response), new FreeMarkerEngine(configuration));
		
		get("/app/account-profile/:id/payment-methods/:token/edit", (request, response) -> editCreditCard(request, response), new FreeMarkerEngine(configuration));
			
		post("/app/account-profile/:id/payment-methods", (request, response) -> addCreditCard(request, response), new FreeMarkerEngine(configuration));
		
		post("/app/account-profile/:id/payment-methods/:token", (request, response) -> updateCreditCard(request, response), new FreeMarkerEngine(configuration));
		
		delete("/app/account-profile/:id/payment-methods/:token", (request, response) -> removeCreditCard(request, response));
		
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
	
	private ModelAndView updateAccountProfile(Request request, Response response) throws IOException {
		
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
	
	private ModelAndView getCreditCard(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.header("x-api-key", API_KEY)
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.path(request.params(":id"))
				.path("credit-card")
				.path(request.params(":token"))
				.execute();
			
		if (httpResponse.getStatusCode() != Status.OK) {
			throw new NotFoundException(httpResponse.getAsString());
		}
		
		CreditCard creditCard = httpResponse.getEntity(CreditCard.class);
		
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("creditCard", creditCard);
		model.put("mode", "view");
			
		return new ModelAndView(model, "secure/payment-method.html");	
	}
	
	private ModelAndView newCreditCard(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.header("x-api-key", API_KEY)
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.path(request.params(":id"))
				.path("credit-card")
				.execute();
			
		if (httpResponse.getStatusCode() != Status.OK) {
			throw new NotFoundException(httpResponse.getAsString());
		}
			
		AccountProfile accountProfile = httpResponse.getEntity(AccountProfile.class);
			
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("creditCard", new CreditCard());
		model.put("accountProfile", accountProfile);
		model.put("action", String.format("/app/account-profile/%s/payment-methods/", request.params(":id")));
		model.put("mode", "add");
			
		return new ModelAndView(model, "secure/payment-method.html");	
	}
	
	private ModelAndView editCreditCard(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.header("x-api-key", API_KEY)
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.path(request.params(":id"))
				.path("credit-card")
				.path(request.params(":token"))
				.execute();
			
		if (httpResponse.getStatusCode() != Status.OK) {
			throw new NotFoundException(httpResponse.getAsString());
		}
		
		CreditCard creditCard = httpResponse.getEntity(CreditCard.class);
		
		AccountProfile accountProfile = new AccountProfile();
		accountProfile.setId(request.params(":id"));
		
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("accountProfile", accountProfile);
		model.put("creditCard", creditCard);
		model.put("action", String.format("/app/account-profile/%s/payment-methods/%s", request.params(":id"), request.params(":token")));
		model.put("mode", "edit");
			
		return new ModelAndView(model, "secure/payment-method.html");	
	}
	
	private ModelAndView addCreditCard(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		String cardholderName = request.queryParams("cardholderName");
		String number = request.queryParams("number");
		String expirationMonth = request.queryParams("expirationMonth");
		String expirationYear = request.queryParams("expirationYear");
		String city = request.queryParams("city");
		String countryCode = request.queryParams("countryCode");
		String postalCode = request.queryParams("postalCode");
		String state = request.queryParams("state");
		String street = request.queryParams("street");
		String firstName = request.queryParams("firstName");
		String lastName = request.queryParams("lastName");
		
		CreditCard creditCard = new CreditCard()
				.withBillingAddress(new Address()
						.withCity(city)
						.withCountryCode(countryCode)
						.withPostalCode(postalCode)
						.withState(state)
						.withStreet(street))
				.withBillingContact(new Contact()
						.withFirstName(firstName)
						.withLastName(lastName))
				.withCardholderName(cardholderName)
				.withExpirationMonth(expirationMonth)
				.withExpirationYear(expirationYear)
				.withNumber(number);
		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-api-key", API_KEY)
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.path(request.params(":id"))
				.path("credit-card")
				.body(creditCard)
				.execute();
		
		Map<String, Object> model = getModel();
			
		if (httpResponse.getStatusCode() == Status.OK) {
			creditCard = httpResponse.getEntity(CreditCard.class);
			
			model.put("account", account);
			model.put("creditCard", creditCard);
			model.put("mode", "view");
			model.put("successMessage", getValue("add.success"));
		} else {
			model.put("errorMessage", httpResponse.getAsString());
		}
			
		return new ModelAndView(model, "secure/payment-method.html");	
	}
	
	private ModelAndView updateCreditCard(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		String cardholderName = request.queryParams("cardholderName");
		String number = request.queryParams("number");
		String expirationMonth = request.queryParams("expirationMonth");
		String expirationYear = request.queryParams("expirationYear");
		String city = request.queryParams("city");
		String countryCode = request.queryParams("countryCode");
		String postalCode = request.queryParams("postalCode");
		String state = request.queryParams("state");
		String street = request.queryParams("street");
		String firstName = request.queryParams("firstName");
		String lastName = request.queryParams("lastName");
		
		CreditCard creditCard = new CreditCard()
				.withBillingAddress(new Address()
						.withCity(city)
						.withCountryCode(countryCode)
						.withPostalCode(postalCode)
						.withState(state)
						.withStreet(street))
				.withBillingContact(new Contact()
						.withFirstName(firstName)
						.withLastName(lastName))
				.withCardholderName(cardholderName)
				.withExpirationMonth(expirationMonth)
				.withExpirationYear(expirationYear)
				.withNumber(number);
		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.header("x-api-key", API_KEY)
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.path(request.params(":id"))
				.path("credit-card")
				.path(request.params(":token"))
				.body(creditCard)
				.execute();
		
		Map<String, Object> model = getModel();
			
		if (httpResponse.getStatusCode() == Status.OK) {
			creditCard = httpResponse.getEntity(CreditCard.class);
			
			model.put("account", account);
			model.put("creditCard", creditCard);
			model.put("mode", "view");
			model.put("successMessage", getValue("update.success"));
		} else {
			model.put("errorMessage", httpResponse.getAsString());
		}
			
		return new ModelAndView(model, "secure/payment-method.html");	
	}
	
	private String removeCreditCard(Request request, Response response) {
		Token token = getToken(request);
		
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.header("x-api-key", API_KEY)
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.path(request.params(":id"))
				.path("credit-card")
				.path(request.params(":token"))
				.execute();
			
		if (httpResponse.getStatusCode() != Status.OK) {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		return "";
	}
}