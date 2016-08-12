package com.nowellpoint.www.app.view;

import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;
import spark.Route;

public class SetupController extends AbstractController {
	
	public SetupController(Configuration cfg) {
		super(SetupController.class, cfg);		
	}

	@Override
	public void configureRoutes(Configuration configuration) {
		// TODO Auto-generated method stub
		
	}
	
	public Route showSetup = (Request request, Response response) -> {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		return render(request, model, Path.Template.SETUP);
	};
}