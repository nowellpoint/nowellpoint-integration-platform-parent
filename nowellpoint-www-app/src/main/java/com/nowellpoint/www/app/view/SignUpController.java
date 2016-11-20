package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.nowellpoint.client.NowellpointClient;
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
		get(Path.Route.SIGN_UP, (request, response) -> showSignUp(configuration, request, response));
		post(Path.Route.SIGN_UP, (request, response) -> signUp(configuration, request, response));
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String showSignUp(Configuration configuration, Request request, Response response) {
		SignUpRequest signUpRequest = new SignUpRequest();
		signUpRequest.setCountryCode("US");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("signUpRequest", signUpRequest);
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
				.withCountryCode(request.queryParams("countryCode"));
		
		SignUpResult<User> signUpResult = new NowellpointClient()
				.user()
				.signUp(signUpRequest);
		
		Map<String, Object> model = new HashMap<String, Object>();
    	
    	if (signUpResult.isSuccess()) {
    		model.put("successMessage", MessageProvider.getMessage(Locale.US, "signUpConfirm"));   
    	} else {
    		model.put("signUpRequest", signUpRequest);
    		model.put("errorMessage", signUpResult.getErrorMessage());
    	}
    	
    	return render(configuration, request, response, model, Template.SIGN_UP);
	}
}