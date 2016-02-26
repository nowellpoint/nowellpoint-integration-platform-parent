package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class DashboardController {
	
	public DashboardController(Configuration cfg) {     
        get("/app/dashboard", (request, response) -> getContextRoot(request, response), new FreeMarkerEngine(cfg));
	}
	
	private static ModelAndView getContextRoot(Request request, Response response) {
    	Map<String,Object> model = new HashMap<String,Object>();
    	model.put("account", request.attribute("account"));
		return new ModelAndView(model, "secure/dashboard.html");
	}
}