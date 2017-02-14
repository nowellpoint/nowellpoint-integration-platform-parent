package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.util.HashMap;
import java.util.Map;

import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class SetupController extends AbstractController {
	
	public static class Template {
		public static final String SETUP = String.format(APPLICATION_CONTEXT, "setup.html");
	}
	
	public SetupController(Configuration configuration) {
		super(SetupController.class);
		configureRoutes(configuration);
	}
	
	private void configureRoutes(Configuration configuration) {
		get(Path.Route.SETUP, (request, response) -> showSetup(configuration, request, response));
	}
	
	private String showSetup(Configuration configuration, Request request, Response response) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		return render(configuration, request, response, model, Template.SETUP);
	};
}