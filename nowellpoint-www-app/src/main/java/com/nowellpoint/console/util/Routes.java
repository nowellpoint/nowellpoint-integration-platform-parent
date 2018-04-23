package com.nowellpoint.console.util;

import com.nowellpoint.console.view.AuthenticationController;
import com.nowellpoint.console.view.IndexController;
import com.nowellpoint.console.view.StartController;

import freemarker.template.Configuration;

public class Routes {
	
	public static void configureRoutes(Configuration configuration) {
		IndexController.configureRoutes(configuration);
		AuthenticationController.configureRoutes(configuration);
		StartController.configureRoutes(configuration);
	}
}