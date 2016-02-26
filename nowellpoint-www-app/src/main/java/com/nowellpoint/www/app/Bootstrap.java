package com.nowellpoint.www.app;

import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.halt;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotAuthorizedException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.IsoCountry;
import com.nowellpoint.www.app.view.ApplicationConfigurationController;
import com.nowellpoint.www.app.view.ApplicationController;
import com.nowellpoint.www.app.view.AuthenticationController;
import com.nowellpoint.www.app.view.ContactController;
import com.nowellpoint.www.app.view.DashboardController;
import com.nowellpoint.www.app.view.UserProfileController;
import com.nowellpoint.www.app.view.ProjectController;
import com.nowellpoint.www.app.view.SalesforceController;
import com.nowellpoint.www.app.view.SetupController;
import com.nowellpoint.www.app.view.SignUpController;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.servlet.SparkApplication;
import spark.template.freemarker.FreeMarkerEngine;

public class Bootstrap implements SparkApplication {
	
	private static final Logger LOGGER = Logger.getLogger(Bootstrap.class.getName());

    public static void main(String[] args) throws Exception {
    	new Bootstrap().init();
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
        
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);

		//
		// set configuration options
		//

        cfg.setClassForTemplateLoading(this.getClass(), "/views");
		cfg.setDefaultEncoding("UTF-8");
		cfg.setLocale(Locale.US);
		
		//
		//
		//
		
		ObjectMapper objectMapper = new ObjectMapper();
        
        //
        // load countries list
        //
		
		try {
			List<IsoCountry> isoCountries = objectMapper.readValue(loadCountries(), objectMapper.getTypeFactory().constructCollectionType(List.class, IsoCountry.class));
			
			List<IsoCountry> filteredList = isoCountries.stream()
					.filter(country -> "US".equals(country.getLanguage()))
					.sorted((p1, p2) -> p1.getName().compareTo(p2.getName()))
					.collect(Collectors.toList());
			
			cfg.setSharedVariable("countryList", filteredList);
			
		} catch (IOException | TemplateModelException e) {
			e.printStackTrace();
			halt();
		}
        
        //
        // load messages for locale
        //
        
        messages.keySet().stream().forEach(message -> {
        	try {
				cfg.setSharedVariable(message, messages.getString(message));
			} catch (TemplateException e) {
				e.printStackTrace();
				halt();
			}
        });
        
        //
        //
        //
        
        before("/app/*", (request, response) -> verify(request, response));
        
        //
        // configure routes
        //
        
        get("/", (request, response) -> root(request, response), new FreeMarkerEngine(cfg));
        
        //
        // search
        //
        
        get("/services", (request, response) -> getServices(request, response), new FreeMarkerEngine(cfg));
                
        //
        //
        //
        
        get("/healthcheck", (request, response) -> {
        	response.status(200);
        	return "";
        });
        
        //
        // routes
        //
        
        new DashboardController(cfg);
        new ApplicationController(cfg);
        new UserProfileController(cfg);
        new SignUpController(cfg);
        new ContactController(cfg);
        new AuthenticationController(cfg);
        new ProjectController(cfg);
        new SalesforceController(cfg);
        new SetupController(cfg);
        new ApplicationConfigurationController(cfg);
        
        //
        // exception handlers
        //
        
        exception(NotAuthorizedException.class, (exception, request, response) -> {
        	
        	Map<String, Object> model = new HashMap<String, Object>();
        	model.put("errorMessage", exception.getMessage());
        	
        	Template template;
			try {
				template = cfg.getTemplate("login.html");
				Writer output = new StringWriter();
				template.process(model, output);
				response.body(output.toString());
				output.flush();
			} catch (Exception e) {
				e.printStackTrace();
				halt();
			}  	
        });
        
        exception(NotFoundException.class, (exception, request, response) -> {
            response.status(404);
            response.body(exception.getMessage());
        });
        
        exception(BadRequestException.class, (exception, request, response) -> {
        	response.status(401);
            response.body(exception.getMessage());
        });
        
        exception(InternalServerErrorException.class, (exception, request, response) -> {
            response.status(500);
            response.body(exception.getMessage());
        });
	}
	
	/**
	 * 
	 * @return
	 */
	
	private static String loadCountries() {
		try {
			HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.path("iso-country")
					.execute();
			
			String json = httpResponse.getEntity();
			
			return json;
			
		} catch (IOException e) {
			e.printStackTrace();
			halt();
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView root(Request request, Response response) {
    	Map<String,Object> model = new HashMap<String,Object>();
    	return new ModelAndView(model, "index.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private static ModelAndView getServices(Request request, Response response) {
    	Map<String,Object> model = new HashMap<String,Object>();
		return new ModelAndView(model, "services.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	
	private static void verify(Request request, Response response) throws JsonParseException, JsonMappingException, IOException {
		
		LOGGER.info("authenticated request received: " + request.requestMethod() + " - "+ request.uri());
		
		ObjectMapper objectMapper = new ObjectMapper();
		
    	Optional<String> cookie = Optional.ofNullable(request.cookie("com.nowellpoint.auth.token"));
    	if (cookie.isPresent()) {
    		Token token = objectMapper.readValue(cookie.get(), Token.class);
    		Account account = objectMapper.readValue(getAccount(token.getAccessToken()), Account.class);
    		request.attribute("token", token);
    		request.attribute("account", account);
    	} else {
    		response.redirect("/login");
    		halt();
    	}
	}
	
	/**
	 * 
	 * @param accessToken
	 * @return
	 */
	
	private static String getAccount(String accessToken) {
		
		try {
			HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
					.accept(MediaType.APPLICATION_JSON)
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(accessToken)
					.path("account")
					.execute();
			
			int statusCode = httpResponse.getStatusCode();
	    	
	    	LOGGER.info("Status Code: " + statusCode + " Method: GET : " + httpResponse.getURL());
	    	
	    	if (statusCode != 200) {
	    		throw new BadRequestException(httpResponse.getEntity());
	    	}
	    	
	    	return httpResponse.getEntity();
	    	 	    	
		} catch (IOException e) {
			throw new InternalServerErrorException(e);
		}
	}
}