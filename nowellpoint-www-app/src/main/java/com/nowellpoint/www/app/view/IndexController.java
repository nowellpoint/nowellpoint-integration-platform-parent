package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.client.Environment;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.Path;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class IndexController extends AbstractController {
	
	private static final Logger logger = Logger.getLogger(IndexController.class.getName());
	
	public static class Template {
		public static final String INDEX = "index2.html";
	}
	
	public IndexController(Configuration configuration) {
		super(IndexController.class);
		configureRoutes(configuration);
	}
	
	@Override
	public void configureRoutes(Configuration configuration) {
		get(Path.Route.INDEX, (request, response) -> index(configuration, request, response));
		post(Path.Route.CONTACT, (request, response) -> contactUs(configuration, request, response));
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String index(Configuration configuration, Request request, Response response) {
		return render(configuration, request, response, getModel(), Template.INDEX);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String contactUs(Configuration configuration, Request request, Response response) {
		System.out.println(request.queryParams("firstName"));
		System.out.println(request.queryParams("lastName"));
		System.out.println(request.queryParams("email"));
		System.out.println(request.queryParams("phone"));
		System.out.println(request.queryParams("company"));
		System.out.println(request.queryParams("message"));
		
//    	HttpResponse httpResponse = RestResource.post(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")).getEnvironmentUrl())
//    			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
//    			.path("contact")
//				.parameter("firstName", request.queryParams("firstName"))
//				.parameter("lastName", request.queryParams("lastName"))
//				.parameter("email", request.queryParams("email"))
//				.parameter("phone", request.queryParams("phone"))
//				.parameter("company", request.queryParams("company"))
//				.parameter("message", request.queryParams("message"))
//    			.execute();
//    	
//    	logger.info("Status Code: " + httpResponse.getStatusCode());
    	
    	Map<String, Object> model = new HashMap<String, Object>();
    	model.put("successMessage", MessageProvider.getMessage(Locale.US, "contactConfirm"));
    	//return render(configuration, request, response, model, Path.Template.CONTACT_US);
    	
    	//response.redirect(Path.Route.INDEX.concat("#contact"));
    	
    	return MessageProvider.getMessage(Locale.US, "contactConfirm");
	};
}