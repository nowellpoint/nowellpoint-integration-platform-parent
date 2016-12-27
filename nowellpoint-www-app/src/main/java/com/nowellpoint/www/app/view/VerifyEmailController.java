package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.util.Locale;
import java.util.Map;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.SignUpResult;
import com.nowellpoint.client.model.User;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class VerifyEmailController extends AbstractController {
	
	public static class Template {
		public static final String ADMINISTRATION_HOME = String.format(APPLICATION_CONTEXT, "administration-home.html");
		public static final String CACHE_MANAGER = String.format(APPLICATION_CONTEXT, "cache.html");
		public static final String VERIFY_EMAIL = "verify-email.html";
	}

	public VerifyEmailController() {
		super(VerifyEmailController.class);
	}
	
	@Override
	public void configureRoutes(Configuration configuration) {
		get(Path.Route.VERIFY_EMAIL, (request, response) -> verifyEmail(configuration, request, response));
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