package com.nowellpoint.www.app;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.halt;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
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
import com.nowellpoint.client.Environment;
import com.nowellpoint.client.model.IsoCountry;
import com.nowellpoint.client.model.IsoCountryList;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.www.app.util.Path;
import com.nowellpoint.www.app.view.AccountProfileController;
import com.nowellpoint.www.app.view.AdministrationController;
import com.nowellpoint.www.app.view.AuthenticationController;
import com.nowellpoint.www.app.view.Controller;
import com.nowellpoint.www.app.view.DashboardController;
import com.nowellpoint.www.app.view.IndexController;
import com.nowellpoint.www.app.view.NotificationController;
import com.nowellpoint.www.app.view.SalesforceConnectorController;
import com.nowellpoint.www.app.view.SalesforceOauthController;
import com.nowellpoint.www.app.view.ScheduledJobController;
import com.nowellpoint.www.app.view.SignUpController;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
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
        
        try {
			newController(IndexController.class, configuration);
			newController(AuthenticationController.class, configuration);
			newController(DashboardController.class, configuration);
			newController(AccountProfileController.class, configuration);
			newController(AdministrationController.class, configuration);
			newController(SignUpController.class, configuration);
			newController(NotificationController.class, configuration);
			newController(SalesforceOauthController.class, configuration);
			newController(SalesforceConnectorController.class, configuration);
			newController(ScheduledJobController.class, configuration);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {

			e.printStackTrace();
		}
        
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
	
	private static <T extends Controller> Controller newController(Class<T> type, Configuration configuration) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		return type.getConstructor(Configuration.class).newInstance(configuration);
	}
	
	/**
	 * 
	 * @return
	 */
	
	private static List<IsoCountry> loadCountries() {
		HttpResponse httpResponse = RestResource.get(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")).getEnvironmentUrl())
				.path("iso-countries")
				.execute();
		
		List<IsoCountry> countries = Collections.emptyList();
		
		if (httpResponse.getStatusCode() == Status.OK) {
			IsoCountryList isoCountryList = httpResponse.getEntity(IsoCountryList.class);
			countries = isoCountryList.getItems();
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