package com.nowellpoint.www.app.view;

import java.util.Map;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;
import spark.Route;

public class VerifyEmailController extends AbstractController {

	public VerifyEmailController(Configuration configuration) {
		super(VerifyEmailController.class, configuration);
	}
	
	public Route verifyEmail = (Request request, Response response) -> {
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.path("signup")
				.path("verify-email")
				.path(request.queryParams("emailVerificationToken"))
				.execute();
		
		Map<String,Object> model = getModel();
		
    	if (httpResponse.getStatusCode() == Status.OK) {
    		model.put("successMessage", "Your email has been verified.");
    	} else {
    		model.put("errorMessage", "We are unable to verify your email address.");
    	}
    	
    	return render(request, model, Path.Template.VERIFY_EMAIL);
	};
}