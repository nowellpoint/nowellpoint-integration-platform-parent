package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.BadRequestException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.SalesforceConnector;
import com.nowellpoint.www.app.util.MessageProvider;

import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class SalesforceController extends AbstractController {
	
	private final static Logger LOG = LoggerFactory.getLogger(SalesforceController.class.getName());
	
	public SalesforceController(Configuration cfg) {
		super(SalesforceController.class, cfg);
	}
	
	public void configureRoutes(Configuration cfg) {
		get("/app/salesforce/oauth", (request, response) -> oauth(request, response));
        
        get("/app/salesforce/callback", (request, response) -> callback(request, response), new FreeMarkerEngine(cfg));
        
        get("/app/connectors/salesforce/detail", (request, response) -> getSalesforceConnectorDetails(request, response), new FreeMarkerEngine(cfg));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private String oauth(Request request, Response response) {
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
    			.header("x-api-key", API_KEY)
    			.path("salesforce")
    			.path("oauth")
    			.queryParameter("state", request.queryParams("id"))
    			.execute();
    	
    	LOG.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + httpResponse.getURL() + " : " + httpResponse.getHeaders().get("Location"));
		
		response.redirect(httpResponse.getHeaders().get("Location").get(0));		
		
		return "";
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private ModelAndView callback(Request request, Response response) {
    	
    	Optional<String> code = Optional.ofNullable(request.queryParams("code"));
    	
    	if (! code.isPresent()) {
    		throw new BadRequestException("missing OAuth code from Salesforce");
    	}
    	
		return new ModelAndView(getModel(), "secure/salesforce-callback.html");	
    }
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	
	private ModelAndView getSalesforceConnectorDetails(Request request, Response response) {
		Token token = getToken(request);
		
		Account account = getAccount(request);
    	
    	HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("x-api-key", API_KEY)
				.bearerAuthorization(token.getAccessToken())
    			.path("salesforce")
    			.path("connector")
    			.queryParameter("code", request.queryParams("code"))
    			.execute();
    	
    	SalesforceConnector salesforceConnector = null;
    	String successMessage = null;
    	String errorMessage = null;
    	
    	if (httpResponse.getStatusCode() == Status.OK) {
    		salesforceConnector = httpResponse.getEntity(SalesforceConnector.class);	
    		successMessage = MessageProvider.getMessage(Locale.US, "saveSuccess");
    	} else {
    		errorMessage = httpResponse.getAsString();
    	}	
    	
    	Map<String, Object> model = getModel();
    	model.put("account", account);
    	model.put("salesforceConnector", salesforceConnector);
    	model.put("successMessage", successMessage);
    	model.put("errorMessage", errorMessage);
    	
    	return new ModelAndView(model, "secure/salesforce-authenticate.html");
	}
}