package com.nowellpoint.www.app.view;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.auth.TokenCredentials;
import com.nowellpoint.client.model.AccountProfile;
import com.nowellpoint.client.model.AddCreditCardRequest;
import com.nowellpoint.client.model.AddResult;
import com.nowellpoint.client.model.Address;
import com.nowellpoint.client.model.Contact;
import com.nowellpoint.client.model.CreditCard;
import com.nowellpoint.client.model.ExceptionResponse;
import com.nowellpoint.client.model.Feature;
import com.nowellpoint.client.model.GetPlansRequest;
import com.nowellpoint.client.model.GetResult;
import com.nowellpoint.client.model.Plan;
import com.nowellpoint.client.model.Service;
import com.nowellpoint.client.model.SetResult;
import com.nowellpoint.client.model.SetSubscriptionRequest;
import com.nowellpoint.client.model.Subscription;
import com.nowellpoint.client.model.idp.Token;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.Path;
import com.nowellpoint.util.Assert;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.Request;
import spark.Response;
import spark.Route;

public class AccountProfileController extends AbstractController {
	
	private static final Logger LOGGER = Logger.getLogger(AccountProfileController.class.getName());
	
	public AccountProfileController(Configuration configuration) {
		super(AccountProfileController.class, configuration);
	}
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * listPlans
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route listPlans = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		AccountProfile account = getAccount(request);
		
		GetPlansRequest getPlansRequest = new GetPlansRequest()
				.withLanguageSidKey(account.getLanguageSidKey())
				.withLocaleSidKey(account.getLocaleSidKey());
		
		GetResult<List<Plan>> getResult = new NowellpointClient(new TokenCredentials(token))
				.plan()
				.getPlans(getPlansRequest);
		
		List<Plan> plans = getResult.getTarget();

		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("accountProfile", account);
		model.put("action", "listPlans");
		model.put("planTable", buildPlanTable(account, plans));
		model.put("locales", new TreeMap<String, String>(getLocales(account)));
		model.put("languages", getSupportedLanguages());
		model.put("timeZones", getTimeZones());
			
		return render(request, model, Path.Template.ACCOUNT_PROFILE_PLANS);		
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * reviewPlan
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route reviewPlan = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		AccountProfile account = getAccount(request);
		
		String planId = request.params(":planId");
		
		Plan plan = new NowellpointClient(new TokenCredentials(token))
				.plan()
				.get(planId)
				.getTarget();

		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("accountProfile", account);
		model.put("action", "reviewPlan");
		model.put("plan", plan);
			
		return render(request, model, Path.Template.ACCOUNT_PROFILE_PLANS);		
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * getAccountProfile
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route getAccountProfile = (Request request, Response response) -> {
		
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		GetResult<AccountProfile> getResult = new NowellpointClient(new TokenCredentials(token))
				.accountProfile()
				.get(id);
		
		String createdByHref = Path.Route.ACCOUNT_PROFILE.replace(":id", getResult.getTarget().getCreatedBy().getId());
		String lastModifiedByHref = Path.Route.ACCOUNT_PROFILE.replace(":id", getResult.getTarget().getLastModifiedBy().getId());
		
		Map<String, Object> model = getModel();
		model.put("accountProfile", getResult.getTarget());
		model.put("locales", getLocales(getResult.getTarget()));
		model.put("languages", getSupportedLanguages());
		model.put("createdByHref", createdByHref);
		model.put("lastModifiedByHref", lastModifiedByHref);
		
		if (getResult.getTarget().getId().equals(id)) {
			return render(request, model, Path.Template.ACCOUNT_PROFILE_ME);
		}

		return render(request, model, Path.Template.ACCOUNT_PROFILE);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * editAccountProfile
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route editAccountProfile = (Request request, Response response) -> {		
		AccountProfile account = getAccount(request);
			
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("accountProfile", account);
		model.put("locales", new TreeMap<String, String>(getLocales(account)));
		model.put("languages", getSupportedLanguages());
		model.put("timeZones", getTimeZones());
			
		return render(request, model, Path.Template.ACCOUNT_PROFILE_EDIT);		
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * setSubscription
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route setPlan = (Request request, Response response) -> {		
		Token token = getToken(request);
		
		AccountProfile accountProfile = getAccount(request);
		
		Boolean newAccount = Assert.isNull(accountProfile.getSubscription().getPlanId()); 
		
		String planId = request.params(":planId");
		
		String id = request.params(":id");
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
		String cvv = request.queryParams("cvv");
		Boolean primary = request.queryParams("primary") != null ? Boolean.TRUE : Boolean.FALSE;
		
		Map<String, Object> model = getModel();
		model.put("accountProfile", new AccountProfile(id));
		
		if (Assert.isNotNull(cardholderName) || Assert.isNotNull(number) || Assert.isNotNull(expirationMonth) || Assert.isNotNull(expirationYear) ||
				Assert.isNotNull(expirationYear) || Assert.isNotNull(city) || Assert.isNotNull(countryCode) || Assert.isNotNull(postalCode) ||
				Assert.isNotNull(state) || Assert.isNotNull(firstName) || Assert.isNotNull(lastName)) {
			
			AddCreditCardRequest addCreditCardRequest = new AddCreditCardRequest()
					.withAccountProfileId(id)
					.withCardholderName(cardholderName)
					.withExpirationMonth(expirationMonth)
					.withExpirationYear(expirationYear)
					.withNumber(number)
					.withCardholderName(cvv)
					.withPrimary(primary)
					.withCity(city)
					.withCountryCode(countryCode)
					.withPostalCode(postalCode)
					.withState(state)
					.withStreet(street)
					.withFirstName(firstName)
					.withLastName(lastName);
			
			AddResult<CreditCard> addResult = new NowellpointClient(new TokenCredentials(token))
					.accountProfile()
					.creditCard()
					.add(addCreditCardRequest);
				
			if (! addResult.isSuccess()) {
				
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
						.withCardholderName(cvv)
						.withPrimary(primary);
				
				model.put("creditCard", creditCard);
				model.put("action", String.format("/app/account-profile/%s/payment-methods", request.params(":id")));
				model.put("mode", "add");
				model.put("errorMessage", addResult.getErrorMessage());
				
				String output = render(request, model, Path.Template.ACCOUNT_PROFILE_PLANS);
				
				throw new BadRequestException(output);	
			}
		}
		
		SetSubscriptionRequest setSubscriptionRequest = new SetSubscriptionRequest()
				.withAccountProfileId(accountProfile.getId())
				.withPlanId(planId);
		
		SetResult<Subscription> setResult = new NowellpointClient(new TokenCredentials(token))
				.accountProfile()
				.subscription()
				.set(setSubscriptionRequest);
		
		if (! setResult.isSuccess()) {
			
			model.put("account", accountProfile);
			model.put("accountProfile", accountProfile);
			model.put("errorMessage", setResult.getErrorMessage());
			
			String output = render(request, model, Path.Template.ACCOUNT_PROFILE_PLANS);
			
			throw new BadRequestException(output);	
		}
		
		if (newAccount) {
			response.redirect(Path.Route.SCHEDULED_JOB_SELECT_TYPE);
		} else {
			response.cookie("successMessage", MessageProvider.getMessage(getDefaultLocale(accountProfile), "subscription.plan.update.success"), 3);
			response.redirect(Path.Route.ACCOUNT_PROFILE.replace(":id", request.params(":id")));
		}
		
		return "";	
	};	
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * editAddress
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route editAddress = (Request request, Response response) -> {		
		Token token = getToken(request);
		
		AccountProfile account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.path(request.params(":id"))
				.path("address")
				.execute();
			
		if (httpResponse.getStatusCode() != Status.OK) {
			throw new NotFoundException(httpResponse.getAsString());
		}
			
		Address address = httpResponse.getEntity(Address.class);
			
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("accountProfile", new AccountProfile(request.params(":id")));
		model.put("address", address);
			
		return render(request, model, Path.Template.ACCOUNT_PROFILE_ADDRESS_EDIT);		
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * deleteAccountProfile
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route deactivateAccountProfile = (Request request, Response response) -> {	
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
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * confirmDeactivateAccountProfile
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route confirmDeactivateAccountProfile = (Request request, Response response) -> {	
		Token token = getToken(request);
		
		AccountProfile account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("account-profile")
				.path(request.params(":id"))
				.execute();
			
		if (httpResponse.getStatusCode() != Status.OK) {
			throw new NotFoundException(httpResponse.getAsString());
		}
			
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("accountProfile", new AccountProfile(request.params(":id")));
			
		return render(request, model, Path.Template.ACCOUNT_PROFILE_DEACTIVATE);		
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * updateAccountProfile
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route updateAccountProfile = (Request request, Response response) -> {
		Token token = getToken(request);
		
		AccountProfile account = getAccount(request);
		
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
				.withExtension(request.queryParams("extension"))
				.withLanguageSidKey(request.queryParams("languageSidKey"))
				.withLocaleSidKey(request.queryParams("localeSidKey"))
				.withTimeZoneSidKey(request.queryParams("timeZoneSidKey"))
				.withEnableSalesforceLogin(request.queryParams("enableSalesforceLogin") != null ? Boolean.TRUE : Boolean.FALSE);

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
				.parameter("languageSidKey", accountProfile.getLanguageSidKey())
				.parameter("localeSidKey", accountProfile.getLocaleSidKey())
				.parameter("timeZoneSidKey", accountProfile.getTimeZoneSidKey())
				.parameter("enableSalesforceLogin", accountProfile.getEnableSalesforceLogin())
				.execute();
		
		LOGGER.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		if (httpResponse.getStatusCode() != Status.OK && httpResponse.getStatusCode() != Status.CREATED) {
			ExceptionResponse error = httpResponse.getEntity(ExceptionResponse.class);
			
			Map<String, Object> model = getModel();
			model.put("account", account);
			model.put("accountProfile", accountProfile);
			model.put("errorMessage", error.getMessage());
			
			String output = render(request, model, Path.Template.ACCOUNT_PROFILE_EDIT);
			
			throw new BadRequestException(output);	
		}
		
		response.cookie("successMessage", MessageProvider.getMessage(getDefaultLocale(accountProfile), "update.profile.success"), 3);
		response.redirect(String.format("/app/account-profile/%s", request.params(":id")));
		
		return "";	
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * updateAccountProfileAddress
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route updateAccountProfileAddress = (Request request, Response response) -> {
		Token token = getToken(request);
		
		AccountProfile accountProfile = getAccount(request);
		
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
			model.put("account", accountProfile);
			model.put("accountProfile", new AccountProfile(request.params(":id")));
			model.put("address", address);
			model.put("errorMessage", error.getMessage());
			
			String output = render(request, model, Path.Template.ACCOUNT_PROFILE_ADDRESS_EDIT);
			
			throw new BadRequestException(output);
		}
		
		response.cookie("successMessage", MessageProvider.getMessage(getDefaultLocale(accountProfile), "update.address.success"), 3);
		response.redirect(String.format("/app/account-profile/%s", request.params(":id")));
		
		return "";		
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * removeProfilePicture
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route removeProfilePicture = (Request request, Response response) -> {
		Token token = getToken(request);
		
		AccountProfile account = getAccount(request);
		
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
    			.bearerAuthorization(token.getAccessToken())
        		.path("account-profile")
        		.path(request.params(":id"))
        		.path("photo")
        		.execute();
		
		AccountProfile accountProfile = httpResponse.getEntity(AccountProfile.class);
		
		Map<String, Object> model = getModel();
		model.put("account", account);
		model.put("accountProfile", accountProfile);
		
		return render(request, model, Path.Template.ACCOUNT_PROFILE);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * getCreditCard
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route getCreditCard = (Request request, Response response) -> {
		Token token = getToken(request);
		
		AccountProfile account = getAccount(request);
		
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
		
		return render(request, model, Path.Template.ACCOUNT_PROFILE_PAYMENT_METHOD);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * newCreditCard
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route newCreditCard = (Request request, Response response) -> {
		Token token = getToken(request);
		
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
		model.put("creditCard", creditCard);
		model.put("accountProfile", accountProfile);
		model.put("action", String.format("/app/account-profile/%s/payment-methods", request.params(":id")));
		model.put("mode", "new");
		
		return render(request, model, Path.Template.ACCOUNT_PROFILE_PAYMENT_METHOD);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * editCreditCard
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route editCreditCard = (Request request, Response response) -> {
		Token token = getToken(request);
		
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
		model.put("accountProfile", accountProfile);
		model.put("creditCard", creditCard);
		model.put("action", String.format("/app/account-profile/%s/payment-methods/%s", request.params(":id"), request.params(":token")));
		model.put("mode", "edit");
		
		return render(request, model, Path.Template.ACCOUNT_PROFILE_PAYMENT_METHOD);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * addCreditCard
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route addCreditCard = (Request request, Response response) -> {
		Token token = getToken(request);
		
		AccountProfile accountProfile = getAccount(request);
		
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
		String cvv = request.queryParams("cvv");
		Boolean primary = request.queryParams("primary") != null ? Boolean.TRUE : Boolean.FALSE;
		
		AddCreditCardRequest addCreditCardRequest = new AddCreditCardRequest()
				.withCardholderName(cardholderName)
				.withExpirationMonth(expirationMonth)
				.withExpirationYear(expirationYear)
				.withNumber(number)
				.withCardholderName(cvv)
				.withPrimary(primary)
				.withCity(city)
				.withCountryCode(countryCode)
				.withPostalCode(postalCode)
				.withState(state)
				.withStreet(street)
				.withFirstName(firstName)
				.withLastName(lastName);
		
		AddResult<CreditCard> addResult = new NowellpointClient(new TokenCredentials(token))
				.accountProfile()
				.creditCard()
				.add(addCreditCardRequest);
		
		Map<String, Object> model = getModel();
		model.put("accountProfile", new AccountProfile(request.params(":id")));
			
		if (addResult.isSuccess()) {
			CreditCard creditCard = addResult.getTarget();
			
			model.put("creditCard", creditCard);
			model.put("mode", "view");
			model.put("successMessage", MessageProvider.getMessage(getDefaultLocale(accountProfile), "add.credit.card.success"));
		} else {	
			
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
					.withCardholderName(cvv)
					.withPrimary(primary);
			
			model.put("creditCard", creditCard);
			model.put("action", String.format("/app/account-profile/%s/payment-methods", request.params(":id")));
			model.put("mode", "add");
			model.put("errorMessage", addResult.getErrorMessage());
		}
		
		return render(request, model, Path.Template.ACCOUNT_PROFILE_PAYMENT_METHOD);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * updateCreditCard
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route updateCreditCard = (Request request, Response response) -> {
		Token token = getToken(request);
		
		AccountProfile accountProfile = getAccount(request);
		
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
		String cvv = request.queryParams("cvv");
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
				.withCardholderName(cvv)
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
		model.put("accountProfile", new AccountProfile(request.params(":id")));
			
		if (httpResponse.getStatusCode() == Status.OK) {
			creditCard = httpResponse.getEntity(CreditCard.class);
			
			model.put("creditCard", creditCard);
			model.put("mode", "view");
			model.put("successMessage", MessageProvider.getMessage(getDefaultLocale(accountProfile), "update.credit.card.success"));
		} else {
			ExceptionResponse error = httpResponse.getEntity(ExceptionResponse.class);
			
			model.put("creditCard", creditCard);
			model.put("action", String.format("/app/account-profile/%s/payment-methods/%s", request.params(":id"), request.params(":token")));
			model.put("mode", "edit");
			model.put("errorMessage", error.getMessage());
		}
			
		return render(request, model, Path.Template.ACCOUNT_PROFILE_PAYMENT_METHOD);
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * setPrimaryCreditCard
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route setPrimaryCreditCard = (Request request, Response response) -> {
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
		
		response.cookie(String.format("/app/account-profile/%s",  request.params(":id")), "successMessage", MessageProvider.getMessage(getDefaultLocale(getAccount(request)), "primary.credit.card.set"), 3, Boolean.FALSE);
		
		return "";
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * removeCreditCard
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	public Route removeCreditCard = (Request request, Response response) -> {
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
		
		response.cookie(String.format("/app/account-profile/%s",  request.params(":id")), "successMessage", MessageProvider.getMessage(getDefaultLocale(getAccount(request)), "remove.credit.card.success"), 3, Boolean.FALSE);
		
		return "";
	};
	
	/**
	 * 
	 * @param accountProfile
	 * @return Locale map 
	 */
	
	private Map<String,String> getLocales(AccountProfile accountProfile) {
		Locale.setDefault(new Locale(accountProfile.getLocaleSidKey()));
		
		Map<String,String> localeMap = Arrays.asList(Locale.getAvailableLocales())
				.stream()
				.collect(Collectors.toMap(l -> l.toString(), l -> l.getDisplayLanguage()
						.concat(! l.getCountry().isEmpty() ? " (".concat(l.getDisplayCountry().concat(")")) : "")));
		
		return localeMap;
	}
	
	/**
	 * 
	 * @return application supported languages
	 */
	
	private Map<String,String> getSupportedLanguages() {
		Map<String,String> languageMap = new HashMap<String,String>();
		languageMap.put(Locale.US.toString(), Locale.US.getDisplayLanguage());
		return languageMap;
	}
	
	/**
	 * 
	 * @return application supported timezones
	 */
	
	private List<String> getTimeZones() {
		return Arrays.asList(TimeZone.getAvailableIDs());
	}
	
	/**
	 * 
	 * 
	 * @param plans
	 * @return
	 * 
	 * 
	 */
	
	private static String buildPlanTable(AccountProfile accountProfile, List<Plan> plans) {

		//ResourceBundle bundle = ResourceBundle.getBundle(AccountProfileController.class.getName(), new Locale("en_US"));
		
		plans = plans.stream()
				.sorted((p1, p2) -> p1.getPrice().getUnitPrice().compareTo(p2.getPrice().getUnitPrice()))
				.collect(Collectors.toList());
		
		StringBuilder html = new StringBuilder();
		html.append("<div class='content table-responsive'>");
		html.append("<table id='plan-comparison' class='table'>");
		//html.append("<thead><th class='col-xs-4'>" + bundle.getString("features") + "</th>");
		html.append("<thead><th class='col-xs-4'>Features</th>");
		plans.stream().forEach(p -> {
			html.append("<th class='col-xs-4 text-center'>" + p.getPlanName() + "<br>" + p.getPrice().getCurrencySymbol() + "&nbsp;" + NumberFormat.getInstance(new Locale("en_US")).format(p.getPrice().getUnitPrice()) + "</th>");
		});
		html.append("</thead>");
		html.append("<tbody>");

		for (int j = 0; j < plans.get(0).getServices().size(); j++) {
			
			boolean newService = true;
			
			for (int k = 0; k < plans.get(0).getServices().get(0).getFeatures().size(); k++) {
				
				for (int i = 0; i < plans.size(); i++) {
					
					Service service = plans.get(i).getServices().stream().sorted((s1, s2) -> s1.getName().compareTo(s2.getName())).collect(Collectors.toList()).get(j);
					
					if (newService) {
						html.append("<tr>");
						html.append("<td class='active' colspan='" + String.valueOf(1 + plans.size()) + "'><h4>" + service.getName() + ": " + service.getDescription() + "</h4></td>");
						html.append("</tr>");
						newService = false;
					}
					
					Feature feature = service.getFeatures().stream().sorted((f1, f2) -> f1.getName().compareTo(f2.getName())).collect(Collectors.toList()).get(k);
					
					if (i == 0) {
						html.append("<tr>");
						html.append("<td>" + feature.getName() + "</td>");
					}
					
					html.append("<td class='text-center "  + (feature.getEnabled() ? "text-success" : "text-danger") + "'><span class='icon " + (feature.getEnabled() ? "icon icon-check" : "icon icon-cross") + "'></span></td>");
				}
			}
			
			html.append("</tr>");	
		}
		
		html.append("<tr>");
		html.append("<td></td>");
		plans.stream().forEach(p -> {
			html.append("<td class='text-center p-a'>");
			if (accountProfile.getSubscription() != null && p.getId().equals(accountProfile.getSubscription().getPlanId())) {
				//html.append("<p class='text-center text-success'>" + bundle.getString("current.subscription") + "</p>");
				html.append("<p class='text-center text-success'>Current Subscription</p>");
			} else {
				//html.append("<a class='btn btn-primary' role='button' href='" + Path.Route.ACCOUNT_PROFILE_PLAN.replace(":id", accountProfile.getId()).replace(":planId", p.getId()) + "' id='add-plan-" + p.getPlanCode().toLowerCase() + "'>" + bundle.getString("select") + "</a>");
				html.append("<a class='btn btn-primary' role='button' href='" + Path.Route.ACCOUNT_PROFILE_PLAN.replace(":id", accountProfile.getId()).replace(":planId", p.getId()) + "' id='add-plan-" + p.getPlanCode().toLowerCase() + "'>Select</a>");
			}
			html.append("</td>");
		});
		
		html.append("</tr>");
		html.append("</tbody>");
		html.append("</table>");
		html.append("</div>");

		return html.toString();
	}
}