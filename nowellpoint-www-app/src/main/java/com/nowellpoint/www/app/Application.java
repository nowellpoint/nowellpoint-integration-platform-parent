package com.nowellpoint.www.app;

import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;

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
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.http.HttpRequestException;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.AccountProfile;
import com.nowellpoint.www.app.model.IsoCountry;
import com.nowellpoint.www.app.view.AccountProfileController;
import com.nowellpoint.www.app.view.AdministrationController;
import com.nowellpoint.www.app.view.ApplicationController;
import com.nowellpoint.www.app.view.AuthenticationController;
import com.nowellpoint.www.app.view.ContactController;
import com.nowellpoint.www.app.view.DashboardController;
import com.nowellpoint.www.app.view.NotificationController;
import com.nowellpoint.www.app.view.Path;
import com.nowellpoint.www.app.view.ProjectController;
import com.nowellpoint.www.app.view.SalesforceConnectorController;
import com.nowellpoint.www.app.view.SalesforceOauthController;
import com.nowellpoint.www.app.view.ServiceProviderController;
import com.nowellpoint.www.app.view.SetupController;
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
	
	private static final Logger LOGGER = Logger.getLogger(Application.class.getName());
	
	private static final ObjectMapper objectMapper = new ObjectMapper();

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
        
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);

		//
		// set configuration options
		//

        cfg.setClassForTemplateLoading(this.getClass(), "/views");
		cfg.setDefaultEncoding("UTF-8");
		cfg.setLocale(Locale.US);
        
        //
        // load countries list
        //
		
		try {			
			List<IsoCountry> isoCountries = loadCountries();
			
			List<IsoCountry> filteredList = isoCountries.stream()
					.filter(country -> "US".equals(country.getLanguage()))
					.sorted((p1, p2) -> p1.getName().compareTo(p2.getName()))
					.collect(Collectors.toList());
			
			cfg.setSharedVariable("countryList", filteredList);
			
		} catch (TemplateModelException e) {
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
        
        before("/", (request, response) -> {
        	
        });
        
        //
        //
        //
        
        before("/app/*", (request, response) -> verify(request, response));
        
        //
        // configure routes
        //
        
        get("/", (request, response) -> getContextRoot(request, response), new FreeMarkerEngine(cfg));
        
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
        
        AuthenticationController authenticationController = new AuthenticationController(cfg);
        AccountProfileController accountProfileController = new AccountProfileController(cfg);
        VerifyEmailController verifyEmailController = new VerifyEmailController(cfg);
        DashboardController dashboardController = new DashboardController(cfg);
        
        // setup routes
        
        get(Path.Route.LOGIN, authenticationController.showLoginPage);
        post(Path.Route.LOGIN, authenticationController.login);
        get(Path.Route.LOGOUT, authenticationController.logout);
        
        get(Path.Route.VERIFY_EMAIL, verifyEmailController.verifyEmail);
        
        get(Path.Route.START, dashboardController.showStartPage);
        get(Path.Route.DASHBOARD, dashboardController.showDashboard);
        
        get(Path.Route.ACCOUNT_PROFILE, accountProfileController.getAccountProfile);
        post(Path.Route.ACCOUNT_PROFILE, accountProfileController.updateAccountProfile);
        get(Path.Route.ACCOUNT_PROFILE.concat("/edit"), accountProfileController.editAccountProfile);
        get(Path.Route.ACCOUNT_PROFILE.concat("/disable"), accountProfileController.disableAccountProfile);
        delete(Path.Route.ACCOUNT_PROFILE.concat("/picture"), accountProfileController.removeProfilePicture);
        get(Path.Route.ACCOUNT_PROFILE_ADDRESS, accountProfileController.editAccountProfileAddress);
        post(Path.Route.ACCOUNT_PROFILE_ADDRESS, accountProfileController.updateAccountProfileAddress);
        get(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/:token/view"), accountProfileController.getCreditCard);
        get(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/new"), accountProfileController.newCreditCard);
        get(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/:token/edit"), accountProfileController.editCreditCard);
        post(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS, accountProfileController.addCreditCard);
        post(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/:token"), accountProfileController.updateCreditCard);
        post(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/:token/primary"), accountProfileController.setPrimaryCreditCard);
        delete(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/:token"), accountProfileController.removeCreditCard);
        
        //
        // routes
        //
        
        new AdministrationController(cfg);
        new ServiceProviderController(cfg);
        new ApplicationController(cfg);
        new SignUpController(cfg);
        new ContactController(cfg);
        new ProjectController(cfg);
        new SalesforceOauthController(cfg);
        new SalesforceConnectorController(cfg);
        new SetupController(cfg);
        new NotificationController(cfg);
        
        //
        // exception handlers
        //
        
        exception(NotAuthorizedException.class, authenticationController.handleNotAuthorizedException);
        
        exception(BadRequestException.class, (exception, request, response) -> {
            response.status(400);
            response.body(exception.getMessage());
        });
        
        exception(NotFoundException.class, (exception, request, response) -> {
            response.status(404);
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
	
	private static List<IsoCountry> loadCountries() {
		try {
			HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.path("iso-countries")
					.execute();
			
			return httpResponse.getEntityList(IsoCountry.class);
			
		} catch (HttpRequestException e) {
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
	
	private static ModelAndView getContextRoot(Request request, Response response) {
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
		
    	Optional<String> cookie = Optional.ofNullable(request.cookie("com.nowellpoint.auth.token"));
    	if (cookie.isPresent()) {
    		Token token = objectMapper.readValue(cookie.get(), Token.class);
    		AccountProfile account = getAccount(token.getAccessToken());
    		request.attribute("token", token);
    		request.attribute("account", account);
    	} else {
    		response.cookie("/", "redirectUrl", request.pathInfo(), 72000, Boolean.TRUE);
    		response.redirect("/login");
    		halt();
    	}
	}
	
	/**
	 * 
	 * @param accessToken
	 * @return
	 */
	
	private static AccountProfile getAccount(String accessToken) {
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.accept(MediaType.APPLICATION_JSON)
				.bearerAuthorization(accessToken)
				.path("account-profile")
				.path("me")
				.execute();
		
		int statusCode = httpResponse.getStatusCode();
    	
    	LOGGER.info("Status Code: " + statusCode + " Method: GET : " + httpResponse.getURL());
    	
    	if (statusCode != 200) {
    		throw new BadRequestException(httpResponse.getAsString());
    	}
    	
    	return httpResponse.getEntity(AccountProfile.class);
	}
}