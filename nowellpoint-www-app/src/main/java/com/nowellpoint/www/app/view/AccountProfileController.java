package com.nowellpoint.www.app.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import com.nowellpoint.aws.http.HttpRequestException;
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
import com.nowellpoint.www.app.model.ExceptionResponse;

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
		
		//
		// account profile routes
		//
		
		get("/app/account-profile", (request, response) -> getMyAccountProfile(request, response), new FreeMarkerEngine(configuration));
		
		get("/app/account-profile/:id", (request, response) -> getAccountProfile(request, response), new FreeMarkerEngine(configuration));
		
		get("/app/account-profile/:id/edit", (request, response) -> editAccountProfile(request, response), new FreeMarkerEngine(configuration));
		
		get("/app/account-profile/:id/disable", (request, response) -> disableAccountProfile(request, response));
		
		post("/app/account-profile/:id", (request, response) -> updateAccountProfile(request, response));
		
		//
		// account address routes
		//
		
		get("/app/account-profile/:id/address", (request, response) -> editAccountProfileAddress(request, response), new FreeMarkerEngine(configuration));
		
		post("/app/account-profile/:id/address", (request, response) -> updateAccountProfileAddress(request, response));
		
		// credit card routes
		
		get("/app/account-profile/:id/payment-methods/:token/view", (request, response) -> getCreditCard(request, response), new FreeMarkerEngine(configuration));
		
		get("/app/account-profile/:id/payment-methods/add", (request, response) -> newCreditCard(request, response), new FreeMarkerEngine(configuration));
		
		get("/app/account-profile/:id/payment-methods/:token/edit", (request, response) -> editCreditCard(request, response), new FreeMarkerEngine(configuration));
		
		post("/app/account-profile/:id/payment-methods/:token/primary", (request, response) -> setPrimaryCreditCard(request, response));
		
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
	
	private ModelAndView getMyAccountProfile(Request request, Response response) {
		
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
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
	 */
	
	private ModelAndView getAccountProfile(Request request, Response response) {
		
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.path(request.params(":id"))
				.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
			
		if (httpResponse.getStatusCode() != Status.OK) {
			throw new NotFoundException(httpResponse.getAsString());
		}
			
		AccountProfile accountProfile = httpResponse.getEntity(AccountProfile.class);
			
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("accountProfile", accountProfile);
		model.put("successMessage", request.cookie("successMessage"));
			
		return new ModelAndView(model, "secure/account-profile.html");			
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private ModelAndView editAccountProfile(Request request, Response response) {
		
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.path(request.params(":id"))
				.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
			
		if (httpResponse.getStatusCode() != Status.OK) {
			throw new NotFoundException(httpResponse.getAsString());
		}
			
		AccountProfile accountProfile = httpResponse.getEntity(AccountProfile.class);
			
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("accountProfile", accountProfile);
			
		return new ModelAndView(model, "secure/account-profile-edit.html");			
	}
	
	private ModelAndView editAccountProfileAddress(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.path(request.params(":id"))
				.path("address")
				.execute();
			
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
			
		if (httpResponse.getStatusCode() != Status.OK) {
			throw new NotFoundException(httpResponse.getAsString());
		}
			
		Address address = httpResponse.getEntity(Address.class);
			
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("accountProfile", new AccountProfile(request.params(":id")));
		model.put("address", address);
			
		return new ModelAndView(model, "secure/account-profile-address-edit.html");		
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private String disableAccountProfile(Request request, Response response) throws IOException {
		Token token = getToken(request);
		
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.path(request.params(":id"))
				.execute();
			
		if (httpResponse.getStatusCode() != Status.OK) {
			ExceptionResponse error = httpResponse.getEntity(ExceptionResponse.class);
			throw new BadRequestException(error.getMessage());
		}
		
		httpResponse = RestResource.delete(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
    			.path("oauth")
    			.path("token")
    			.execute();
    	
    	int statusCode = httpResponse.getStatusCode();
    	
    	if (statusCode != Status.NO_CONTENT) {
    		ExceptionResponse error = httpResponse.getEntity(ExceptionResponse.class);
			throw new BadRequestException(error.getMessage());
    	}
    	
    	response.removeCookie("com.nowellpoint.oauth.token"); 
    	
    	request.session().invalidate();
    	
    	response.redirect("/");
		
		return "";
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private String updateAccountProfile(Request request, Response response) {
		
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		AccountProfile accountProfile = new AccountProfile()
				.withId(request.params(":id"))
				.withFirstName(request.queryParams("firstName"))
				.withLastName(request.queryParams("lastName"))
				.withCompany(request.queryParams("company"))
				.withDivision(request.queryParams("division"))
				.withDepartment(request.queryParams("department"))
				.withTitle(request.queryParams("title"))
				.withEmail(request.queryParams("email"))
				.withFax(request.queryParams("fax"))
				.withMobilePhone(request.queryParams("mobilePhone"))
				.withPhone(request.queryParams("phone"))
				.withExtension(request.queryParams("extension"));

		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path("account-profile")
				.path(request.params(":id"))
				.parameter("firstName", accountProfile.getFirstName())
				.parameter("lastName", accountProfile.getLastName())
				.parameter("company", accountProfile.getCompany())
				.parameter("division", accountProfile.getDivision())
				.parameter("department", accountProfile.getDepartment())
				.parameter("title", accountProfile.getTitle())
				.parameter("email", accountProfile.getEmail())
				.parameter("fax", accountProfile.getFax())
				.parameter("mobilePhone", accountProfile.getMobilePhone())
				.parameter("phone", accountProfile.getPhone())
				.parameter("extension", accountProfile.getExtension())
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		if (httpResponse.getStatusCode() != Status.OK && httpResponse.getStatusCode() != Status.CREATED) {
			ExceptionResponse error = httpResponse.getEntity(ExceptionResponse.class);
			
			Map<String, Object> model = getModel();
			model.put("account", account);
			model.put("accountProfile", accountProfile);
			model.put("errorMessage", error.getMessage());
			
			String output = buildTemplate(new ModelAndView(model, "secure/account-profile-edit.html"));
			
			throw new BadRequestException(output);	
		}
		
		response.cookie("successMessage", getValue("update.profile.success"), 3);
		response.redirect(String.format("/app/account-profile/%s", request.params(":id")));
		
		return "";	
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String updateAccountProfileAddress(Request request, Response response) {
		
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		Address address = new Address()
				.withCity(request.queryParams("city"))
				.withCountryCode(request.queryParams("countryCode"))
				.withPostalCode(request.queryParams("postalCode"))
				.withState(request.queryParams("state"))
				.withStreet(request.queryParams("street"));
		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.contentType(MediaType.APPLICATION_JSON)
    			.acceptCharset("UTF-8")
				.path("account-profile")
				.path(request.params(":id"))
				.path("address")
				.body(address)
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL());
		
		if (httpResponse.getStatusCode() != Status.OK) {
			ExceptionResponse error = httpResponse.getEntity(ExceptionResponse.class);
			
			Map<String, Object> model = getModel();
			model.put("account", account);
			model.put("accountProfile", new AccountProfile(request.params(":id")));
			model.put("address", address);
			model.put("errorMessage", error.getMessage());
			
			String output = buildTemplate(new ModelAndView(model, "secure/account-profile-address-edit.html"));
			
			throw new BadRequestException(output);
		}
		
		response.cookie("successMessage", getValue("update.address.success"), 3);
		response.redirect(String.format("/app/account-profile/%s", request.params(":id")));
		
		return "";		
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String setSalesforceProfilePicture(Request request, Response response) throws IOException {
		
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
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
    			.bearerAuthorization(token.getAccessToken())
        		.path("account-profile")
        		.path("photo")
        		.execute();
		
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
		model.put("accountProfile", new AccountProfile(request.params(":id")));
		model.put("creditCard", creditCard);
		model.put("mode", "view");
			
		return new ModelAndView(model, "secure/payment-method.html");	
	}
	
	private ModelAndView newCreditCard(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.path(request.params(":id"))
				.execute();
			
		if (httpResponse.getStatusCode() != Status.OK) {
			throw new NotFoundException(httpResponse.getAsString());
		}
			
		AccountProfile accountProfile = httpResponse.getEntity(AccountProfile.class);
		
		CreditCard creditCard = new CreditCard();
		creditCard.setExpirationMonth(String.valueOf(LocalDate.now().getMonthValue()));
		creditCard.setExpirationYear(String.valueOf(LocalDate.now().getYear()));
		creditCard.setBillingAddress(new Address());
		creditCard.setBillingContact(new Contact());
			
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("creditCard", creditCard);
		model.put("accountProfile", accountProfile);
		model.put("action", String.format("/app/account-profile/%s/payment-methods", request.params(":id")));
		model.put("mode", "add");
			
		return new ModelAndView(model, "secure/payment-method.html");	
	}
	
	private ModelAndView editCreditCard(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.path(request.params(":id"))
				.path("credit-card")
				.path(request.params(":token"))
				.execute();
			
		if (httpResponse.getStatusCode() != Status.OK) {
			ExceptionResponse error = httpResponse.getEntity(ExceptionResponse.class);
			throw new NotFoundException(error.getMessage());
		}
		
		CreditCard creditCard = httpResponse.getEntity(CreditCard.class);
		
		httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.path(request.params(":id"))
				.execute();
		
		AccountProfile accountProfile = httpResponse.getEntity(AccountProfile.class);
		
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
		Boolean primary = request.queryParams("primary") != null ? Boolean.TRUE : Boolean.FALSE;
		
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
				.withNumber(number)
				.withPrimary(primary);
		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.path(request.params(":id"))
				.path("credit-card")
				.body(creditCard)
				.execute();
		
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("accountProfile", new AccountProfile(request.params(":id")));
			
		if (httpResponse.getStatusCode() == Status.OK) {
			creditCard = httpResponse.getEntity(CreditCard.class);
			
			model.put("creditCard", creditCard);
			model.put("mode", "view");
			model.put("successMessage", getValue("add.credit.card.success"));
		} else {
			ExceptionResponse error = httpResponse.getEntity(ExceptionResponse.class);
			
			model.put("creditCard", creditCard);
			model.put("action", String.format("/app/account-profile/%s/payment-methods", request.params(":id")));
			model.put("mode", "add");
			model.put("errorMessage", error.getMessage());
		}
			
		return new ModelAndView(model, "secure/payment-method.html");	
	}
	
	private ModelAndView updateCreditCard(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
		
		String cardholderName = request.queryParams("cardholderName");
		String expirationMonth = request.queryParams("expirationMonth");
		String expirationYear = request.queryParams("expirationYear");
		String city = request.queryParams("city");
		String countryCode = request.queryParams("countryCode");
		String postalCode = request.queryParams("postalCode");
		String state = request.queryParams("state");
		String street = request.queryParams("street");
		String firstName = request.queryParams("firstName");
		String lastName = request.queryParams("lastName");
		Boolean primary = request.queryParams("primary") != null ? Boolean.TRUE : Boolean.FALSE;
		
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
				.withPrimary(primary);
		
		HttpResponse httpResponse = RestResource.put(API_ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.path(request.params(":id"))
				.path("credit-card")
				.path(request.params(":token"))
				.body(creditCard)
				.execute();
		
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("accountProfile", new AccountProfile(request.params(":id")));
			
		if (httpResponse.getStatusCode() == Status.OK) {
			creditCard = httpResponse.getEntity(CreditCard.class);
			
			model.put("creditCard", creditCard);
			model.put("mode", "view");
			model.put("successMessage", getValue("update.credit.card.success"));
		} else {
			ExceptionResponse error = httpResponse.getEntity(ExceptionResponse.class);
			
			model.put("creditCard", creditCard);
			model.put("action", String.format("/app/account-profile/%s/payment-methods/%s", request.params(":id"), request.params(":token")));
			model.put("mode", "edit");
			model.put("errorMessage", error.getMessage());
		}
			
		return new ModelAndView(model, "secure/payment-method.html");	
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws HttpRequestException
	 * @throws UnsupportedEncodingException
	 */
	
	private String setPrimaryCreditCard(Request request, Response response) throws HttpRequestException, UnsupportedEncodingException {
		
		Token token = getToken(request);
		
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path("account-profile")
				.path(request.params(":id"))
				.path("credit-card")
				.path(request.params(":token"))
				.parameter("primary", "true")
				.execute();
			
		if (httpResponse.getStatusCode() != Status.OK) {
			ExceptionResponse error = httpResponse.getEntity(ExceptionResponse.class);
			throw new BadRequestException(error.getMessage());
		}
		
		response.cookie(String.format("/app/account-profile/%s",  request.params(":id")), getValue("primary.credit.card.set"), "successMessage", 3, Boolean.FALSE);
		
		return "";
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String removeCreditCard(Request request, Response response) {
		Token token = getToken(request);
		
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.path(request.params(":id"))
				.path("credit-card")
				.path(request.params(":token"))
				.execute();
			
		if (httpResponse.getStatusCode() != Status.OK) {
			ExceptionResponse error = httpResponse.getEntity(ExceptionResponse.class);
			throw new BadRequestException(error.getMessage());
		}
		
		response.cookie(String.format("/app/account-profile/%s",  request.params(":id")), getValue("remove.credit.card.success"), "successMessage", 3, Boolean.FALSE);
		
		return "";
	}
}