package com.nowellpoint.console.view;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import com.nowellpoint.client.Environment;
import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.ProvisionRequesst;
import com.nowellpoint.client.model.Registration;
import com.nowellpoint.client.model.SignUpRequest;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.content.model.Plan;
import com.nowellpoint.content.model.PlanList;
import com.nowellpoint.content.service.ContentService;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.TemplateBuilder;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class SignUpController extends AbstractStaticController {
	
	private static final Environment ENVIRONMENT = Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT"));
	
	public static class Template {
		public static final String PLANS = "plans.html";
		public static final String SIGN_UP = "signup.html";
		public static final String SIGN_UP_CONFIRM = "signup-confirm.html";
		public static final String PROVISION_ACCOUNT = "provision-account.html";
		public static final String PROVISION_ACCOUNT_CONFIRM = "provision-account-confirm.html";
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String plans(Configuration configuration, Request request, Response response) {
		return TemplateBuilder.template()
				.configuration(configuration)
				.controllerClass(SignUpController.class)
				.identity(getIdentity(request))
				.locale(getLocale(request))
				.addToModel("countryCode", Locale.getDefault().getCountry())
				.templateName(Template.PLANS)
				.timeZone(getTimeZone(request))
				.build();
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String freeAccount(Configuration configuration, Request request, Response response) {
		
		ContentService service = new ContentService();
		PlanList planList = service.getPlans();
		
		Optional<Plan> optional = planList.getItems()
				.stream()
				.filter(plan -> plan.getPlanCode().equals("FREE"))
				.findFirst();
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("planId", optional.get().getId());
		model.put("plan", optional.get());
		model.put("countryCode", Locale.getDefault().getCountry());
		model.put("action", "createAccount");
		
		return TemplateBuilder.template()
				.configuration(configuration)
				.controllerClass(SignUpController.class)
				.identity(getIdentity(request))
				.locale(getLocale(request))
				.model(model)
				.templateName(Template.SIGN_UP)
				.timeZone(getTimeZone(request))
				.build();
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String paidAccount(Configuration configuration, Request request, Response response) {
		
		String planId = request.queryParams("planId");
		
		if (planId != null) {
			
			ContentService service = new ContentService();
			PlanList planList = service.getPlans();
			
			Optional<Plan> optional = planList.getItems()
					.stream()
					.filter(plan -> plan.getId().equals(planId))
					.findFirst();
			
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("planId", planId);
			model.put("plan", optional.get());
			model.put("countryCode", Locale.getDefault().getCountry());
			model.put("action", "createAccount");
			
			return TemplateBuilder.template()
					.configuration(configuration)
					.controllerClass(SignUpController.class)
					.identity(getIdentity(request))
					.locale(getLocale(request))
					.model(model)
					.templateName(Template.SIGN_UP)
					.timeZone(getTimeZone(request))
					.build();
			
		} else {
			
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("action", "listPlans");
			
			return TemplateBuilder.template()
					.configuration(configuration)
					.controllerClass(SignUpController.class)
					.identity(getIdentity(request))
					.locale(getLocale(request))
					.model(model)
					.templateName(Template.PLANS)
					.timeZone(getTimeZone(request))
					.build();
		}
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String signUp(Configuration configuration, Request request, Response response) {

		String firstName = request.queryParams("firstName");
		String lastName = request.queryParams("lastName");
		String email = request.queryParams("email");
		// String password = request.queryParams("password");
		String phone = request.queryParams("phone");
		// String confirmPassword = request.queryParams("confirmPassword");
		String countryCode = request.queryParams("countryCode");
		String planId = request.queryParams("planId");
		String domain = request.queryParams("domain");

		Map<String, Object> model = getModel();

		if ("XX".equalsIgnoreCase(countryCode)) {

			ContentService service = new ContentService();
			PlanList planList = service.getPlans();

			Optional<Plan> optional = planList.getItems().stream().filter(plan -> plan.getId().equals(planId))
					.findFirst();

			model.put("action", "createAccount");
			model.put("firstName", firstName);
			model.put("lastName", lastName);
			model.put("email", email);
			model.put("countryCode", countryCode);
			model.put("planId", planId);
			model.put("plan", optional.get());
			model.put("phone", phone);
			model.put("domain", domain);
			model.put("errorMessage", "Please select a country from the list");

			return TemplateBuilder.template().configuration(configuration).controllerClass(SignUpController.class)
					.identity(getIdentity(request)).locale(getLocale(request)).model(model)
					.templateName(Template.SIGN_UP).timeZone(getTimeZone(request)).build();
		}

		SignUpRequest signUpRequest = SignUpRequest.builder().countryCode(countryCode).email(email).firstName(firstName)
				.lastName(lastName).planId(planId).phone(phone).domain(domain).build();

		CreateResult<Registration> result = NowellpointClient.defaultClient(ENVIRONMENT).registration()
				.signUp(signUpRequest);

		if (result.isSuccess()) {
			model.put("email", email);

			return TemplateBuilder.template().configuration(configuration).controllerClass(SignUpController.class)
					.identity(getIdentity(request)).locale(getLocale(request)).model(model)
					.templateName(Template.SIGN_UP_CONFIRM).timeZone(getTimeZone(request)).build();
		} else {

			ContentService service = new ContentService();
			PlanList planList = service.getPlans();

			Optional<Plan> optional = planList.getItems().stream().filter(plan -> plan.getId().equals(planId))
					.findFirst();

			model.put("action", "createAccount");
			model.put("firstName", firstName);
			model.put("lastName", lastName);
			model.put("email", email);
			model.put("countryCode", countryCode);
			model.put("planId", planId);
			model.put("plan", optional.get());
			model.put("phone", phone);
			model.put("domain", domain);
			model.put("errorMessage", result.getErrorMessage());

			return TemplateBuilder.template()
					.configuration(configuration)
					.controllerClass(SignUpController.class)
					.identity(getIdentity(request))
					.locale(getLocale(request))
					.model(model)
					.templateName(Template.SIGN_UP)
					.timeZone(getTimeZone(request))
					.build();
		}
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String provision(Configuration configuration, Request request, Response response) {
		
		String planCode = request.queryParams("planCode");
		String registrationId = request.queryParams("registrationId");
		
		UpdateResult<Registration> result = null;
		
		if ("FREE".equalsIgnoreCase(planCode)) {
			result = NowellpointClient.defaultClient(ENVIRONMENT)
					.registration()
					.provisionFreePlan(registrationId);
		} else {
			
			String cardholderName = request.queryParams("cardholderName");
			String cardNumber = request.queryParams("cardNumber");
			String expirationMonth = request.queryParams("expirationMonth");
			String expirationYear = request.queryParams("expirationYear");
			String securityCode = request.queryParams("securityCode");
			
			ProvisionRequesst provisionRequest = ProvisionRequesst.builder()
					.cardholderName(cardholderName)
					.cvv(securityCode)
					.expirationMonth(expirationMonth)
					.expirationYear(expirationYear)
					.cardNumber(cardNumber)
					.build();
			
			result = NowellpointClient.defaultClient(ENVIRONMENT).registration().provisionPaidPlan(registrationId, provisionRequest);
		}
		
		Map<String, Object> model = getModel();
		
		if (result.isSuccess()) {
			
			return TemplateBuilder.template()
					.configuration(configuration)
					.controllerClass(SignUpController.class)
					.identity(getIdentity(request))
					.locale(getLocale(request))
					.model(model)
					.templateName(Template.PROVISION_ACCOUNT_CONFIRM)
					.timeZone(getTimeZone(request))
					.build();
			
		} else {
			model.put("errorMessage", result.getErrorMessage());
			
			return TemplateBuilder.template()
					.configuration(configuration)
					.controllerClass(SignUpController.class)
					.identity(getIdentity(request))
					.locale(getLocale(request))
					.model(model)
					.templateName(Template.PROVISION_ACCOUNT)
					.timeZone(getTimeZone(request))
					.build();
		}
		
		
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String verifyEmail(Configuration configuration, Request request, Response response) {

		String emailVerificationToken = request.queryParams("emailVerificationToken");

		UpdateResult<Registration> verificationResult = NowellpointClient.defaultClient(ENVIRONMENT).registration()
				.verifyRegistration(emailVerificationToken);

		Map<String, Object> model = getModel();

		if (verificationResult.isSuccess()) {

			ContentService service = new ContentService();
			PlanList planList = service.getPlans();

			Optional<Plan> optional = planList.getItems().stream()
					.filter(plan -> plan.getId().equals(verificationResult.getTarget().getPlanId())).findFirst();

			model.put("registration", verificationResult.getTarget());
			model.put("plan", optional.get());
			model.put("successMessage", MessageProvider.getMessage(Locale.US, "email.verification.success"));

		} else {
			model.put("errorMessage", verificationResult.getErrorMessage());
		}
		
		return TemplateBuilder.template()
				.configuration(configuration)
				.controllerClass(SignUpController.class)
				.identity(getIdentity(request))
				.locale(getLocale(request))
				.model(model)
				.templateName(Template.PROVISION_ACCOUNT)
				.timeZone(getTimeZone(request))
				.build();
	}
}