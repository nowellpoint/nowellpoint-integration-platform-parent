package com.nowellpoint.www.app;

import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
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
        
        //
        //
        //
        
        AuthenticationController authenticationController = new AuthenticationController(cfg);
        AccountProfileController accountProfileController = new AccountProfileController(cfg);
        VerifyEmailController verifyEmailController = new VerifyEmailController(cfg);
        DashboardController dashboardController = new DashboardController(cfg);
        AdministrationController administrationController = new AdministrationController(cfg);
        SignUpController signUpController = new SignUpController(cfg);
        NotificationController notificationController = new NotificationController(cfg);
        //SetupController setupController = new SetupController(cfg);
        SalesforceOauthController salesforceOauthController = new SalesforceOauthController(cfg);
        ContactUsController contactUsController = new ContactUsController(cfg);
        //ApplicationController applicationController = new ApplicationController(cfg);
        //ProjectController projectController = new ProjectController(cfg);
        SalesforceConnectorController salesforceConnectorController = new SalesforceConnectorController(cfg);
        ScheduledJobController scheduledJobsController = new ScheduledJobController(cfg);
        
        //
        // setup routes
        //
        
        before("/app/*", AuthenticationController.verify);

        get(Path.Route.INDEX, (request, response) -> getContextRoot(request, response), new FreeMarkerEngine(cfg));
        
        get(Path.Route.SERVICES, (request, response) -> getServices(request, response), new FreeMarkerEngine(cfg));
        
        get(Path.Route.HEALTH_CHECK, (request, response) -> healthCheck(request, response));
        
        get(Path.Route.LOGIN, authenticationController.showLoginPage);
        post(Path.Route.LOGIN, AuthenticationController.login);
        get(Path.Route.LOGOUT, AuthenticationController.logout);
        
        get(Path.Route.CONTACT_US, contactUsController.showContactUs);
		post(Path.Route.CONTACT_US, contactUsController.contactUs);
        
        get(Path.Route.SIGN_UP, signUpController.showSignUp);
		post(Path.Route.SIGN_UP, signUpController.signUp);
        
        get(Path.Route.VERIFY_EMAIL, verifyEmailController.verifyEmail);
        
        get(Path.Route.START, dashboardController.showStartPage);
        get(Path.Route.DASHBOARD, dashboardController.showDashboard);
        
        get(Path.Route.NOTIFICATIONS, notificationController.showNotifications);
        
       // get(Path.Route.SETUP, setupController.showSetup);
        
//        get(Path.Route.APPLICATION_CONNECTOR_SELECT, applicationController.selectSalesforceConnector);
//        get(Path.Route.APPLICATION_EDIT, applicationController.editApplication);
//        get(Path.Route.APPLICATION_NEW, applicationController.newApplication);
//		get(Path.Route.APPLICATION_VIEW, applicationController.viewApplication);
//		get(Path.Route.APPLICATION_LIST, applicationController.getApplications);
//		delete(Path.Route.APPLICATION_DELETE, applicationController.deleteApplication);
//		post(Path.Route.APPLICATION_CREATE, applicationController.createApplication);
//		post(Path.Route.APPLICATION_UPDATE, applicationController.updateApplication);
//		post(Path.Route.APPLICATION_ENVIRONMENT_TEST, applicationController.testConnection);   
//		get(Path.Route.APPLICATION_ENVIRONMENT_VIEW, applicationController.viewEnvironment);
//		get(Path.Route.APPLICATION_ENVIRONMENT_EDIT, applicationController.editEnvironment);
//		get(Path.Route.APPLICATION_ENVIRONMENT_NEW, applicationController.newEnvironment);
//		post(Path.Route.APPLICATION_ENVIRONMENT_ADD, applicationController.addEnvironment);
//		post(Path.Route.APPLICATION_ENVIRONMENT_UPDATE, applicationController.updateEnvironment);
//		delete(Path.Route.APPLICATION_ENVIRONMENT_REMOVE, applicationController.removeEnvironment);
//		get(Path.Route.APPLICATION_SERVICE_VIEW, applicationController.viewServiceInstance);
//		get(Path.Route.APPLICATION_SERVICE_EDIT, applicationController.editServiceInstance);
//		get(Path.Route.APPLICATION_SERVICE_NEW, applicationController.newServiceInstance);  
//		post(Path.Route.APPLICATION_SERVICE_ADD, applicationController.addServiceInstance);
//		post(Path.Route.APPLICATION_SERVICE_UPDATE, applicationController.updateServiceInstance);  
		
//		get(Path.Route.PROJECTS, projectController.getProjects);
//		get(Path.Route.PROJECTS.concat("/:id"), projectController.getProject);
//		post(Path.Route.PROJECTS, projectController.saveProject);
//		delete(Path.Route.PROJECTS.concat("/:id"), projectController.deleteProject);
        
        get(Path.Route.SALESFORCE_OAUTH, salesforceOauthController.oauth);
        get(Path.Route.SALESFORCE_OAUTH.concat("/callback"), salesforceOauthController.callback);
        get(Path.Route.SALESFORCE_OAUTH.concat("/token"), salesforceOauthController.getSalesforceToken);
        
        get(Path.Route.ADMINISTRATION, administrationController.showAdministrationHome);	
        get(Path.Route.ADMINISTRATION.concat("/cache"), administrationController.showManageCache);	
        get(Path.Route.ADMINISTRATION.concat("/properties"), administrationController.showManageProperties);	
		get(Path.Route.ADMINISTRATION.concat("/cache/purge"), administrationController.purgeCache);
        
        get(Path.Route.ACCOUNT_PROFILE, accountProfileController.getAccountProfile);
        get(Path.Route.ACCOUNT_PROFILE_LIST_PLANS, accountProfileController.listPlans);
        get(Path.Route.ACCOUNT_PROFILE_PLAN, accountProfileController.reviewPlan);
        post(Path.Route.ACCOUNT_PROFILE_PLAN, accountProfileController.setPlan);
        post(Path.Route.ACCOUNT_PROFILE, accountProfileController.updateAccountProfile);
        get(Path.Route.ACCOUNT_PROFILE.concat("/edit"), accountProfileController.editAccountProfile);
        get(Path.Route.ACCOUNT_PROFILE_DEACTIVATE, accountProfileController.confirmDeactivateAccountProfile);
        post(Path.Route.ACCOUNT_PROFILE_DEACTIVATE, accountProfileController.deactivateAccountProfile);
        delete(Path.Route.ACCOUNT_PROFILE.concat("/picture"), accountProfileController.removeProfilePicture);
        get(Path.Route.ACCOUNT_PROFILE_ADDRESS, accountProfileController.editAddress);
        post(Path.Route.ACCOUNT_PROFILE_ADDRESS, accountProfileController.updateAccountProfileAddress);
        get(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/:token/view"), accountProfileController.getCreditCard);
        get(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/new"), accountProfileController.newCreditCard);
        get(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/:token/edit"), accountProfileController.editCreditCard);
        post(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS, accountProfileController.addCreditCard);
        post(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/:token"), accountProfileController.updateCreditCard);
        post(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/:token/primary"), accountProfileController.setPrimaryCreditCard);
        delete(Path.Route.ACCOUNT_PROFILE_PAYMENT_METHODS.concat("/:token"), accountProfileController.removeCreditCard);
        
        get(Path.Route.CONNECTORS_SALESFORCE_LIST, salesforceConnectorController.getSalesforceConnectors);
        get(Path.Route.CONNECTORS_SALESFORCE_NEW, salesforceConnectorController.newSalesforceConnector);
        get(Path.Route.CONNECTORS_SALESFORCE_VIEW, salesforceConnectorController.viewSalesforceConnector);
        post(Path.Route.CONNECTORS_SALESFORCE_UPDATE, salesforceConnectorController.updateSalesforceConnector);
        delete(Path.Route.CONNECTORS_SALESFORCE_DELETE, salesforceConnectorController.deleteSalesforceConnector);
        get(Path.Route.CONNECTORS_SALESFORCE_EDIT, salesforceConnectorController.editSalesforceConnector);
        get(Path.Route.CONNECTORS_SALESFORCE_ENVIRONMENT_NEW, salesforceConnectorController.newEnvironment);
        get(Path.Route.CONNECTORS_SALESFORCE_ENVIRONMENT_VIEW, salesforceConnectorController.viewEnvironment);
        get(Path.Route.CONNECTORS_SALESFORCE_ENVIRONMENT_EDIT, salesforceConnectorController.editEnvironment);
        post(Path.Route.CONNECTORS_SALESFORCE_ENVIRONMENT_ADD, salesforceConnectorController.addEnvironment);
        post(Path.Route.CONNECTORS_SALESFORCE_ENVIRONMENT_UPDATE, salesforceConnectorController.updateEnvironment);
        delete(Path.Route.CONNECTORS_SALESFORCE_ENVIRONMENT_REMOVE, salesforceConnectorController.removeEnvironment);
        post(Path.Route.CONNECTORS_SALESFORCE_ENVIRONMENT_TEST, salesforceConnectorController.testConnection);  
        get(Path.Route.CONNECTORS_SALESFORCE_ENVIRONMENT_SOBJECTS, salesforceConnectorController.sobjectsList);
        
        get(Path.Route.SCHEDULED_JOBS_LIST, scheduledJobsController.getScheduledJobs);
        get(Path.Route.SCHEDULED_JOB_SELECT_TYPE, scheduledJobsController.selectType);
        get(Path.Route.SCHEDULED_JOB_SELECT_CONNECTOR, scheduledJobsController.selectConnector);
        get(Path.Route.SCHEDULED_JOB_SELECT_ENVIRONMENT, scheduledJobsController.selectEnvironment);
        get(Path.Route.SCHEDULED_JOB_SET_SCHEDULE, scheduledJobsController.setSchedule);
        post(Path.Route.SCHEDULED_JOB_CREATE, scheduledJobsController.createScheduledJob);
        get(Path.Route.SCHEDULED_JOB_VIEW, scheduledJobsController.viewScheduledJob);
        get(Path.Route.SCHEDULED_JOB_EDIT, scheduledJobsController.editScheduledJob);
        post(Path.Route.SCHEDULED_JOB_UPDATE, scheduledJobsController.updateScheduledJob);
        post(Path.Route.SCHEDULED_JOB_START, scheduledJobsController.startScheduledJob);
        post(Path.Route.SCHEDULED_JOB_STOP, scheduledJobsController.stopScheduledJob);
        get(Path.Route.SCHEDULED_JOB_RUN_HISTORY, scheduledJobsController.getRunHistory);
        get(Path.Route.SCHEDULED_JOB_DOWNLOAD_FILE, scheduledJobsController.downloadFile);
        
        //
        // exception handlers
        //
        
        exception(NotAuthorizedException.class, authenticationController.handleNotAuthorizedException);
        
        exception(NowellpointServiceException.class, (exception, request, response) ->{
        	response.status(((NowellpointServiceException) exception).getStatusCode());
        	response.body(exception.getMessage());
        });
        
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