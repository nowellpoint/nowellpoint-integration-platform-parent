package com.nowellpoint.www.app.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.AccountProfile;
import com.nowellpoint.client.model.AccountProfileRequest;
import com.nowellpoint.client.model.CreditCardRequest;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Address;
import com.nowellpoint.client.model.AddressRequest;
import com.nowellpoint.client.model.Contact;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.CreditCard;
import com.nowellpoint.client.model.GetPlansRequest;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Plan;
import com.nowellpoint.client.model.SubscriptionRequest;
import com.nowellpoint.client.model.Subscription;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.Path;
import com.nowellpoint.util.Assert;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class AccountProfileController extends AbstractController {
	
	public static class Template {
		public static final String ACCOUNT_PROFILE_ME = String.format(APPLICATION_CONTEXT, "account-profile-me.html");
		public static final String ACCOUNT_PROFILE = String.format(APPLICATION_CONTEXT, "account-profile.html");
		public static final String ACCOUNT_PROFILE_PLANS = String.format(APPLICATION_CONTEXT, "account-profile-plans.html");
		public static final String ACCOUNT_PROFILE_EDIT = String.format(APPLICATION_CONTEXT, "account-profile-edit.html");
		public static final String ACCOUNT_PROFILE_ADDRESS_EDIT = String.format(APPLICATION_CONTEXT, "account-profile-address-edit.html");
		public static final String ACCOUNT_PROFILE_DEACTIVATE = String.format(APPLICATION_CONTEXT, "account-profile-deactivate.html");
		public static final String ACCOUNT_PROFILE_PAYMENT_METHOD = String.format(APPLICATION_CONTEXT, "payment-method.html");
		public static final String ACCOUNT_PROFILE_CURRENT_PLAN = String.format(APPLICATION_CONTEXT, "account-profile-current-plan.html");
	}
	
	public AccountProfileController() {
		super(AccountProfileController.class);
	}
	
	@Override
	public void configureRoutes(Configuration configuration) {
		get(Path.Route.ACCOUNT_PROFILE_LIST_PLANS, (request, response) -> listPlans(configuration, request, response));
		get(Path.Route.ACCOUNT_PROFILE, (request, response) -> getAccountProfile(configuration, request, response));
		get(Path.Route.ACCOUNT_PROFILE_PLAN, (request, response) -> reviewPlan(configuration, request, response));
		get(Path.Route.ACCOUNT_PROFILE_CURRENT_PLAN, (request, response) -> currentPlan(configuration, request, response));
        post(Path.Route.ACCOUNT_PROFILE_PLAN, (request, response) -> setPlan(configuration, request, response));
        post(Path.Route.ACCOUNT_PROFILE, (request, response) -> updateAccountProfile(configuration, request, response));
        get(Path.Route.ACCOUNT_PROFILE_EDIT, (request, response) -> editAccountProfile(configuration, request, response));
        get(Path.Route.ACCOUNT_PROFILE_DEACTIVATE, (request, response) -> confirmDeactivateAccountProfile(configuration, request, response));
        post(Path.Route.ACCOUNT_PROFILE_DEACTIVATE, (request, response) -> deactivateAccountProfile(configuration, request, response));
        delete(Path.Route.ACCOUNT_PROFILE_PICTURE, (request, response) -> removeProfilePicture(configuration, request, response));
        get(Path.Route.ACCOUNT_PROFILE_ADDRESS, (request, response) -> editAddress(configuration, request, response));
        post(Path.Route.ACCOUNT_PROFILE_ADDRESS, (request, response) -> updateAccountProfileAddress(configuration, request, response));
        get(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/:token/view"), (request, response) -> getCreditCard(configuration, request, response));
        get(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/new"), (request, response) -> newCreditCard(configuration, request, response));
        get(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/:token/edit"), (request, response) -> editCreditCard(configuration, request, response));
        post(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS, (request, response) -> addCreditCard(configuration, request, response));  
        post(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/:token"), (request, response) -> updateCreditCard(configuration, request, response));
        post(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/:token/primary"), (request, response) -> setPrimaryCreditCard(configuration, request, response));
        delete(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/:token"), (request, response) -> removeCreditCard(configuration, request, response));
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String listPlans(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		Identity identity = getIdentity(request);
		
		String id = request.params(":id");
		
		AccountProfile accountProfile = new NowellpointClient(token)
				.accountProfile()
				.get(id);
		
		GetPlansRequest plansRequest = new GetPlansRequest()
				.withLanguageSidKey(identity.getLanguageSidKey())
				.withLocaleSidKey(identity.getLocaleSidKey());

		List<Plan> plans = new NowellpointClient(token).plan()
				.getPlans(plansRequest)
				.getItems()
				.stream()
				.sorted((p1, p2) -> p1.getPrice().getUnitPrice().compareTo(p2.getPrice().getUnitPrice()))
				.collect(Collectors.toList());

		Map<String, Object> model = getModel();
		model.put("account", identity);
		model.put("accountProfile", accountProfile);
		model.put("action", "listPlans");
		model.put("plans", plans);
		model.put("locales", new TreeMap<String, String>(getLocales(identity.getLocaleSidKey())));
		model.put("languages", getSupportedLanguages());
		model.put("timeZones", getTimeZones());

		return render(configuration, request, response, model, Template.ACCOUNT_PROFILE_PLANS);	
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String getAccountProfile(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		AccountProfile accountProfile = new NowellpointClient(token)
				.accountProfile()
				.get(id);
		
		String createdByHref = Path.Route.ACCOUNT_PROFILE.replace(":id", accountProfile.getCreatedBy().getId());
		String lastModifiedByHref = Path.Route.ACCOUNT_PROFILE.replace(":id", accountProfile.getLastModifiedBy().getId());
		
		Map<String, Object> model = getModel();
		model.put("accountProfile", accountProfile);
		model.put("locales", getLocales(accountProfile.getLocaleSidKey()));
		model.put("languages", getSupportedLanguages());
		model.put("createdByHref", createdByHref);
		model.put("lastModifiedByHref", lastModifiedByHref);
		model.put("successMessage", request.cookie("update.profile.success"));
		
		if (accountProfile.getId().equals(id)) {
			return render(configuration, request, response, model, Template.ACCOUNT_PROFILE_ME);
		}

		return render(configuration, request, response, model, Template.ACCOUNT_PROFILE);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String reviewPlan(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		Identity identity = getIdentity(request);
		
		String id = request.params(":id");
		
		AccountProfile accountProfile = new NowellpointClient(token)
				.accountProfile()
				.get(id);
		
		String planId = request.params(":planId");
		
		Plan plan = new NowellpointClient(token)
				.plan()
				.get(planId);
		
		Address address = new Address();
		address.setCountryCode(identity.getAddress().getCountryCode());
		
		CreditCard creditCard = new CreditCard();
		creditCard.setExpirationMonth(String.valueOf(LocalDate.now().getMonthValue()));
		creditCard.setExpirationYear(String.valueOf(LocalDate.now().getYear()));
		creditCard.setBillingAddress(address);
		creditCard.setBillingContact(new Contact());

		Map<String, Object> model = getModel();
		model.put("account", identity);
		model.put("accountProfile", accountProfile);
		model.put("creditCard", creditCard);
		model.put("action", "reviewPlan");
		model.put("plan", plan);
			
		return render(configuration, request, response, model, Template.ACCOUNT_PROFILE_PLANS);	
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String currentPlan(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		AccountProfile accountProfile = new NowellpointClient(token)
				.accountProfile()
				.get(id);
		
		Map<String, Object> model = getModel();
		model.put("accountProfile", accountProfile);
		
		return render(configuration, request, response, model, Template.ACCOUNT_PROFILE_CURRENT_PLAN);	
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String setPlan(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		Identity identity = getIdentity(request);
		
		String id = request.params(":id");
		
		AccountProfile accountProfile = new NowellpointClient(token)
				.accountProfile()
				.get(id);
		
		Boolean newAccount = Assert.isNull(accountProfile.getSubscription().getPlanId()); 
		
		String planId = request.params(":planId");
		String paymentMethodToken = request.queryParams("paymentMethodToken");
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
		
		if (Assert.isNotNull(cardholderName) || Assert.isNotNull(number)) {
			
			CreditCardRequest creditCardRequest = new CreditCardRequest()
					.withAccountProfileId(id)
					.withCardholderName(cardholderName)
					.withExpirationMonth(expirationMonth)
					.withExpirationYear(expirationYear)
					.withNumber(number)
					.withCvv(cvv)
					.withPrimary(primary)
					.withCity(city)
					.withCountryCode(countryCode)
					.withPostalCode(postalCode)
					.withState(state)
					.withStreet(street)
					.withFirstName(firstName)
					.withLastName(lastName);
			
			CreateResult<CreditCard> createResult = new NowellpointClient(token)
					.accountProfile()
					.creditCard()
					.add(creditCardRequest);
				
			if (createResult.isSuccess()) {
				paymentMethodToken = createResult.getTarget().getToken();
			} else { 
				
				Plan plan = new NowellpointClient(token)
						.plan()
						.get(planId);
				
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
				
				model.put("account", identity);
				model.put("accountProfile", accountProfile);
				model.put("plan", plan);
				model.put("creditCard", creditCard);
				model.put("action", "reviewPlan");
				model.put("errorMessage", createResult.getErrorMessage());
				
				return render(configuration, request, response, model, Template.ACCOUNT_PROFILE_PLANS);
			}
		}
		
		SubscriptionRequest subscriptionRequest = new SubscriptionRequest()
				.withAccountProfileId(accountProfile.getId())
				.withPaymentMethodToken(paymentMethodToken)
				.withPlanId(planId);
		
		UpdateResult<Subscription> updateResult = new NowellpointClient(token)
				.accountProfile()
				.subscription()
				.set(subscriptionRequest);
		
		if (! updateResult.isSuccess()) {
			
			model.put("account", identity);
			model.put("accountProfile", accountProfile);
			model.put("errorMessage", updateResult.getErrorMessage());
			
			String output = render(configuration, request, response, model, Template.ACCOUNT_PROFILE_PLANS);
			
			throw new BadRequestException(output);	
		}
		
		if (newAccount) {
			response.redirect(Path.Route.SCHEDULED_JOB_SELECT_TYPE);
		} else {
			response.cookie("successMessage", MessageProvider.getMessage(getLocale(request), "subscription.plan.update.success"), 3);
			response.redirect(Path.Route.ACCOUNT_PROFILE.replace(":id", request.params(":id")));
		}
		
		return "";	
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String updateAccountProfile(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		Identity identity = getIdentity(request);
		
		AccountProfileRequest accountProfileRequest = new AccountProfileRequest()
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

		UpdateResult<AccountProfile> updateResult = new NowellpointClient(token)
				.accountProfile()
				.update(request.params(":id"), accountProfileRequest);
		
		if (! updateResult.isSuccess()) {
			
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

			Map<String, Object> model = getModel();
			model.put("account", identity);
			model.put("accountProfile", accountProfile);
			model.put("locales", new TreeMap<String, String>(getLocales(identity.getLocaleSidKey())));
			model.put("languages", getSupportedLanguages());
			model.put("timeZones", getTimeZones());
			model.put("errorMessage", updateResult.getErrorMessage());
			
			String output = render(configuration, request, response, model, Template.ACCOUNT_PROFILE_EDIT);
			
			throw new BadRequestException(output);	
		}
		
		response.cookie("successMessage", MessageProvider.getMessage(getLocale(request), "update.profile.success"), 3);
		response.redirect(String.format("/app/account-profile/%s", request.params(":id")));
		
		return "";	
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String editAccountProfile(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		Identity identity = getIdentity(request);
		
		AccountProfile accountProfile = new NowellpointClient(token)
				.accountProfile()
				.get(request.params(":id"));
			
		Map<String, Object> model = getModel();
		model.put("account", identity);
		model.put("accountProfile", accountProfile);
		model.put("locales", new TreeMap<String, String>(getLocales(identity.getLocaleSidKey())));
		model.put("languages", getSupportedLanguages());
		model.put("timeZones", getTimeZones());
			
		return render(configuration, request, response, model, Template.ACCOUNT_PROFILE_EDIT);		
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String confirmDeactivateAccountProfile(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		Identity identity = getIdentity(request);
		
		AccountProfile accountProfile = new NowellpointClient(token)
				.accountProfile()
				.get(request.params(":id"));
			
		Map<String, Object> model = getModel();
		model.put("account", identity);
		model.put("accountProfile", accountProfile);
			
		return render(configuration, request, response, model, Template.ACCOUNT_PROFILE_DEACTIVATE);		
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String deactivateAccountProfile(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		DeleteResult deleteResult = new NowellpointClient(token)
				.accountProfile()
				.deactivate(request.params(":id"));
		
		if (! deleteResult.isSuccess()) {
			throw new BadRequestException(deleteResult.getErrorMessage());
		}
		
    	response.redirect(Path.Route.LOGOUT);
		
		return "";	
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String removeProfilePicture(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		Identity identity = getIdentity(request);
		
		UpdateResult<AccountProfile> updateResult = new NowellpointClient(token)
				.accountProfile()
				.removeProfilePicture(request.params(":id"));
		
		if (! updateResult.isSuccess()) {
			throw new BadRequestException(updateResult.getErrorMessage());
		}
		
		Map<String, Object> model = getModel();
		model.put("account", identity);
		model.put("accountProfile", updateResult.getTarget());
		
		return render(configuration, request, response, model, Template.ACCOUNT_PROFILE);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String editAddress(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		Identity identity = getIdentity(request);
		
		Address address = new NowellpointClient(token)
				.accountProfile()
				.address()
				.get(request.params(":id"));
		
		Map<String, Object> model = getModel();
		model.put("account", identity);
		model.put("accountProfile", new AccountProfile(request.params(":id")));
		model.put("address", address);
			
		return render(configuration, request, response, model, Template.ACCOUNT_PROFILE_ADDRESS_EDIT);	
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String updateAccountProfileAddress(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		Identity identity = getIdentity(request);
		
		AddressRequest addressRequest = new AddressRequest()
				.withCity(request.queryParams("city"))
				.withCountryCode(request.queryParams("countryCode"))
				.withPostalCode(request.queryParams("postalCode"))
				.withState(request.queryParams("state"))
				.withStreet(request.queryParams("street"));
		
		UpdateResult<Address> updateResult = new NowellpointClient(token)
				.accountProfile()
				.address()
				.update(request.params(":id"), addressRequest);
		
		if (! updateResult.isSuccess()) {
			Address address = new Address()
					.withCity(request.queryParams("city"))
					.withCountryCode(request.queryParams("countryCode"))
					.withPostalCode(request.queryParams("postalCode"))
					.withState(request.queryParams("state"))
					.withStreet(request.queryParams("street"));

			
			Map<String, Object> model = getModel();
			model.put("account", identity);
			model.put("accountProfile", new AccountProfile(request.params(":id")));
			model.put("address", address);
			model.put("errorMessage", updateResult.getErrorMessage());
			
			String output = render(configuration, request, response, model, Template.ACCOUNT_PROFILE_ADDRESS_EDIT);
			
			throw new BadRequestException(output);
		}
		
		response.cookie("successMessage", MessageProvider.getMessage(getLocale(request), "update.address.success"), 3);
		response.redirect(String.format("/app/account-profile/%s", request.params(":id")));
		
		return "";		
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String getCreditCard(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		Identity identity = getIdentity(request);
		
		CreditCard creditCard = new NowellpointClient(token)
				.accountProfile()
				.creditCard()
				.get(request.params(":id"), request.params(":token"));
		
		Map<String, Object> model = getModel();
		model.put("account", identity);
		model.put("accountProfile", new AccountProfile(request.params(":id")));
		model.put("creditCard", creditCard);
		model.put("mode", "view");
		
		return render(configuration, request, response, model, Template.ACCOUNT_PROFILE_PAYMENT_METHOD);
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String newCreditCard(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		AccountProfile accountProfile = new NowellpointClient(token)
				.accountProfile()
				.get(request.params(":id"));
		
		Address address = new Address();
		address.setCountryCode(accountProfile.getAddress().getCountryCode());
		
		CreditCard creditCard = new CreditCard();
		creditCard.setExpirationMonth(String.valueOf(LocalDate.now().getMonthValue()));
		creditCard.setExpirationYear(String.valueOf(LocalDate.now().getYear()));
		creditCard.setBillingAddress(address);
		creditCard.setBillingContact(new Contact());
			
		Map<String, Object> model = getModel();
		model.put("creditCard", creditCard);
		model.put("accountProfile", accountProfile);
		model.put("action", String.format("/app/account-profile/%s/payment-methods", request.params(":id")));
		model.put("mode", "new");
		
		return render(configuration, request, response, model, Template.ACCOUNT_PROFILE_PAYMENT_METHOD);
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String editCreditCard(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		CreditCard creditCard = new NowellpointClient(token)
				.accountProfile()
				.creditCard()
				.get(request.params(":id"), request.params(":token"));
		
		AccountProfile accountProfile = new NowellpointClient(token)
				.accountProfile()
				.get(request.params(":id"));
		
		Map<String, Object> model = getModel();
		model.put("accountProfile", accountProfile);
		model.put("creditCard", creditCard);
		model.put("action", String.format("/app/account-profile/%s/payment-methods/%s", request.params(":id"), request.params(":token")));
		model.put("mode", "edit");
		
		return render(configuration, request, response, model, Template.ACCOUNT_PROFILE_PAYMENT_METHOD);
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String addCreditCard(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
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
		
		CreditCardRequest creditCardRequest = new CreditCardRequest()
				.withAccountProfileId(id)
				.withCardholderName(cardholderName)
				.withExpirationMonth(expirationMonth)
				.withExpirationYear(expirationYear)
				.withNumber(number)
				.withCvv(cvv)
				.withPrimary(primary)
				.withCity(city)
				.withCountryCode(countryCode)
				.withPostalCode(postalCode)
				.withState(state)
				.withStreet(street)
				.withFirstName(firstName)
				.withLastName(lastName);
		
		CreateResult<CreditCard> createResult = new NowellpointClient(token)
				.accountProfile()
				.creditCard()
				.add(creditCardRequest);
		
		Map<String, Object> model = getModel();
		model.put("accountProfile", new AccountProfile(request.params(":id")));
			
		if (createResult.isSuccess()) {
			CreditCard creditCard = createResult.getTarget();
			
			model.put("creditCard", creditCard);
			model.put("mode", "view");
			model.put("successMessage", MessageProvider.getMessage(getLocale(request), "add.credit.card.success"));
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
					.withPrimary(primary);
			
			model.put("creditCard", creditCard);
			model.put("action", String.format("/app/account-profile/%s/payment-methods", request.params(":id")));
			model.put("mode", "new");
			model.put("errorMessage", createResult.getErrorMessage());
		}
		
		return render(configuration, request, response, model, Template.ACCOUNT_PROFILE_PAYMENT_METHOD);
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String updateCreditCard(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String accountProfileId = request.params(":id");
		String creditCardToken = request.params(":token");
		
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
		
		CreditCardRequest creditCardRequest = new CreditCardRequest()
				.withAccountProfileId(accountProfileId)
				.withToken(creditCardToken)
				.withCardholderName(cardholderName)
				.withExpirationMonth(expirationMonth)
				.withExpirationYear(expirationYear)
				.withCvv(cvv)
				.withPrimary(primary)
				.withCity(city)
				.withCountryCode(countryCode)
				.withPostalCode(postalCode)
				.withState(state)
				.withStreet(street)
				.withFirstName(firstName)
				.withLastName(lastName);
		
		UpdateResult<CreditCard> updateResult = new NowellpointClient(token)
				.accountProfile()
				.creditCard()
				.update(creditCardRequest);
		
		Map<String, Object> model = getModel();
		model.put("accountProfile", new AccountProfile(accountProfileId));
			
		if (updateResult.isSuccess()) {
			CreditCard creditCard = updateResult.getTarget();
			
			model.put("creditCard", creditCard);
			model.put("mode", "view");
			model.put("successMessage", MessageProvider.getMessage(getLocale(request), "update.credit.card.success"));
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
					.withPrimary(primary);
			
			model.put("creditCard", creditCard);
			model.put("action", String.format("/app/account-profile/%s/payment-methods/%s", accountProfileId, creditCardToken));
			model.put("mode", "edit");
			model.put("errorMessage", updateResult.getErrorMessage());
		}
			
		return render(configuration, request, response, model, Template.ACCOUNT_PROFILE_PAYMENT_METHOD);
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String setPrimaryCreditCard(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		UpdateResult<CreditCard> updateResult = new NowellpointClient(token)
				.accountProfile()
				.creditCard()
				.setPrimary(request.params(":id"), request.params(":token"));
			
		if (! updateResult.isSuccess()) {
			throw new BadRequestException(updateResult.getErrorMessage());
		}
		
		response.cookie(String.format("/app/account-profile/%s",  request.params(":id")), "successMessage", MessageProvider.getMessage(getLocale(request), "primary.credit.card.set"), 3, Boolean.FALSE);
		
		return "";
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String removeCreditCard(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		DeleteResult deleteResult = new NowellpointClient(token)
				.accountProfile()
				.creditCard()
				.delete(request.params(":id"), request.params(":token"));
			
		if (! deleteResult.isSuccess()) {
			throw new BadRequestException(deleteResult.getErrorMessage());
		}
		
		response.cookie(String.format("/app/account-profile/%s",  request.params(":id")), "successMessage", MessageProvider.getMessage(getLocale(request), "remove.credit.card.success"), 3, Boolean.FALSE);
		
		return "";
	};
	
	/**
	 * 
	 * @param accountProfile
	 * @return Locale map 
	 */
	
	private Map<String,String> getLocales(String localeSidKey) {
		Locale.setDefault(new Locale(localeSidKey));
		
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
}