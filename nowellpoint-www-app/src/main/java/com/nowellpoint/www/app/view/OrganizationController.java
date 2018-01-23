package com.nowellpoint.www.app.view;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.AddressRequest;
import com.nowellpoint.client.model.ContactRequest;
import com.nowellpoint.client.model.CreditCardRequest;
import com.nowellpoint.client.model.GetPlansRequest;
import com.nowellpoint.client.model.Identity;
import com.nowellpoint.client.model.Organization;
import com.nowellpoint.client.model.Plan;
import com.nowellpoint.client.model.SubscriptionRequest;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class OrganizationController extends AbstractStaticController {
	
	public static class Template {
		public static final String ORGANIZATION_CHANGE_PLAN = String.format(APPLICATION_CONTEXT, "organization-change-plan.html");
		public static final String ORGANIZATION_VIEW = String.format(APPLICATION_CONTEXT, "organization-view.html");
		public static final String ORGANIZATION_PAYMENT_METHOD = String.format(APPLICATION_CONTEXT, "organization-payment-method.html");
		public static final String ORGANIZATION_BILLING_ADDRESS = String.format(APPLICATION_CONTEXT, "organization-billing-address.html");
		public static final String ORGANIZATION_BILLING_CONTACT = String.format(APPLICATION_CONTEXT, "organization-billing-contact.html");
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
					.organization()
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
		
		UpdateResult<Organization> updateResult = NowellpointClient.defaultClient(token)
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
		
		String id = request.params(":id");
		
		Organization organization = NowellpointClient.defaultClient(token)
				.organization()
				.get(id);
		
		String planId = request.params(":planId");
		
		Plan plan = NowellpointClient.defaultClient(token)
				.plan()
				.get(planId);

		Map<String, Object> model = getModel();
		model.put("organization", organization);
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
	
	public static String updateBillingAddress(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String organizationId = request.params(":id");
		
		String city = request.queryParams("city");
		String countryCode = request.queryParams("countryCode");
		String postalCode = request.queryParams("postalCode");
		String state = request.queryParams("state");
		String street = request.queryParams("street");
		
		AddressRequest addressRequest = AddressRequest.builder()
				.city(city)
				.countryCode(countryCode)
				.organizationId(organizationId)
				.postalCode(postalCode)
				.state(state)
				.street(street)
				.token(token)
				.build();
		
		UpdateResult<Organization> updateResult = NowellpointClient.defaultClient(token)
				.organization()
				.subscription()
				.billingAddress()
				.update(addressRequest);
		
		if (updateResult.isSuccess()) {
			Map<String, Object> model = getModel();
			model.put("organization", updateResult.getTarget());			
			return render(OrganizationController.class, configuration, request, response, model, Template.ORGANIZATION_BILLING_ADDRESS);
		} else {
			return showErrorMessage(OrganizationController.class, configuration, request, response, updateResult.getErrorMessage());
		}
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String updateBillingContact(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String organizationId = request.params(":id");
		
		String firstName = request.queryParams("firstName");
		String lastName = request.queryParams("lastName");
		String email = request.queryParams("email");
		String phone = request.queryParams("phone");
		
		ContactRequest contactRequest = ContactRequest.builder()
				.email(email)
				.firstName(firstName)
				.lastName(lastName)
				.phone(phone)
				.organizationId(organizationId)
				.token(token)
				.build();
		
		UpdateResult<Organization> updateResult = NowellpointClient.defaultClient(token)
				.organization()
				.subscription()
				.billingContact()
				.update(contactRequest);
		
		if (updateResult.isSuccess()) {
			Map<String, Object> model = getModel();
			model.put("organization", updateResult.getTarget());			
			return render(OrganizationController.class, configuration, request, response, model, Template.ORGANIZATION_BILLING_CONTACT);
		} else {
			return showErrorMessage(OrganizationController.class, configuration, request, response, updateResult.getErrorMessage());
		}
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String updatePaymentMethod(Configuration configuration, Request request, Response response) {
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
				.token(token)
				.build();
		
		UpdateResult<Organization> updateResult = NowellpointClient.defaultClient(token)
				.organization()
				.subscription()
				.creditCard()
				.update(creditCardRequest);
		
		if (updateResult.isSuccess()) {
			Map<String, Object> model = getModel();
			model.put("organization", updateResult.getTarget());
			return render(OrganizationController.class, configuration, request, response, model, Template.ORGANIZATION_PAYMENT_METHOD);
		} else {
			return showErrorMessage(OrganizationController.class, configuration, request, response, updateResult.getErrorMessage());
		}
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
		
		String organizationId = request.params(":id");
		
		CreditCardRequest creditCardRequest = CreditCardRequest.builder()
				.organizationId(organizationId)
				.token(token)
				.build();
		
		UpdateResult<Organization> updateResult = NowellpointClient.defaultClient(token)
				.organization()
				.subscription()
				.creditCard()
				.remove(creditCardRequest);
		
		if (updateResult.isSuccess()) {
			
			Map<String, Object> model = getModel();
			model.put("organization", updateResult.getTarget());
			
			return render(OrganizationController.class, configuration, request, response, model, Template.ORGANIZATION_PAYMENT_METHOD);
			
		} else {
			return showErrorMessage(OrganizationController.class, configuration, request, response, updateResult.getErrorMessage());
		}
	};
	
	/**
	 * 
	 * @param locale
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