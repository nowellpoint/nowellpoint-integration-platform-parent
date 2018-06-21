package com.nowellpoint.console.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.List;
import java.util.Locale;

import javax.validation.ValidationException;

import com.nowellpoint.console.model.CreditCardRequest;
import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.model.Plan;
import com.nowellpoint.console.model.Template;
import com.nowellpoint.console.service.OrganizationService;
import com.nowellpoint.console.service.PlanService;
import com.nowellpoint.console.util.Alert;
import com.nowellpoint.console.util.Templates;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class OrganizationController extends BaseController {
	
	private static final OrganizationService organizationService = new OrganizationService();
	
	private static final PlanService planService = new PlanService();
	
	public static void configureRoutes(Configuration configuration) {
		get(Path.Route.ORGANIZATION_VIEW, (request, response) 
				-> viewOrganization(configuration, request, response));
		
		get(Path.Route.ORGANIZATION_LIST_PLANS, (request, response) 
				-> listPlans(configuration, request, response));
		
		get(Path.Route.ORGANIZATION_PLAN, (request, response) 
				-> reviewPlan(configuration, request, response));
		
		post(Path.Route.ORGANIZATION_PLAN, (request, response) 
				-> setPlan(configuration, request, response));
		
		post(Path.Route.ORGANIZATION_CREDIT_CARD, (request, response) 
				-> updatePaymentMethod(configuration, request, response));
		
//		delete(Path.Route.ORGANIZATION_CREDIT_CARD, (request, response) 
//				-> OrganizationController.removeCreditCard(configuration, request, response));
//		
//		post(Path.Route.ORGANIZATION_BILLING_ADDRESS, (request, response) 
//				-> OrganizationController.updateBillingAddress(configuration, request, response));
//		
//		post(Path.Route.ORGANIZATION_BILLING_CONTACT, (request, response) 
//				-> OrganizationController.updateBillingContact(configuration, request, response));
//		
//		get(Path.Route.ORGANIZATION_GET_INVOICE, (request, response) 
//				-> OrganizationController.getInvoice(configuration, request, response));
	}
	
	private static String viewOrganization(Configuration configuration, Request request, Response response) {

		String id = request.params(":id");
		
		Organization organization = organizationService.get(id);
		
		Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(OrganizationController.class)
				.putModel("organization", organization)
				.request(request)
				.templateName(Templates.ORGANIZATION)
				.build();
		
		return template.render();
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String listPlans(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		
		Organization organization = organizationService.get(id);
		
		List<Plan> plans = planService.getPlans(Locale.getDefault().toString());
		
		Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(OrganizationController.class)
				.putModel("organization", organization)
				.putModel("plans", plans)
				.putModel("action", "listPlans")
				.request(request)
				.templateName(Templates.ORGANIZATION_CHANGE_PLAN)
				.build();
		
		return template.render();
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String getInvoice(Configuration configuration, Request request, Response response) {
//		Token token = getToken(request);
//		
//		String id = request.params(":id");
//		String invoiceNumber = request.params(":invoiceNumber");
//		
//		try {
//			byte[] data = NowellpointClient.defaultClient(token)
//					.organization()
//					.downloadInvoice(id, invoiceNumber);
//			
//			HttpServletResponse httpServletResponse = response.raw();
//	        httpServletResponse.setContentType("application/pdf");
//	        httpServletResponse.addHeader("Content-Disposition", String.format("inline; filename=invoice_%s.pdf", invoiceNumber));
//	        httpServletResponse.getOutputStream().write(data);
//	        httpServletResponse.getOutputStream().close();
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		return "";
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String setPlan(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		String planId = request.params(":planId");
		String cardholderName = request.queryParamOrDefault("cardholderName", "");
		String number = request.queryParamOrDefault("number", "");
		String expirationMonth = request.queryParamOrDefault("expirationMonth", "");
		String expirationYear = request.queryParamOrDefault("expirationYear", "");
		String cvv = request.queryParamOrDefault("cvv", "");
		
		Plan plan = planService.get(planId);
		
		if ("FREE".equals(plan.getPlanCode())) {
			organizationService.setPlan(id, plan);
		} else {
			
			CreditCardRequest creditCardRequest = CreditCardRequest.builder()
					.cardholderName(cardholderName)
					.cvv(cvv)
					.expirationMonth(expirationMonth)
					.expirationYear(expirationYear)
					.number(number)
					.build();
			
			try {
				organizationService.setPlan(id, plan, creditCardRequest);
			} catch (ValidationException e) {
				response.status(400);
				return Alert.showError(e.getMessage());
			}
		}
		
//		
//		SubscriptionRequest subscriptionRequest = new SubscriptionRequest()
//				.withOrganizationId(id)
//				.withPlanId(planId)
//				.withCardholderName(cardholderName)
//				.withNumber(number)
//				.withExpirationMonth(expirationMonth)
//				.withExpirationYear(expirationYear)
//				.withCvv(cvv);
//		
//		UpdateResult<Organization> updateResult = NowellpointClient.defaultClient(token)
//				.organization()
//				.subscription()
//				.set(subscriptionRequest);
//		
//		if (! updateResult.isSuccess()) {
//			response.status(400);
//		}
//		
//		return responseBody(updateResult);
		return null;
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String reviewPlan(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		String planId = request.params(":planId");
		
		Organization organization = organizationService.get(id);
		
		Plan plan = planService.get(planId);
		
		Template template = Template.builder()
				.configuration(configuration)
				.controllerClass(OrganizationController.class)
				.putModel("organization", organization)
				.putModel("plan", plan)
				.putModel("action", "reviewPlan")
				.request(request)
				.templateName(Templates.ORGANIZATION_CHANGE_PLAN)
				.build();
		
		return template.render();
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String updateBillingAddress(Configuration configuration, Request request, Response response) {
//		Token token = getToken(request);
//		
//		String organizationId = request.params(":id");
//		
//		String city = request.queryParams("city");
//		String countryCode = request.queryParams("countryCode");
//		String postalCode = request.queryParams("postalCode");
//		String state = request.queryParams("state");
//		String street = request.queryParams("street");
//		
//		AddressRequest addressRequest = AddressRequest.builder()
//				.city(city)
//				.countryCode(countryCode)
//				.organizationId(organizationId)
//				.postalCode(postalCode)
//				.state(state)
//				.street(street)
//				.token(token)
//				.build();
//		
//		UpdateResult<Organization> updateResult = NowellpointClient.defaultClient(token)
//				.organization()
//				.subscription()
//				.billingAddress()
//				.update(addressRequest);
//		
//		if (updateResult.isSuccess()) {
//			Map<String, Object> model = getModel();
//			model.put("organization", updateResult.getTarget());			
//			return render(OrganizationController.class, configuration, request, response, model, Template.ORGANIZATION);
//		} else {
//			return showErrorMessage(OrganizationController.class, configuration, request, response, updateResult.getErrorMessage());
//		}
		
		return null;
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String updateBillingContact(Configuration configuration, Request request, Response response) {
//		Token token = getToken(request);
//		
//		String organizationId = request.params(":id");
//		
//		String firstName = request.queryParams("firstName");
//		String lastName = request.queryParams("lastName");
//		String email = request.queryParams("email");
//		String phone = request.queryParams("phone");
//		
//		ContactRequest contactRequest = ContactRequest.builder()
//				.email(email)
//				.firstName(firstName)
//				.lastName(lastName)
//				.phone(phone)
//				.organizationId(organizationId)
//				.token(token)
//				.build();
//		
//		UpdateResult<Organization> updateResult = NowellpointClient.defaultClient(token)
//				.organization()
//				.subscription()
//				.billingContact()
//				.update(contactRequest);
//		
//		if (updateResult.isSuccess()) {
//			Map<String, Object> model = getModel();
//			model.put("organization", updateResult.getTarget());			
//			return render(OrganizationController.class, configuration, request, response, model, Template.ORGANIZATION);
//		} else {
//			return showErrorMessage(OrganizationController.class, configuration, request, response, updateResult.getErrorMessage());
//		}
		
		return null;
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String updatePaymentMethod(Configuration configuration, Request request, Response response) {
		
		String id = request.params(":id");
		
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
				.build();
		
		try {
			
			Organization organization = organizationService.update(id, creditCardRequest);
			
			Template template = Template.builder()
					.configuration(configuration)
					.controllerClass(OrganizationController.class)
					.putModel("organization", organization)
					.request(request)
					.templateName(Templates.ORGANIZATION_PAYMENT_METHOD)
					.build();
			
			return template.render();
			
		} catch (ValidationException e) {
			
			response.status(400);
			
			return Alert.showError(e.getMessage());
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
//		Token token = getToken(request);
//		
//		String organizationId = request.params(":id");
//		
//		CreditCardRequest creditCardRequest = CreditCardRequest.builder()
//				.organizationId(organizationId)
//				.token(token)
//				.build();
//		
//		UpdateResult<Organization> updateResult = NowellpointClient.defaultClient(token)
//				.organization()
//				.subscription()
//				.creditCard()
//				.remove(creditCardRequest);
//		
//		if (updateResult.isSuccess()) {
//			
//			Map<String, Object> model = getModel();
//			model.put("organization", updateResult.getTarget());
//			
//			return render(OrganizationController.class, configuration, request, response, model, Template.ORGANIZATION);
//			
//		} else {
//			return showErrorMessage(OrganizationController.class, configuration, request, response, updateResult.getErrorMessage());
//		}
		
		return null;
	};
}