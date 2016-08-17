package com.nowellpoint.www.app.view;

import java.util.Map;

import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;
import spark.Route;

public class DashboardController extends AbstractController {
	
	public DashboardController(Configuration configuration) {     
		super(DashboardController.class, configuration);
	}
	
	public Route showStartPage = (Request request, Response response) -> {
    	Map<String,Object> model = getModel();
    	model.put("account", getAccount(request));
    	return render(request, model, Path.Template.START);
	};
	
	public Route showDashboard = (Request request, Response response) -> {
    	Map<String,Object> model = getModel();
    	model.put("account", getAccount(request));
    	return render(request, model, Path.Template.DASHBOARD);
	};
}