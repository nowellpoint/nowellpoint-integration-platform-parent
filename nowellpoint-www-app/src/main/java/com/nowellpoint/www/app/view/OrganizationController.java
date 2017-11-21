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
import com.nowellpoint.client.model.Address;
import com.nowellpoint.client.model.AddressRequest;
import com.nowellpoint.client.model.Contact;
import com.nowellpoint.client.model.CreditCard;
import com.nowellpoint.client.model.CreditCardRequest;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.GetPlansRequest;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Organization;
import com.nowellpoint.client.model.Plan;
import com.nowellpoint.client.model.Subscription;
import com.nowellpoint.client.model.SubscriptionRequest;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.UserProfile;
import com.nowellpoint.www.app.util.MessageProvider;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class OrganizationController extends AbstractStaticController {
	
	public static class Template {
		public static final String ORGANIZATION_CHANGE_PLAN = String.format(APPLICATION_CONTEXT, "organization-change-plan.html");
		public static final String USER_PROFILE_DEACTIVATE = String.format(APPLICATION_CONTEXT, "user-profile-deactivate.html");
		public static final String USER_PROFILE_PAYMENT_METHOD = String.format(APPLICATION_CONTEXT, "payment-method.html");
		public static final String ORGANIZATION_VIEW = String.format(APPLICATION_CONTEXT, "organization-view.html");
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
		
		Organization organization = NowellpointClient.defaultClient(token)
				.organization()
				.get(id);
		
		GetPlansRequest getPlansRequest = new GetPlansRequest()
				.withLanguage("en_US")
				.withLocale(identity.getLocale());
		
		List<Plan> plans = NowellpointClient.defaultClient(token)
				.plan()
				.getPlans(getPlansRequest)
				.getItems()
				.stream()
				.sorted((p1, p2) -> p1.getPrice().getUnitPrice().compareTo(p2.getPrice().getUnitPrice()))
				.collect(Collectors.toList());

		Map<String, Object> model = getModel();
		model.put("organization", organization);
		model.put("action", "listPlans");
		model.put("plans", plans);
		model.put("locales", new TreeMap<String, String>(getLocales(identity.getLocale())));
		model.put("languages", getSupportedLanguages());
		model.put("timeZones", getTimeZones());

		return render(OrganizationController.class, configuration, request, response, model, Template.ORGANIZATION_CHANGE_PLAN);	
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
					.userProfile()
					.downloadInvoice(id, invoiceNumber);
			
			HttpServletResponse httpServletResponse = response.raw();
	        httpServletResponse.setContentType("application/pdf");
	        httpServletResponse.addHeader("Content-Disposition", "inline; filename=mypdf.pdf");
	        httpServletResponse.getOutputStream().write(data);
	        httpServletResponse.getOutputStream().close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	public static String viewOrganization(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		
		Organization organization = NowellpointClient.defaultClient(token)
				.organization()
				.get(id);
		
		Map<String, Object> model = getModel();
		model.put("organization", organization);
		
		return render(OrganizationController.class, configuration, request, response, model, Template.ORGANIZATION_VIEW);	
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String changePlan(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String planId = request.params(":planId");
		String cardholderName = request.queryParamOrDefault("cardholderName", "");
		String number = request.queryParamOrDefault("number", "");
		String expirationMonth = request.queryParamOrDefault("expirationMonth", "");
		String expirationYear = request.queryParamOrDefault("expirationYear", "");
		String cvv = request.queryParamOrDefault("cvv", "");
		
		SubscriptionRequest subscriptionRequest = new SubscriptionRequest()
				.withOrganizationId(id)
				.withPlanId(planId)
				.withCardholderName(cardholderName)
				.withNumber(number)
				.withExpirationMonth(expirationMonth)
				.withExpirationYear(expirationYear)
				.withCvv(cvv);
		
		UpdateResult<Subscription> updateResult = NowellpointClient.defaultClient(token)
				.organization()
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
	
	public static String reviewPlan(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		Identity identity = getIdentity(request);
		
		String id = request.params(":id");
		
		Organization organization = NowellpointClient.defaultClient(token)
				.organization()
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
		address.setCountryCode(identity.getAddress().getCountryCode());
		
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
		model.put("organization", organization);
		model.put("creditCard", creditCard);
		model.put("action", "reviewPlan");
		model.put("plan", plan);
			
		return render(OrganizationController.class, configuration, request, response, model, Template.ORGANIZATION_CHANGE_PLAN);	
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String updateAddress(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		AddressRequest addressRequest = new AddressRequest()
				.withCity(request.queryParams("city"))
				.withCountryCode(request.queryParams("countryCode"))
				.withPostalCode(request.queryParams("postalCode"))
				.withState(request.queryParams("state"))
				.withStreet(request.queryParams("street"));
		
		UpdateResult<Address> updateResult = NowellpointClient.defaultClient(token)
				.userProfile()
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
				.organization()
				.subscription()
				.creditCard()
				.get(request.params(":id"), request.params(":token"));
		
		Map<String, Object> model = getModel();
		model.put("userProfile", new UserProfile(request.params(":id")));
		model.put("creditCard", creditCard);
		model.put("mode", "view");
		
		return render(OrganizationController.class, configuration, request, response, model, Template.USER_PROFILE_PAYMENT_METHOD);
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
				.organization()
				.subscription()
				.creditCard()
				.get(request.params(":id"), request.params(":token"));
		
		UserProfile userProfile = NowellpointClient.defaultClient(token)
				.userProfile()
				.get(request.params(":id"));
		
		Map<String, Object> model = getModel();
		model.put("userProfile", userProfile);
		model.put("creditCard", creditCard);
		model.put("action", String.format("/app/account-profile/%s/payment-methods/%s", request.params(":id"), request.params(":token")));
		model.put("mode", "edit");
		
		return render(OrganizationController.class, configuration, request, response, model, Template.USER_PROFILE_PAYMENT_METHOD);
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
		
		String organizationId = request.params(":id");
		
		String cardholderName = request.queryParams("cardholderName");
		String expirationMonth = request.queryParams("expirationMonth");
		String expirationYear = request.queryParams("expirationYear");
		String number = request.queryParams("number");
		String cvv = request.queryParams("cvv");
		
		CreditCardRequest creditCardRequest = CreditCardRequest.builder()
				.cardholderName(cardholderName)
				.cvv(cvv)
				.expirationMonth(expirationMonth)
				.expirationYear(expirationYear)
				.number(number)
				.organizationId(organizationId)
				.build();
		
		UpdateResult<CreditCard> updateResult = NowellpointClient.defaultClient(token)
				.organization()
				.subscription()
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
	
	public static String removeCreditCard(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		DeleteResult deleteResult = NowellpointClient.defaultClient(token)
				.organization()
				.subscription()
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
	
	private static Map<String,String> getLocales(Locale locale) {
		Locale.setDefault(locale);
		
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