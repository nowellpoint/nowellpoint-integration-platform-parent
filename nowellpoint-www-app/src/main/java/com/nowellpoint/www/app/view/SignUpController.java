package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.Contact;
import com.nowellpoint.client.model.CreditCard;
import com.nowellpoint.client.model.GetPlansRequest;
import com.nowellpoint.client.model.GetResult;
import com.nowellpoint.client.model.Plan;
import com.nowellpoint.client.model.SignUpRequest;
import com.nowellpoint.client.model.SignUpResult;
import com.nowellpoint.client.model.User;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class SignUpController extends AbstractController {
	
	public static class Template {
		public static final String SIGN_UP = "signup.html";
		public static final String VERIFY_EMAIL = "verify-email.html";
	}
	
	public SignUpController() {
		super(SignUpController.class);
	}
	
	@Override
	public void configureRoutes(Configuration configuration) {
		get(Path.Route.LIST_PLANS, (request, response) -> listPlans(configuration, request, response));
		get(Path.Route.SETUP_ACCOUNT, (request, response) -> setupAccount(configuration, request, response));
		post(Path.Route.SIGN_UP, (request, response) -> signUp(configuration, request, response));
		get(Path.Route.VERIFY_EMAIL, (request, response) -> verifyEmail(configuration, request, response));
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String listPlans(Configuration configuration, Request request, Response response) {
		
		GetPlansRequest getPlansRequest = new GetPlansRequest()
				.withLanguageSidKey("en_US")
				.withLocaleSidKey("en_US");
		
		GetResult<List<Plan>> getResult = new NowellpointClient()
				.plan()
				.getPlans(getPlansRequest);
		
		List<Plan> plans = getResult.getTarget().stream()
				.sorted((p1, p2) -> p1.getPrice().getUnitPrice().compareTo(p2.getPrice().getUnitPrice()))
				.collect(Collectors.toList());
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("plans", plans);
		model.put("action", "listPlans");
		
		return render(configuration, request, response, model, Template.SIGN_UP);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String setupAccount(Configuration configuration, Request request, Response response) {
		
		String planId = request.queryParams("planId");
		
		Plan plan = new NowellpointClient()
				.plan()
				.get(planId);
		
		SignUpRequest signUpRequest = new SignUpRequest()
				.withCountryCode("US")
				.withPlanId(planId);
		
		CreditCard creditCard = new CreditCard();
		creditCard.setExpirationMonth(String.valueOf(LocalDate.now().getMonthValue()));
		creditCard.setExpirationYear(String.valueOf(LocalDate.now().getYear()));
		creditCard.setBillingContact(new Contact());
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("signUpRequest", signUpRequest);
		model.put("plan", plan);
		model.put("creditCard", creditCard);
		model.put("action", "createAccount");
		
		return render(configuration, request, response, model, Template.SIGN_UP);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String signUp(Configuration configuration, Request request, Response response) {
		
		String firstName = request.queryParams("firstName");
		String lastName = request.queryParams("lastName");
		String email = request.queryParams("email");
		String password = request.queryParams("password");
		String confirmPassword = request.queryParams("confirmPassword");
		String countryCode = request.queryParams("countryCode");
		String planId = request.queryParams("planId");
		String cardNumber = request.queryParams("cardNumber");
		String expirationMonth = request.queryParams("expirationMonth");
		String expirationYear = request.queryParams("expirationYear");
		String securityCode = request.queryParams("securityCode");
		
		SignUpRequest signUpRequest = new SignUpRequest()
				.withFirstName(firstName)
				.withLastName(lastName)
				.withEmail(email)
				.withPassword(password)
				.withConfirmPassword(confirmPassword)
				.withCountryCode(countryCode)
				.withPlanId(planId)
				.withCardNumber(cardNumber)
				.withExpirationMonth(expirationMonth)
				.withExpirationYear(expirationYear)
				.withSecurityCode(securityCode);
		
		SignUpResult<User> signUpResult = new NowellpointClient()
				.user()
				.signUp(signUpRequest);
		
		Map<String, Object> model = new HashMap<String, Object>();
		
    	if (! signUpResult.isSuccess()) {
    		
    		Plan plan = new NowellpointClient()
    				.plan()
    				.get(planId);
    		
    		model.put("action", "createAccount");
    		model.put("signUpRequest", signUpRequest);
    		model.put("plan", plan);
    		model.put("errorMessage", signUpResult.getErrorMessage());
    		
    		return render(configuration, request, response, model, Template.SIGN_UP);
    	}

    	model.put("action", "signUpSuccess");
    	model.put("successMessage", MessageProvider.getMessage(Locale.US, "signUpConfirm"));   
    	
    	return render(configuration, request, response, model, Template.SIGN_UP);
	}
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String verifyEmail(Configuration configuration, Request request, Response response) {
		
		String emailVerificationToken = request.queryParams("emailVerificationToken");
		
		SignUpResult<User> signUpResult = new NowellpointClient()
				.user()
				.verifyEmail(emailVerificationToken);
		
		Map<String,Object> model = getModel();
		
		if (signUpResult.isSuccess()) {
			model.put("successMessage", MessageProvider.getMessage(Locale.US, "email.verification.success"));
		} else {
			model.put("errorMessage", MessageProvider.getMessage(Locale.US, "email.verification.failure"));
		}
		
    	return render(configuration, request, response, model, Template.VERIFY_EMAIL);
	}
}