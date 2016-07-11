package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.util.Map;

import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class DashboardController extends AbstractController {
	
	public DashboardController(Configuration configuration) {     
		super(DashboardController.class, configuration);
	}
	
	@Override
	public void configureRoutes(Configuration configuration) {
		get("/app/start", (request, response) -> showStartPage(request, response), new FreeMarkerEngine(configuration));
        
        get("/app/dashboard", (request, response) -> showDashboard(request, response), new FreeMarkerEngine(configuration));
	}
	
	private ModelAndView showStartPage(Request request, Response response) {
    	Map<String,Object> model = getModel();
    	model.put("account", getAccount(request));
		return new ModelAndView(model, "secure/start.html");
	}
	
	private ModelAndView showDashboard(Request request, Response response) {
    	Map<String,Object> model = getModel();
    	model.put("account", getAccount(request));
		return new ModelAndView(model, "secure/dashboard.html");
	}
}