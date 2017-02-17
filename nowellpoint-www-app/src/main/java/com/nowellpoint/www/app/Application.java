package com.nowellpoint.www.app;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.halt;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;

import com.nowellpoint.client.Environment;
import com.nowellpoint.client.model.IsoCountry;
import com.nowellpoint.client.model.IsoCountryList;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.http.Status;
import com.nowellpoint.www.app.util.Path;
import com.nowellpoint.www.app.view.AccountProfileController;
import com.nowellpoint.www.app.view.AdministrationController;
import com.nowellpoint.www.app.view.AuthenticationController;
import com.nowellpoint.www.app.view.DashboardController;
import com.nowellpoint.www.app.view.IndexController;
import com.nowellpoint.www.app.view.NotificationController;
import com.nowellpoint.www.app.view.SalesforceConnectorController;
import com.nowellpoint.www.app.view.SalesforceOauthController;
import com.nowellpoint.www.app.view.ScheduledJobController;
import com.nowellpoint.www.app.view.SignUpController;
import com.nowellpoint.www.app.view.StartController;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateModelException;
import spark.Request;
import spark.Response;
import spark.servlet.SparkApplication;

public class Application implements SparkApplication {
	
    public static void main(String[] args) throws Exception {
    	new Application().init();
    }

	@Override
	public void init() {
        
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
		
		List<IsoCountry> isoCountries = loadCountries(configuration.getLocale());
		
		try {			
			configuration.setSharedVariable("countryList", isoCountries);
		} catch (TemplateModelException e) {
			e.printStackTrace();
			halt();
		}
        
        //
        // configure routes
        //
        
		configureRoutes(configuration);
        
		//
		//
		//
        
        get(Path.Route.HEALTH_CHECK, (request, response) -> healthCheck(request, response));
        
        
        //
        // exception handlers
        //
        
        exception(ServiceUnavailableException.class, (exception, request, response) -> {
        	exception.printStackTrace();
        	response.status(Status.SERVICE_UNAVAILABLE);
        	response.body(exception.getMessage());
        });
        
        exception(BadRequestException.class, (exception, request, response) -> {
        	exception.printStackTrace();
            response.status(400);
            response.body(exception.getMessage());
        });
        
        exception(NotFoundException.class, (exception, request, response) -> {
        	exception.printStackTrace();
            response.status(404);
            response.body(exception.getMessage());
        });
        
        exception(InternalServerErrorException.class, (exception, request, response) -> {
  
        	Map<String, Object> model = new HashMap<>();
        	model.put("errorMessage", exception.getMessage());
        	
        	Writer output = new StringWriter();
    		try {
    			Template template = configuration.getTemplate("error.html");
    			freemarker.core.Environment environment = template.createProcessingEnvironment(model, output);
    			environment.process();
    			response.status(500);
            	response.body(output.toString());
            	output.flush();
    		} catch (Exception e) {
    			e.printStackTrace();
    			halt();
    		}
        });
        
        exception(Exception.class, (exception, request, response) -> {
        	exception.printStackTrace();
        	response.status(500);
            response.body(exception.getMessage());
        });
	}
	
	private static void configureRoutes(Configuration configuration) {
		IndexController.configureRoutes(configuration);
		SignUpController.configureRoutes(configuration);
        AuthenticationController.configureRoutes(configuration);
        StartController.configureRoutes(configuration);
        DashboardController.configureRoutes(configuration);
        AdministrationController.configureRoutes(configuration);
        AccountProfileController.configureRoutes(configuration);
        SalesforceOauthController.configureRoutes(configuration);
        ScheduledJobController.configureRoutes(configuration);
        SalesforceConnectorController.configureRoutes(configuration);
        NotificationController.configureRoutes(configuration);
	}
	
	/**
	 * 
	 * @return
	 */
	
	private static List<IsoCountry> loadCountries(Locale locale) {
		HttpResponse httpResponse = RestResource.get(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")).getEnvironmentUrl())
				.path("iso-countries")
				.execute();
		
		List<IsoCountry> countries = Collections.emptyList();
		
		if (httpResponse.getStatusCode() == Status.OK) {
			
			IsoCountryList isoCountryList = httpResponse.getEntity(IsoCountryList.class);
			
			countries = isoCountryList.getItems()
					.stream()
					.filter(country -> "US".equals(country.getLanguage()))
					.sorted((p1, p2) -> p1.getName().compareTo(p2.getName()))
					.collect(Collectors.toList());
		}
		
		return countries;
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