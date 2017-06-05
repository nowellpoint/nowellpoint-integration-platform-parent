package com.nowellpoint.www.app.view;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;

import com.nowellpoint.client.Environment;
import com.nowellpoint.client.model.Contact;
import com.nowellpoint.client.model.CreditCard;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.Plan;
import com.nowellpoint.client.model.PlanList;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.TemplateBuilder;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class SignUpController extends AbstractStaticController {
	
	public static class Template {
		public static final String PLANS = "plans.html";
		public static final String SIGN_UP = "signup.html";
		public static final String SIGN_UP_CONFIRM = "signup-confirm.html";
		public static final String VERIFY_EMAIL = "verify-email.html";
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String plans(Configuration configuration, Request request, Response response) {
		
		HttpResponse httpResponse = RestResource.get(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")).getEnvironmentUrl())
				.path("plans")
				.queryParameter("locale", Locale.getDefault().toString())
				.queryParameter("language", "en_US")
				.execute();
		
		PlanList planList = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			planList = httpResponse.getEntity(PlanList.class);
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
		}
		
		List<Plan> plans = planList.getItems()
				.stream()
				.sorted((p1, p2) -> p1.getPrice().getUnitPrice().compareTo(p2.getPrice().getUnitPrice()))
				.collect(Collectors.toList());
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("countryCode", Locale.getDefault().getCountry());
		model.put("planList", plans);
		
		return render(SignUpController.class, configuration, request, response, model, Template.PLANS);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String freeAccount(Configuration configuration, Request request, Response response) {
		
		HttpResponse httpResponse = RestResource.get(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")).getEnvironmentUrl())
				.path("plans")
				.queryParameter("locale", Locale.getDefault().toString())
				.queryParameter("language", "en_US")
				.execute();
		
		PlanList planList = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			planList = httpResponse.getEntity(PlanList.class);
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
		}
		
		Optional<Plan> optional = planList.getItems()
				.stream()
				.filter(plan -> plan.getPlanCode().equals("FREE"))
				.findFirst();
		
		CreditCard creditCard = new CreditCard();
		creditCard.setExpirationMonth(String.valueOf(LocalDate.now().getMonthValue()));
		creditCard.setExpirationYear(String.valueOf(LocalDate.now().getYear()));
		creditCard.setBillingContact(new Contact());
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("planId", optional.get().getId());
		model.put("plan", optional.get());
		model.put("countryCode", Locale.getDefault().getCountry());
		model.put("creditCard", creditCard);
		model.put("action", "createAccount");
		
		return TemplateBuilder.template()
				.withConfiguration(configuration)
				.withControllerClass(SignUpController.class)
				.withIdentity(getIdentity(request))
				.withLocale(getLocale(request))
				.withModel(model)
				.withTemplateName(Template.SIGN_UP)
				.withTimeZone(getTimeZone(request))
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
			
			HttpResponse httpResponse = RestResource.get(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")).getEnvironmentUrl())
					.path("plans")
					.path(planId)
					.execute();
			
			if (httpResponse.getStatusCode() != Status.OK) {
				throw new BadRequestException(httpResponse.getAsString());
			}
			
			Plan plan = httpResponse.getEntity(Plan.class);
			
			CreditCard creditCard = new CreditCard();
			creditCard.setExpirationMonth(String.valueOf(LocalDate.now().getMonthValue()));
			creditCard.setExpirationYear(String.valueOf(LocalDate.now().getYear()));
			creditCard.setBillingContact(new Contact());
			
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("planId", planId);
			model.put("plan", plan);
			model.put("countryCode", Locale.getDefault().getCountry());
			model.put("creditCard", creditCard);
			model.put("action", "createAccount");
			
			return TemplateBuilder.template()
					.withConfiguration(configuration)
					.withControllerClass(SignUpController.class)
					.withIdentity(getIdentity(request))
					.withLocale(getLocale(request))
					.withModel(model)
					.withTemplateName(Template.SIGN_UP)
					.withTimeZone(getTimeZone(request))
					.build();
			
		} else {
			
			HttpResponse httpResponse = RestResource.get(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")).getEnvironmentUrl())
					.path("plans")
					.queryParameter("locale", Locale.getDefault().toString())
					.queryParameter("language", "en_US")
					.execute();
			
			PlanList planList = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				planList = httpResponse.getEntity(PlanList.class);
			} else {
				throw new ServiceUnavailableException(httpResponse.getAsString());
			}
			
			List<Plan> plans = planList.getItems()
					.stream()
					.sorted((p1, p2) -> p1.getPrice().getUnitPrice().compareTo(p2.getPrice().getUnitPrice()))
					.collect(Collectors.toList());
			
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("plans", plans);
			model.put("action", "listPlans");
			
			return TemplateBuilder.template()
					.withConfiguration(configuration)
					.withControllerClass(SignUpController.class)
					.withIdentity(getIdentity(request))
					.withLocale(getLocale(request))
					.withModel(model)
					.withTemplateName(Template.SIGN_UP)
					.withTimeZone(getTimeZone(request))
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
		String password = request.queryParams("password");
		String confirmPassword = request.queryParams("confirmPassword");
		String countryCode = request.queryParams("countryCode");
		String planId = request.queryParams("planId");
		String cardNumber = request.queryParams("cardNumber");
		String expirationMonth = request.queryParams("expirationMonth");
		String expirationYear = request.queryParams("expirationYear");
		String securityCode = request.queryParams("securityCode");
		
		Map<String, Object> model = getModel();
		
		try {
			HttpResponse httpResponse = RestResource.post(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")).getEnvironmentUrl())
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.acceptCharset("UTF-8")
					.path("signup")
					.parameter("firstName", URLEncoder.encode(firstName, "UTF-8"))
					.parameter("lastName", URLEncoder.encode(lastName, "UTF-8"))
					.parameter("email", email)
					.parameter("countryCode", countryCode)
					.parameter("password", URLEncoder.encode(password, "UTF-8"))
					.parameter("confirmPassword", URLEncoder.encode(confirmPassword, "UTF-8"))
					.parameter("planId", planId)
					.parameter("cardNumber", cardNumber)
					.parameter("expirationMonth", expirationMonth)
					.parameter("expirationYear", expirationYear)
					.parameter("securityCode", securityCode)
					.execute();
	    	
	    	if (httpResponse.getStatusCode() != Status.OK) {
	    		
	    		Error error = httpResponse.getEntity(Error.class);
	    		
	    		httpResponse = RestResource.get(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")).getEnvironmentUrl())
						.path("plans")
						.path(planId)
						.execute();
	    		
	    		Plan plan = httpResponse.getEntity(Plan.class);
	    		
	    		model.put("action", "createAccount");
	    		model.put("firstName", firstName);
	    		model.put("lastName", lastName);
	    		model.put("email", email);
	    		model.put("password", password);
	    		model.put("countryCode", countryCode);
	    		model.put("planId", planId);
	    		model.put("plan", plan);
	    		model.put("errorMessage", error.getErrorMessage());
	    		
	    	} else {
	    		
	    		model.put("email", email);
	        	
	        	return TemplateBuilder.template()
	    				.withConfiguration(configuration)
	    				.withControllerClass(SignUpController.class)
	    				.withIdentity(getIdentity(request))
	    				.withLocale(getLocale(request))
	    				.withModel(model)
	    				.withTemplateName(Template.SIGN_UP_CONFIRM)
	    				.withTimeZone(getTimeZone(request))
	    				.build();
	    	}
	    	
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		
		return TemplateBuilder.template()
				.withConfiguration(configuration)
				.withControllerClass(SignUpController.class)
				.withIdentity(getIdentity(request))
				.withLocale(getLocale(request))
				.withModel(model)
				.withTemplateName(Template.SIGN_UP)
				.withTimeZone(getTimeZone(request))
				.build();
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
		
		HttpResponse httpResponse = RestResource.post(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")).getEnvironmentUrl())
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.path("signup")
				.path("verify-email")
				.path(emailVerificationToken)
				.execute();
		
		Map<String,Object> model = getModel();
    	
    	if (httpResponse.getStatusCode() == Status.OK) {
    		model.put("successMessage", MessageProvider.getMessage(Locale.US, "email.verification.success"));
    	} else {
    		model.put("errorMessage", MessageProvider.getMessage(Locale.US, "email.verification.failure"));
    	}
    	
    	return TemplateBuilder.template()
				.withConfiguration(configuration)
				.withControllerClass(SignUpController.class)
				.withIdentity(getIdentity(request))
				.withLocale(getLocale(request))
				.withModel(model)
				.withTemplateName(Template.VERIFY_EMAIL)
				.withTimeZone(getTimeZone(request))
				.build();
	}
}