package com.nowellpoint.www.app;

import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.halt;

import java.io.IOException;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.aws.model.data.IsoCountry;
import com.nowellpoint.www.app.client.NCSAuthClient;
import com.nowellpoint.www.app.view.ApplicationController;
import com.nowellpoint.www.app.view.AuthenticationController;
import com.nowellpoint.www.app.view.ContactController;
import com.nowellpoint.www.app.view.UserProfileController;
import com.nowellpoint.www.app.view.ProjectController;
import com.nowellpoint.www.app.view.SalesforceController;
import com.nowellpoint.www.app.view.SetupController;
import com.nowellpoint.www.app.view.SignUpController;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import spark.ModelAndView;
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
        // ObjectMapper
        //
        
        ObjectMapper objectMapper = new ObjectMapper();
        
        //
        // load countries list
        //
		
		try {
			HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.path("iso-country")
					.execute();
			
			String json = httpResponse.getEntity();
			
			List<IsoCountry> isoCountries = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, IsoCountry.class));
			
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
        // NowellpointService
        //
        
        NCSAuthClient nowellpointService = new NCSAuthClient();
        
        //
        //
        //
        
        before("/app/*", (request, response) -> {
        	LOGGER.info("authenticated request received: " + request.requestMethod() + " - "+ request.uri());
        	Optional<String> cookie = Optional.ofNullable(request.cookie("com.nowellpoint.auth.token"));
        	if (cookie.isPresent()) {
        		Token token = objectMapper.readValue(cookie.get(), Token.class);
        		Account account = nowellpointService.getAccount(token.getAccessToken());
        		request.attribute("token", token);
        		request.attribute("account", account);
        	} else {
        		response.redirect("/login");
        		halt();
        	}
        });
        
        //
        // configure routes
        //
        
        get("/", (request, response) -> {
        	Map<String,Object> model = new HashMap<String,Object>();
 			return new ModelAndView(model, "index.html");
		}, new FreeMarkerEngine(cfg));
        
        //
        // search
        //
        
        get("/services", (request, response) -> {
        	Map<String,Object> model = new HashMap<String,Object>();
			return new ModelAndView(model, "services.html");
		}, new FreeMarkerEngine(cfg));
        
        //
        //
        //
        
        get("/app/dashboard", (request, response) -> {
        	Map<String,Object> model = new HashMap<String,Object>();
        	model.put("account", request.attribute("account"));
			return new ModelAndView(model, "secure/dashboard.html");
		}, new FreeMarkerEngine(cfg));
                
        //
        //
        //
        
        get("/healthcheck", (request, response) -> {
        	response.status(200);
        	return null;
        });
        
        //
        // routes
        //
        
        new ApplicationController(cfg);
        new UserProfileController(cfg);
        new SignUpController(cfg);
        new ContactController(cfg);
        new AuthenticationController(cfg);
        new ProjectController(cfg);
        new SalesforceController(cfg);
        new SetupController(cfg);
        
        //
        // exception handler
        //
        
        exception(NotFoundException.class, (e, request, response) -> {
            response.status(404);
            response.body(e.getMessage());
        });
        
        exception(BadRequestException.class, (e, request, response) -> {
        	response.status(401);
            response.body(e.getMessage());
        });
        
        exception(InternalServerErrorException.class, (e, request, response) -> {
            response.status(500);
            response.body(e.getMessage());
        });
	}
}