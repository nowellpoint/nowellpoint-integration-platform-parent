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
import com.nowellpoint.client.auth.TokenCredentials;
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
	}
	
	public SignUpController() {
		super(SignUpController.class);
	}
	
	@Override
	public void configureRoutes(Configuration configuration) {
		get(Path.Route.LIST_PLANS, (request, response) -> listPlans(configuration, request, response));
		get(Path.Route.CREATE_ACCOUNT, (request, response) -> createAccount(configuration, request, response));
		get(Path.Route.PAYMENT_METHOD, (request, response) -> listPlans(configuration, request, response));
		post(Path.Route.SIGN_UP, (request, response) -> signUp(configuration, request, response));
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
	
	private String createAccount(Configuration configuration, Request request, Response response) {
		
		String planId = request.queryParams("planId");
		
		System.out.println(planId);
		
		Plan plan = new NowellpointClient()
				.plan()
				.get(planId)
				.getTarget();
		
		System.out.println("here");
		
		SignUpRequest signUpRequest = new SignUpRequest()
				.withCountryCode("US")
				.withPlanId(planId);
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("signUpRequest", signUpRequest);
		model.put("plan", plan);
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
		
		SignUpRequest signUpRequest = new SignUpRequest()
				.withFirstName(request.queryParams("firstName"))
				.withLastName(request.queryParams("lastName"))
				.withEmail(request.queryParams("email"))
				.withPassword(request.queryParams("password"))
				.withConfirmPassword(request.queryParams("confirmPassword"))
				.withCountryCode(request.queryParams("countryCode"))
				.withPlanId(request.queryParams("planId"));
		
//		SignUpResult<User> signUpResult = new NowellpointClient()
//				.user()
//				.signUp(signUpRequest);
		
		CreditCard creditCard = new CreditCard();
		creditCard.setExpirationMonth(String.valueOf(LocalDate.now().getMonthValue()));
		creditCard.setExpirationYear(String.valueOf(LocalDate.now().getYear()));
		creditCard.setBillingContact(new Contact());
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("action", "paymentMethod");
		model.put("creditCard", creditCard);
    	
//    	if (signUpResult.isSuccess()) {
//    		model.put("successMessage", MessageProvider.getMessage(Locale.US, "signUpConfirm"));   
//    	} else {
//    		model.put("signUpRequest", signUpRequest);
//    		model.put("errorMessage", signUpResult.getErrorMessage());
//    	}
    	
    	return render(configuration, request, response, model, Template.SIGN_UP);
	}
}