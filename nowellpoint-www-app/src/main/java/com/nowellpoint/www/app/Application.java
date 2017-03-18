package com.nowellpoint.www.app;

import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
    	new Application();
    }
    
    public Application() {
    	init();
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
		// verify secure requests
		//
		
		before("/app/*", (request, response) -> AuthenticationController.verify(request, response));
		
		//
		// index routes
		//
		
		get(Path.Route.INDEX, (request, response) -> IndexController.serveIndexPage(configuration, request, response));
		post(Path.Route.CONTACT, (request, response) -> IndexController.contact(configuration, request, response));
		
		//
		// dashboard controller
		//
		
		get(Path.Route.DASHBOARD, (request, response) -> DashboardController.showDashboard(configuration, request, response));
		
		//
		// notifications controller
		//
		
		get(Path.Route.NOTIFICATIONS, (request, response) -> NotificationController.serveNotificationsPage(configuration, request, response));
		
		//
		// account profile routes
		//
		
		get(Path.Route.ACCOUNT_PROFILE_LIST_PLANS, (request, response) -> AccountProfileController.listPlans(configuration, request, response));
		get(Path.Route.ACCOUNT_PROFILE, (request, response) -> AccountProfileController.viewAccountProfile(configuration, request, response));
		get(Path.Route.ACCOUNT_PROFILE_PLAN, (request, response) -> AccountProfileController.reviewPlan(configuration, request, response));
		get(Path.Route.ACCOUNT_PROFILE_CURRENT_PLAN, (request, response) -> AccountProfileController.currentPlan(configuration, request, response));
        post(Path.Route.ACCOUNT_PROFILE_PLAN, (request, response) -> AccountProfileController.setPlan(configuration, request, response));
        post(Path.Route.ACCOUNT_PROFILE, (request, response) -> AccountProfileController.updateAccountProfile(configuration, request, response));
        get(Path.Route.ACCOUNT_PROFILE_DEACTIVATE, (request, response) -> AccountProfileController.confirmDeactivateAccountProfile(configuration, request, response));
        post(Path.Route.ACCOUNT_PROFILE_DEACTIVATE, (request, response) -> AccountProfileController.deactivateAccountProfile(configuration, request, response));
        delete(Path.Route.ACCOUNT_PROFILE_PICTURE, (request, response) -> AccountProfileController.removeProfilePicture(configuration, request, response));
        post(Path.Route.ACCOUNT_PROFILE_ADDRESS, (request, response) -> AccountProfileController.updateAccountProfileAddress(configuration, request, response));
        post(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS, (request, response) -> AccountProfileController.addCreditCard(configuration, request, response));  
        get(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/:token/view"), (request, response) -> AccountProfileController.getCreditCard(configuration, request, response));
        get(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/:token/edit"), (request, response) -> AccountProfileController.editCreditCard(configuration, request, response));
        post(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/:token"), (request, response) -> AccountProfileController.updateCreditCard(configuration, request, response));
        post(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/:token/primary"), (request, response) -> AccountProfileController.setPrimaryCreditCard(configuration, request, response));
        delete(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/:token"), (request, response) -> AccountProfileController.removeCreditCard(configuration, request, response));
		
		//
		// authentication routes
		//
		
		get(Path.Route.LOGIN, (request, response) -> AuthenticationController.serveLoginPage(configuration, request, response));
        post(Path.Route.LOGIN, (request, response) -> AuthenticationController.login(configuration, request, response));
        get(Path.Route.LOGOUT, (request, response) -> AuthenticationController.logout(configuration, request, response));
        
        //
        // start routes
        //
        
        get(Path.Route.START, (request, response) -> StartController.serveStartPage(configuration, request, response));
        
        //
        // administration routes
        //
        
        get(Path.Route.ADMINISTRATION, (request, response) -> AdministrationController.serveAdminHomePage(configuration, request, response));	
        get(Path.Route.ADMINISTRATION.concat("/cache"), (request, response) -> AdministrationController.showManageCache(configuration, request, response));	
		get(Path.Route.ADMINISTRATION.concat("/cache/purge"), (request, response) -> AdministrationController.purgeCache(configuration, request, response));
		
		//
		// salesforce oauth routes
		//
		
		get(Path.Route.SALESFORCE_OAUTH, (request, response) -> SalesforceOauthController.oauth(configuration, request, response));
        get(Path.Route.SALESFORCE_OAUTH.concat("/callback"), (request, response) -> SalesforceOauthController.callback(configuration, request, response));
        get(Path.Route.SALESFORCE_OAUTH.concat("/token"), (request, response) -> SalesforceOauthController.getSalesforceToken(configuration, request, response));
        
        //
        // signup routes
        //
        
        get(Path.Route.PLANS, (request, response) -> SignUpController.plans(configuration, request, response));
		get(Path.Route.FREE_ACCOUNT, (request, response) -> SignUpController.freeAccount(configuration, request, response));
		get(Path.Route.SIGN_UP, (request, response) -> SignUpController.paidAccount(configuration, request, response));
		post(Path.Route.SIGN_UP, (request, response) -> SignUpController.signUp(configuration, request, response));
		get(Path.Route.VERIFY_EMAIL, (request, response) -> SignUpController.verifyEmail(configuration, request, response));
        
		
		//
		// salesforce connector routes
		//
		
		get(Path.Route.CONNECTORS_SALESFORCE_LIST, (request, response) -> SalesforceConnectorController.listSalesforceConnectors(configuration, request, response));
        get(Path.Route.CONNECTORS_SALESFORCE_NEW, (request, response) -> SalesforceConnectorController.newSalesforceConnector(configuration, request, response));
        get(Path.Route.CONNECTORS_SALESFORCE_VIEW, (request, response) -> SalesforceConnectorController.viewSalesforceConnector(configuration, request, response));
        post(Path.Route.CONNECTORS_SALESFORCE_UPDATE, (request, response) -> SalesforceConnectorController.updateSalesforceConnector(configuration, request, response));
        delete(Path.Route.CONNECTORS_SALESFORCE_DELETE, (request, response) -> SalesforceConnectorController.deleteSalesforceConnector(configuration, request, response));
        get(Path.Route.CONNECTORS_SALESFORCE_EDIT, (request, response) -> SalesforceConnectorController.editSalesforceConnector(configuration, request, response));
        post(Path.Route.CONNECTORS_SALESFORCE_TEST, (request, response) -> SalesforceConnectorController.testSalesforceConnector(configuration, request, response));
        post(Path.Route.CONNECTORS_SALESFORCE_BUILD, (request, response) -> SalesforceConnectorController.buildSalesforceConnector(configuration, request, response));
        get(Path.Route.CONNECTORS_SALESFORCE_SOBJECT_LIST, (request, response) -> SalesforceConnectorController.listSObjects(configuration, request, response));
        get(Path.Route.CONNECTORS_SALESFORCE_SOBJECT_VIEW, (request, response) -> SalesforceConnectorController.viewSObject(configuration, request, response));
        get(Path.Route.CONNECTORS_SALESFORCE_SERVICE_LIST, (request, response) -> SalesforceConnectorController.listServices(configuration, request, response));
        post(Path.Route.CONNECTORS_SALESFORCE_SERVICE_ADD, (request, response) -> SalesforceConnectorController.addService(configuration, request, response));
        get(Path.Route.CONNECTORS_SALESFORCE_SERVICE_SETUP, (request, response) -> SalesforceConnectorController.setupService(configuration, request, response));
        
		//
		// health check route
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