/**
 * 
 * Copyright 2015-2016 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

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
import java.util.ResourceBundle;
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
import com.nowellpoint.www.app.view.UserProfileController;
import com.nowellpoint.www.app.view.AdministrationController;
import com.nowellpoint.www.app.view.AuthenticationController;
import com.nowellpoint.www.app.view.DashboardController;
import com.nowellpoint.www.app.view.IndexController;
import com.nowellpoint.www.app.view.JobController;
import com.nowellpoint.www.app.view.NotificationController;
import com.nowellpoint.www.app.view.OrganizationController;
import com.nowellpoint.www.app.view.SalesforceConnectorController;
import com.nowellpoint.www.app.view.SalesforceOauthController;
import com.nowellpoint.www.app.view.SignUpController;
import com.nowellpoint.www.app.view.StartController;

import freemarker.ext.beans.ResourceBundleModel;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.TemplateModelException;
import spark.Request;
import spark.Response;
import spark.servlet.SparkApplication;

public class Application implements SparkApplication {

	private static final Logger LOG = Logger.getLogger(Application.class.getName());

	public static void main(String[] args) throws Exception {
		new Application();
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

		before("/*", (request, response) -> {
			request.attribute("com.nowellpoint.default.locale", configuration.getLocale());
			request.attribute("com.nowellpoint.default.timezone", configuration.getTimeZone());
		});

		//
		// verify secure requests
		//

		before("/app/*", (request, response) -> AuthenticationController.verify(configuration, request, response));

		//
		// index routes
		//

		get(Path.Route.INDEX, (request, response) -> IndexController.serveIndexPage(configuration, request, response));
		post(Path.Route.CONTACT, (request, response) -> IndexController.contact(configuration, request, response));

		//
		// dashboard controller
		//

		get(Path.Route.DASHBOARD,
				(request, response) -> DashboardController.showDashboard(configuration, request, response));

		//
		// notifications controller
		//

		get(Path.Route.NOTIFICATIONS,
				(request, response) -> NotificationController.serveNotificationsPage(configuration, request, response));

		//
		// user profile routes
		//

		get(Path.Route.USER_PROFILE,
				(request, response) -> UserProfileController.viewUserProfile(configuration, request, response));
		get(Path.Route.USER_PROFILE_INVOICE_DOWNLOAD,
				(request, response) -> UserProfileController.downloadInvoice(configuration, request, response));
		post(Path.Route.USER_PROFILE,
				(request, response) -> UserProfileController.updateUserProfile(configuration, request, response));
		get(Path.Route.USER_PROFILE_DEACTIVATE, (request, response) -> UserProfileController
				.confirmDeactivateAccountProfile(configuration, request, response));
		post(Path.Route.USER_PROFILE_DEACTIVATE, (request, response) -> UserProfileController
				.deactivateAccountProfile(configuration, request, response));
		delete(Path.Route.USER_PROFILE_PICTURE,
				(request, response) -> UserProfileController.removeProfilePicture(configuration, request, response));
		post(Path.Route.USER_PROFILE_ADDRESS,
				(request, response) -> UserProfileController.updateAddress(configuration, request, response));

		//
		// organization routes
		//

		get(Path.Route.ORGANIZATION_VIEW,
				(request, response) -> OrganizationController.viewOrganization(configuration, request, response));
		get(Path.Route.ORGANIZATION_LIST_PLANS,
				(request, response) -> OrganizationController.listPlans(configuration, request, response));
		get(Path.Route.ORGANIZATION_REVIEW_PLAN,
				(request, response) -> OrganizationController.reviewPlan(configuration, request, response));
		post(Path.Route.ORGANIZATION_CHANGE_PLAN,
				(request, response) -> OrganizationController.changePlan(configuration, request, response));

		//
		// authentication routes
		//

		get(Path.Route.LOGIN,
				(request, response) -> AuthenticationController.serveLoginPage(configuration, request, response));
		post(Path.Route.LOGIN, (request, response) -> AuthenticationController.login(configuration, request, response));
		get(Path.Route.LOGOUT,
				(request, response) -> AuthenticationController.logout(configuration, request, response));

		//
		// start routes
		//

		get(Path.Route.START, (request, response) -> StartController.serveStartPage(configuration, request, response));

		//
		// administration routes
		//

		get(Path.Route.ADMINISTRATION,
				(request, response) -> AdministrationController.serveAdminHomePage(configuration, request, response));
		get(Path.Route.ADMINISTRATION.concat("/cache"),
				(request, response) -> AdministrationController.showManageCache(configuration, request, response));
		get(Path.Route.ADMINISTRATION.concat("/cache/purge"),
				(request, response) -> AdministrationController.purgeCache(configuration, request, response));

		//
		// salesforce oauth routes
		//

		get(Path.Route.SALESFORCE_OAUTH,
				(request, response) -> SalesforceOauthController.oauth(configuration, request, response));
		get(Path.Route.SALESFORCE_OAUTH.concat("/callback"),
				(request, response) -> SalesforceOauthController.callback(configuration, request, response));
		get(Path.Route.SALESFORCE_OAUTH.concat("/token"),
				(request, response) -> SalesforceOauthController.getSalesforceToken(configuration, request, response));

		//
		// signup routes
		//

		get(Path.Route.PLANS, (request, response) -> SignUpController.plans(configuration, request, response));
		get(Path.Route.FREE_ACCOUNT,
				(request, response) -> SignUpController.freeAccount(configuration, request, response));
		get(Path.Route.SIGN_UP, (request, response) -> SignUpController.paidAccount(configuration, request, response));
		post(Path.Route.SIGN_UP, (request, response) -> SignUpController.signUp(configuration, request, response));
		get(Path.Route.VERIFY_EMAIL,
				(request, response) -> SignUpController.verifyEmail(configuration, request, response));
		post(Path.Route.PROVISION, (request, response) -> SignUpController.provision(configuration, request, response));

		//
		// salesforce connector routes
		//

		get(Path.Route.CONNECTORS_SALESFORCE_LIST, (request, response) -> SalesforceConnectorController
				.listSalesforceConnectors(configuration, request, response));
		get(Path.Route.CONNECTORS_SALESFORCE_NEW, (request, response) -> SalesforceConnectorController
				.newSalesforceConnector(configuration, request, response));
		get(Path.Route.CONNECTORS_SALESFORCE_VIEW, (request, response) -> SalesforceConnectorController
				.viewSalesforceConnector(configuration, request, response));
		get(Path.Route.CONNECTORS_SALESFORCE_FLOW_NEW,
				(request, response) -> SalesforceConnectorController.newFlow(configuration, request, response));
		post(Path.Route.CONNECTORS_SALESFORCE_UPDATE, (request, response) -> SalesforceConnectorController
				.updateSalesforceConnector(configuration, request, response));
		delete(Path.Route.CONNECTORS_SALESFORCE_DELETE, (request, response) -> SalesforceConnectorController
				.deleteSalesforceConnector(configuration, request, response));
		get(Path.Route.CONNECTORS_SALESFORCE_EDIT, (request, response) -> SalesforceConnectorController
				.editSalesforceConnector(configuration, request, response));
		post(Path.Route.CONNECTORS_SALESFORCE_TEST, (request, response) -> SalesforceConnectorController
				.testSalesforceConnector(configuration, request, response));
		post(Path.Route.CONNECTORS_SALESFORCE_BUILD, (request, response) -> SalesforceConnectorController
				.buildSalesforceConnector(configuration, request, response));
		post(Path.Route.CONNECTORS_SALESFORCE_METADATA_BACKUP,
				(request, response) -> SalesforceConnectorController.metadataBackup(configuration, request, response));
		get(Path.Route.CONNECTORS_SALESFORCE_SOBJECT_LIST,
				(request, response) -> SalesforceConnectorController.listSObjects(configuration, request, response));
		get(Path.Route.CONNECTORS_SALESFORCE_SOBJECT_VIEW,
				(request, response) -> SalesforceConnectorController.viewSObject(configuration, request, response));

		//
		// jobs routes
		//

		get(Path.Route.JOBS_LIST, (request, response) -> JobController.listJobs(configuration, request, response));
		get(Path.Route.JOBS_VIEW, (request, response) -> JobController.viewJob(configuration, request, response));
		post(Path.Route.JOBS_UPDATE, (request, response) -> JobController.updateJob(configuration, request, response));
		get(Path.Route.JOBS_OUTPUTS,
				(request, response) -> JobController.viewOutputs(configuration, request, response));
		get(Path.Route.JOBS_OUTPUTS_DOWNLOAD,
				(request, response) -> JobController.downloadOutputFile(configuration, request, response));
		post(Path.Route.JOBS_SUBMIT, (request, response) -> JobController.submitJob(configuration, request, response));
		post(Path.Route.JOBS_RUN, (request, response) -> JobController.runJob(configuration, request, response));
		post(Path.Route.JOBS_STOP, (request, response) -> JobController.stopJob(configuration, request, response));
		post(Path.Route.JOBS_TERMINATE,
				(request, response) -> JobController.terminateJob(configuration, request, response));
		post(Path.Route.JOBS_WEBHOOK_URL_TEST,
				(request, response) -> JobController.testWebhookUrl(configuration, request, response));

		post("/app/jobs/:connectorId/metadata-backup",
				(request, response) -> JobController.createJob(configuration, request, response));

		//
		// health check route
		//

		get(Path.Route.HEALTH_CHECK, (request, response) -> healthCheck(request, response));

		//
		// exception handlers
		//

		exception(ServiceUnavailableException.class, (exception, request, response) -> {
			LOG.error(InternalServerErrorException.class.getName(), exception);

			Map<String, Object> model = new HashMap<>();
			model.put("errorMessage", exception.getMessage());
			model.put("messages", new ResourceBundleModel(ResourceBundle.getBundle("messages", Locale.getDefault()), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));

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

		exception(BadRequestException.class, (exception, request, response) -> {
			LOG.error(BadRequestException.class.getName(), exception);
			response.status(400);
			response.body(exception.getMessage());
		});

		exception(NotFoundException.class, (exception, request, response) -> {
			LOG.error(NotFoundException.class.getName(), exception);
			response.status(404);
			response.body(exception.getMessage());
		});

		exception(InternalServerErrorException.class, (exception, request, response) -> {
			LOG.error(InternalServerErrorException.class.getName(), exception);

			Map<String, Object> model = new HashMap<>();
			model.put("errorMessage", exception.getMessage());
			model.put("messages", new ResourceBundleModel(ResourceBundle.getBundle("messages", Locale.getDefault()), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build()));

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
			LOG.error(Exception.class.getName(), exception);
			response.status(500);
			response.body(generateExceptionPage(configuration, exception.getMessage()));
		});
	}

	private static String generateExceptionPage(Configuration configuration, String errorMessage) {
		Map<String, Object> model = new HashMap<>();
		model.put("errorMessage", errorMessage);

		String page = null;

		Writer output = new StringWriter();
		try {
			Template template = configuration.getTemplate("error.html");
			freemarker.core.Environment environment = template.createProcessingEnvironment(model, output);
			environment.process();
			page = output.toString();
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
			halt();
		}

		return page;
	}

	/**
	 * 
	 * @return
	 */

	private static List<IsoCountry> loadCountries(Locale locale) {
		HttpResponse httpResponse = RestResource
				.get(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")).getEnvironmentUrl())
				.path("iso-countries").execute();

		List<IsoCountry> countries = Collections.emptyList();

		if (httpResponse.getStatusCode() == Status.OK) {

			IsoCountryList isoCountryList = httpResponse.getEntity(IsoCountryList.class);

			countries = isoCountryList.getItems().stream().sorted((p1, p2) -> p1.getName().compareTo(p2.getName()))
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