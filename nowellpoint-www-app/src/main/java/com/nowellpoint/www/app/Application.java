package com.nowellpoint.www.app;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.halt;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.model.IsoCountry;
import com.nowellpoint.client.model.NowellpointServiceException;
import com.nowellpoint.www.app.util.Path;
import com.nowellpoint.www.app.view.AccountProfileController;
import com.nowellpoint.www.app.view.AdministrationController;
//import com.nowellpoint.www.app.view.ApplicationController;
import com.nowellpoint.www.app.view.AuthenticationController;
import com.nowellpoint.www.app.view.ContactUsController;
import com.nowellpoint.www.app.view.DashboardController;
import com.nowellpoint.www.app.view.NotificationController;
//import com.nowellpoint.www.app.view.ProjectController;
import com.nowellpoint.www.app.view.SalesforceConnectorController;
import com.nowellpoint.www.app.view.SalesforceOauthController;
import com.nowellpoint.www.app.view.ScheduledJobController;
//import com.nowellpoint.www.app.view.SetupController;
import com.nowellpoint.www.app.view.SignUpController;
import com.nowellpoint.www.app.view.VerifyEmailController;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.servlet.SparkApplication;
import spark.template.freemarker.FreeMarkerEngine;

public class Application implements SparkApplication {
	
    public static void main(String[] args) throws Exception {
    	new Application().init();
    }

	@Override
	public void init() {
		
		//
		// add resource bundle
		//

		ResourceBundle messages = ResourceBundle.getBundle("messages", Locale.US);
        
        //
		// Configure FreeMarker
		//
        
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);

		//
		// set configuration options
		//

        configuration.setClassForTemplateLoading(this.getClass(), "/views");
		configuration.setDefaultEncoding("UTF-8");
		configuration.setLocale(Locale.getDefault());
		configuration.setTimeZone(TimeZone.getDefault());
        
        //
        // load countries list
        //
		
		try {			
			List<IsoCountry> isoCountries = loadCountries();
			
			List<IsoCountry> filteredList = isoCountries.stream()
					.filter(country -> "US".equals(country.getLanguage()))
					.sorted((p1, p2) -> p1.getName().compareTo(p2.getName()))
					.collect(Collectors.toList());
			
			configuration.setSharedVariable("countryList", filteredList);
			
		} catch (TemplateModelException e) {
			e.printStackTrace();
			halt();
		}
        
        //
        // load messages for locale
        //
        
        messages.keySet().stream().forEach(message -> {
        	try {
				configuration.setSharedVariable(message, messages.getString(message));
			} catch (TemplateException e) {
				e.printStackTrace();
				halt();
			}
        });
        
        //
        //
        //
        
        AuthenticationController authenticationController = new AuthenticationController();
        AccountProfileController accountProfileController = new AccountProfileController();
        VerifyEmailController verifyEmailController = new VerifyEmailController();
        DashboardController dashboardController = new DashboardController();
        AdministrationController administrationController = new AdministrationController();
        SignUpController signUpController = new SignUpController();
        NotificationController notificationController = new NotificationController();
        //SetupController setupController = new SetupController(cfg);
        SalesforceOauthController salesforceOauthController = new SalesforceOauthController();
        ContactUsController contactUsController = new ContactUsController();
        //ApplicationController applicationController = new ApplicationController(cfg);
        //ProjectController projectController = new ProjectController(cfg);
        SalesforceConnectorController salesforceConnectorController = new SalesforceConnectorController();
        ScheduledJobController scheduledJobsController = new ScheduledJobController();
        
        //
        // setup routes
        //
        
        authenticationController.configureRoutes(configuration);
        accountProfileController.configureRoutes(configuration);
        administrationController.configureRoutes(configuration);
        salesforceOauthController.configureRoutes(configuration);
        verifyEmailController.configureRoutes(configuration);
        dashboardController.configureRoutes(configuration);
        signUpController.configureRoutes(configuration);
        notificationController.configureRoutes(configuration);
        contactUsController.configureRoutes(configuration);
        salesforceConnectorController.configureRoutes(configuration);
        scheduledJobsController.configureRoutes(configuration);

        get(Path.Route.INDEX, (request, response) -> getContextRoot(request, response), new FreeMarkerEngine(configuration));
        
        get(Path.Route.SERVICES, (request, response) -> getServices(request, response), new FreeMarkerEngine(configuration));
        
        get(Path.Route.HEALTH_CHECK, (request, response) -> healthCheck(request, response));
        
        
        //
        // exception handlers
        //
        
        exception(NowellpointServiceException.class, (exception, request, response) ->{
        	response.status(((NowellpointServiceException) exception).getStatusCode());
        	response.body(exception.getMessage());
        });
        
        exception(BadRequestException.class, (exception, request, response) -> {
            response.status(400);
            response.body(exception.getMessage());
        });
        
        exception(NotFoundException.class, (exception, request, response) -> {
        	exception.printStackTrace();
            response.status(404);
            response.body(exception.getMessage());
        });
        
        exception(InternalServerErrorException.class, (exception, request, response) -> {
            response.status(500);
            response.body(exception.getMessage());
        });
        
        exception(Exception.class, (exception, request, response) -> {
        	response.status(500);
            response.body(exception.getMessage());
        });
	}
	
	/**
	 * 
	 * @return
	 */
	
	private static List<IsoCountry> loadCountries() {
		HttpResponse httpResponse = RestResource.get(System.getenv("NOWELLPOINT_API_ENDPOINT"))
				.path("iso-countries")
				.execute();
		
		List<IsoCountry> countries = Collections.emptyList();
		
		if (httpResponse.getStatusCode() == Status.OK) {
			countries = httpResponse.getEntityList(IsoCountry.class);
		}
		
		return countries;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView getContextRoot(Request request, Response response) {
    	Map<String,Object> model = new HashMap<String,Object>();
    	return new ModelAndView(model, Path.Template.INDEX);
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView getServices(Request request, Response response) {
    	Map<String,Object> model = new HashMap<String,Object>();
		return new ModelAndView(model, Path.Template.SERVICES);
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static String healthCheck(Request request, Response response) {
		response.status(200);
    	return "";
	}
}