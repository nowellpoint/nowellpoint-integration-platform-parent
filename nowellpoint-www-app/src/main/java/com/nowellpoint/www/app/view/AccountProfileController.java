package com.nowellpoint.www.app.view;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.AccountProfile;
import com.nowellpoint.client.model.AccountProfileRequest;
import com.nowellpoint.client.model.Address;
import com.nowellpoint.client.model.AddressRequest;
import com.nowellpoint.client.model.Contact;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.CreditCard;
import com.nowellpoint.client.model.CreditCardRequest;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.GetPlansRequest;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Plan;
import com.nowellpoint.client.model.Subscription;
import com.nowellpoint.client.model.SubscriptionRequest;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class AccountProfileController extends AbstractStaticController {
	
	public static class Template {
		public static final String ACCOUNT_PROFILE_ME = String.format(APPLICATION_CONTEXT, "account-profile-me.html");
		public static final String ACCOUNT_PROFILE = String.format(APPLICATION_CONTEXT, "account-profile.html");
		public static final String ACCOUNT_PROFILE_PLANS = String.format(APPLICATION_CONTEXT, "account-profile-plans.html");
		public static final String ACCOUNT_PROFILE_DEACTIVATE = String.format(APPLICATION_CONTEXT, "account-profile-deactivate.html");
		public static final String ACCOUNT_PROFILE_PAYMENT_METHOD = String.format(APPLICATION_CONTEXT, "payment-method.html");
		public static final String ACCOUNT_PROFILE_CURRENT_PLAN = String.format(APPLICATION_CONTEXT, "account-profile-current-plan.html");
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String listPlans(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		Identity identity = getIdentity(request);
		
		String id = request.params(":id");
		
		AccountProfile accountProfile = NowellpointClient.defaultClient(token)
				.accountProfile()
				.get(id);
		
		GetPlansRequest getPlansRequest = new GetPlansRequest()
				.withLanguage(identity.getLanguageSidKey())
				.withLocale(identity.getLocaleSidKey());
		
		List<Plan> plans = NowellpointClient.defaultClient(token)
				.plan()
				.getPlans(getPlansRequest)
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

		return render(AccountProfileController.class, configuration, request, response, model, Template.ACCOUNT_PROFILE_PLANS);	
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String viewAccountProfile(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		Identity identity = getIdentity(request);
		
		String id = request.params(":id");
		
		AccountProfile accountProfile = NowellpointClient.defaultClient(token)
				.accountProfile()
				.get(id);
		
		Address address = new Address();
		address.setCity(identity.getAddress().getCity());
		address.setPostalCode(identity.getAddress().getPostalCode());
		address.setState(identity.getAddress().getState());
		address.setStreet(identity.getAddress().getStreet());
		address.setCountryCode(accountProfile.getAddress().getCountryCode());
		
		Contact contact = new Contact();
		contact.setFirstName(identity.getFirstName());
		contact.setLastName(identity.getLastName());
		
		CreditCard creditCard = new CreditCard();
		creditCard.setCardholderName((identity.getFirstName() != null ? identity.getFirstName().concat(" ") : "").concat(identity.getLastName()));
		creditCard.setExpirationMonth(String.valueOf(LocalDate.now().getMonthValue()));
		creditCard.setExpirationYear(String.valueOf(LocalDate.now().getYear()));
		creditCard.setBillingAddress(address);
		creditCard.setBillingContact(contact);
		
		Map<String, Object> model = getModel();
		model.put("accountProfile", accountProfile);
		model.put("creditCard", creditCard);
		model.put("locales", getLocales(accountProfile.getLocaleSidKey()));
		model.put("languages", getSupportedLanguages());
		model.put("successMessage", request.cookie("update.profile.success"));
		
		model.put("locales", new TreeMap<String, String>(getLocales(identity.getLocaleSidKey())));
		model.put("languages", getSupportedLanguages());
		model.put("timeZones", getTimeZones());
		
		if (accountProfile.getId().equals(identity.getId())) {
			return render(AccountProfileController.class, configuration, request, response, model, Template.ACCOUNT_PROFILE_ME);
		}

		return render(AccountProfileController.class, configuration, request, response, model, Template.ACCOUNT_PROFILE);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String reviewPlan(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		Identity identity = getIdentity(request);
		
		String id = request.params(":id");
		
		AccountProfile accountProfile = NowellpointClient.defaultClient(token)
				.accountProfile()
				.get(id);
		
		String planId = request.params(":planId");
		
		Plan plan = NowellpointClient.defaultClient(token)
				.plan()
				.get(planId);
		
		Address address = new Address();
		address.setCity(identity.getAddress().getCity());
		address.setPostalCode(identity.getAddress().getPostalCode());
		address.setState(identity.getAddress().getState());
		address.setStreet(identity.getAddress().getStreet());
		address.setCountryCode(accountProfile.getAddress().getCountryCode());
		
		Contact contact = new Contact();
		contact.setFirstName(identity.getFirstName());
		contact.setLastName(identity.getLastName());
		
		CreditCard creditCard = new CreditCard();
		creditCard.setCardholderName((identity.getFirstName() != null ? identity.getFirstName().concat(" ") : "").concat(identity.getLastName()));
		creditCard.setExpirationMonth(String.valueOf(LocalDate.now().getMonthValue()));
		creditCard.setExpirationYear(String.valueOf(LocalDate.now().getYear()));
		creditCard.setBillingAddress(address);
		creditCard.setBillingContact(contact);

		Map<String, Object> model = getModel();
		model.put("account", identity);
		model.put("accountProfile", accountProfile);
		model.put("creditCard", creditCard);
		model.put("action", "reviewPlan");
		model.put("plan", plan);
			
		return render(AccountProfileController.class, configuration, request, response, model, Template.ACCOUNT_PROFILE_PLANS);	
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String downloadInvoice(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String invoiceNumber = request.params(":invoiceNumber");
		
		try {
			byte[] data = NowellpointClient.defaultClient(token)
					.accountProfile()
					.downloadInvoice(id, invoiceNumber);
			
			System.out.println(data.length);
			
			HttpServletResponse httpServletResponse = response.raw();
	        httpServletResponse.setContentType("application/pdf");
	        httpServletResponse.addHeader("Content-Disposition", "inline; filename=mypdf.pdf");
	        httpServletResponse.getOutputStream().write(data);
	        httpServletResponse.getOutputStream().close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	public static String currentPlan(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		AccountProfile accountProfile = NowellpointClient.defaultClient(token)
				.accountProfile()
				.get(id);
		
		Map<String, Object> model = getModel();
		model.put("accountProfile", accountProfile);
		
		return render(AccountProfileController.class, configuration, request, response, model, Template.ACCOUNT_PROFILE_CURRENT_PLAN);	
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String setPlan(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		AccountProfile accountProfile = NowellpointClient.defaultClient(token)
				.accountProfile()
				.get(id); 
		
		String planId = request.params(":planId");
		String paymentMethodToken = request.queryParams("paymentMethodToken");
		
		SubscriptionRequest subscriptionRequest = new SubscriptionRequest()
				.withAccountProfileId(accountProfile.getId())
				.withPaymentMethodToken(paymentMethodToken)
				.withPlanId(planId);
		
		UpdateResult<Subscription> updateResult = NowellpointClient.defaultClient(token)
				.accountProfile()
				.subscription()
				.set(subscriptionRequest);
		
		if (! updateResult.isSuccess()) {
			response.status(400);
		}
		
		return responseBody(updateResult);
			
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String updateAccountProfile(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		AccountProfileRequest accountProfileRequest = new AccountProfileRequest()
				.withFirstName(request.queryParams("firstName"))
				.withLastName(request.queryParams("lastName"))
				.withCompany(request.queryParams("company"))
				.withDivision(request.queryParams("division"))
				.withDepartment(request.queryParams("department"))
				.withTitle(request.queryParams("title"))
				.withEmail(request.queryParams("email"))
				.withMobilePhone(request.queryParams("mobilePhone"))
				.withPhone(request.queryParams("phone"))
				.withExtension(request.queryParams("extension"))
				.withLanguageSidKey(request.queryParams("languageSidKey"))
				.withLocaleSidKey(request.queryParams("localeSidKey"))
				.withTimeZoneSidKey(request.queryParams("timeZoneSidKey"))
				.withEnableSalesforceLogin(request.queryParams("enableSalesforceLogin") != null ? Boolean.TRUE : Boolean.FALSE);

		UpdateResult<AccountProfile> updateResult = NowellpointClient.defaultClient(token)
				.accountProfile()
				.update(request.params(":id"), accountProfileRequest);
		
		if (! updateResult.isSuccess()) {
			response.status(400);
		}
		
		return responseBody(updateResult);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String confirmDeactivateAccountProfile(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		AccountProfile accountProfile = NowellpointClient.defaultClient(token)
				.accountProfile()
				.get(request.params(":id"));
			
		Map<String, Object> model = getModel();
		model.put("accountProfile", accountProfile);
			
		return render(AccountProfileController.class, configuration, request, response, model, Template.ACCOUNT_PROFILE_DEACTIVATE);		
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String deactivateAccountProfile(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		DeleteResult deleteResult = NowellpointClient.defaultClient(token)
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
	
	public static String removeProfilePicture(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		UpdateResult<AccountProfile> updateResult = NowellpointClient.defaultClient(token)
				.accountProfile()
				.removeProfilePicture(request.params(":id"));
		
		if (! updateResult.isSuccess()) {
			throw new BadRequestException(updateResult.getErrorMessage());
		}
		
		Map<String, Object> model = getModel();
		model.put("accountProfile", updateResult.getTarget());
		
		return render(AccountProfileController.class, configuration, request, response, model, Template.ACCOUNT_PROFILE);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String updateAccountProfileAddress(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		AddressRequest addressRequest = new AddressRequest()
				.withCity(request.queryParams("city"))
				.withCountryCode(request.queryParams("countryCode"))
				.withPostalCode(request.queryParams("postalCode"))
				.withState(request.queryParams("state"))
				.withStreet(request.queryParams("street"));
		
		UpdateResult<Address> updateResult = NowellpointClient.defaultClient(token)
				.accountProfile()
				.address()
				.update(request.params(":id"), addressRequest);
		
		if (! updateResult.isSuccess()) {
			response.status(400);
		}
		
		return responseBody(updateResult);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String getCreditCard(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		CreditCard creditCard = NowellpointClient.defaultClient(token)
				.accountProfile()
				.creditCard()
				.get(request.params(":id"), request.params(":token"));
		
		Map<String, Object> model = getModel();
		model.put("accountProfile", new AccountProfile(request.params(":id")));
		model.put("creditCard", creditCard);
		model.put("mode", "view");
		
		return render(AccountProfileController.class, configuration, request, response, model, Template.ACCOUNT_PROFILE_PAYMENT_METHOD);
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String editCreditCard(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		CreditCard creditCard = NowellpointClient.defaultClient(token)
				.accountProfile()
				.creditCard()
				.get(request.params(":id"), request.params(":token"));
		
		AccountProfile accountProfile = NowellpointClient.defaultClient(token)
				.accountProfile()
				.get(request.params(":id"));
		
		Map<String, Object> model = getModel();
		model.put("accountProfile", accountProfile);
		model.put("creditCard", creditCard);
		model.put("action", String.format("/app/account-profile/%s/payment-methods/%s", request.params(":id"), request.params(":token")));
		model.put("mode", "edit");
		
		return render(AccountProfileController.class, configuration, request, response, model, Template.ACCOUNT_PROFILE_PAYMENT_METHOD);
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String addCreditCard(Configuration configuration, Request request, Response response) {
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
		
		CreateResult<CreditCard> createResult = NowellpointClient.defaultClient(token)
				.accountProfile()
				.creditCard()
				.add(creditCardRequest);
		
		if (! createResult.isSuccess()) {
			response.status(400);
		}
		
		return responseBody(createResult);
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String updateCreditCard(Configuration configuration, Request request, Response response) {
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
		Boolean primary = request.queryParams("primary") != null ? Boolean.TRUE : Boolean.FALSE;
		
		CreditCardRequest creditCardRequest = new CreditCardRequest()
				.withAccountProfileId(accountProfileId)
				.withToken(creditCardToken)
				.withCardholderName(cardholderName)
				.withExpirationMonth(expirationMonth)
				.withExpirationYear(expirationYear)
				.withPrimary(primary)
				.withCity(city)
				.withCountryCode(countryCode)
				.withPostalCode(postalCode)
				.withState(state)
				.withStreet(street)
				.withFirstName(firstName)
				.withLastName(lastName);
		
		UpdateResult<CreditCard> updateResult = NowellpointClient.defaultClient(token)
				.accountProfile()
				.creditCard()
				.update(creditCardRequest);
		
		if (! updateResult.isSuccess()) {
			response.status(400);
		}
		
		return responseBody(updateResult);
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String setPrimaryCreditCard(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		UpdateResult<CreditCard> updateResult = NowellpointClient.defaultClient(token)
				.accountProfile()
				.creditCard()
				.setPrimary(request.params(":id"), request.params(":token"));
		
		if (! updateResult.isSuccess()) {
			response.status(400);
		}
		
		return responseBody(updateResult);
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String removeCreditCard(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		DeleteResult deleteResult = NowellpointClient.defaultClient(token)
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
	
	private static Map<String,String> getLocales(String localeSidKey) {
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
	
	private static Map<String,String> getSupportedLanguages() {
		Map<String,String> languageMap = new HashMap<String,String>();
		languageMap.put(Locale.US.toString(), Locale.US.getDisplayLanguage());
		return languageMap;
	}
	
	/**
	 * 
	 * @return application supported timezones
	 */
	
	private static List<String> getTimeZones() {
		return Arrays.asList(TimeZone.getAvailableIDs());
	}
}