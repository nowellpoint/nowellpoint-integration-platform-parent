package com.nowellpoint.www.app.view;

import java.util.Locale;

import com.nowellpoint.client.Environment;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.TemplateBuilder;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class IndexController extends AbstractStaticController {
	
	private static final Logger logger = Logger.getLogger(IndexController.class.getName());
	
	public static class Template {
		public static final String INDEX = "index.html";
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String serveIndexPage(Configuration configuration, Request request, Response response) {
		return TemplateBuilder.template()
				.configuration(configuration)
				.controllerClass(IndexController.class)
				.identity(getIdentity(request))
				.locale(getLocale(request))
				.templateName(Template.INDEX)
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
	
	public static String contact(Configuration configuration, Request request, Response response) {
		
    	HttpResponse httpResponse = RestResource.post(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")).getEnvironmentUrl())
    			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
    			.path("leads")
				.parameter("firstName", request.queryParams("firstName"))
				.parameter("lastName", request.queryParams("lastName"))
				.parameter("email", request.queryParams("email"))
				.parameter("phone", request.queryParams("phone"))
				.parameter("company", request.queryParams("company"))
				.parameter("message", request.queryParams("message"))
    			.execute();
    	
    	logger.info(httpResponse.getHeaders().get("Location"));
    	
    	return MessageProvider.getMessage(Locale.US, "contactConfirm");
	};
}