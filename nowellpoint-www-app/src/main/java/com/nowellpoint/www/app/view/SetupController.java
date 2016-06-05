package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

public class SetupController {
	
	public SetupController(Configuration cfg) {
		
		get("/app/setup", (request, response) -> {
			
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("account", request.attribute("account"));
			
			return new ModelAndView(model, "secure/setup.html");
			
		}, new FreeMarkerEngine(cfg));		
	}
}