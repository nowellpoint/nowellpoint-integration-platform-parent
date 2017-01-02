package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.util.Map;
import java.util.Optional;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.model.ExceptionResponse;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class SalesforceOauthController extends AbstractController {
	
	public static final class Template {
		public static final String SALESFORCE_OAUTH = String.format(APPLICATION_CONTEXT, "salesforce-callback.html");
	}
	
	public SalesforceOauthController() {
		super(SalesforceOauthController.class);
	}
	
	@Override
	public void configureRoutes(Configuration configuration) {
		get(Path.Route.SALESFORCE_OAUTH, (request, response) -> oauth(configuration, request, response));
        get(Path.Route.SALESFORCE_OAUTH.concat("/callback"), (request, response) -> callback(configuration, request, response));
        get(Path.Route.SALESFORCE_OAUTH.concat("/token"), (request, response) -> getSalesforceToken(configuration, request, response));
	}
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * oauth
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private String oauth(Configuration configuration, Request request, Response response) {
		
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
    			.path("salesforce")
    			.path("oauth")
    			.execute();
		
		response.redirect(httpResponse.getHeaders().get("Location"));		
		
		return "";
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * callback
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private String callback(Configuration configuration, Request request, Response response) {
    	
    	Optional<String> code = Optional.ofNullable(request.queryParams("code"));
    	
    	if (! code.isPresent()) {
    		throw new BadRequestException("missing OAuth code from Salesforce");
    	}
    	
    	Map<String, Object> model = getModel();
    	
    	return render(configuration, request, response, model, Template.SALESFORCE_OAUTH);
    };
	
    /**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * getSalesforceToken
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
    private String getSalesforceToken(Configuration configuration, Request request, Response response) {
    	
    	HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
				.bearerAuthorization(getToken(request).getAccessToken())
    			.path("salesforce")
    			.path("oauth")
    			.path("token")
    			.queryParameter("code", request.queryParams("code"))
    			.execute();
    	
    	Token token = null;
    	
    	if (httpResponse.getStatusCode() != Status.OK) {
    		ExceptionResponse error = httpResponse.getEntity(ExceptionResponse.class);
			throw new BadRequestException(error.getMessage());
    	}
    	
    	token = httpResponse.getEntity(Token.class);
    	
    	httpResponse = RestResource.post(API_ENDPOINT)
				.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
				.bearerAuthorization(getToken(request).getAccessToken())
				.path("connectors")
    			.path("salesforce")
    			.parameter("id", token.getId())
    			.parameter("instanceUrl", token.getInstanceUrl())
    			.parameter("accessToken", token.getAccessToken())
    			.parameter("refreshToken", token.getRefreshToken())
    			.execute();
    	
    	if (httpResponse.getStatusCode() != Status.CREATED) {
    		ExceptionResponse error = httpResponse.getEntity(ExceptionResponse.class);
			throw new BadRequestException(error.getMessage());
    	}
    	
    	SalesforceConnector salesforceConnector = httpResponse.getEntity(SalesforceConnector.class);
    	
    	response.redirect(String.format("/app/connectors/salesforce/%s", salesforceConnector.getId()));
    	
    	return "";
	};
}