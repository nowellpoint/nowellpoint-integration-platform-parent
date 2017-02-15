package com.nowellpoint.www.app.view;

import static spark.Spark.get;

import java.util.Optional;

import javax.ws.rs.BadRequestException;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.CreateSalesforceConnectorRequest;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.sforce.OauthToken;
import com.nowellpoint.www.app.util.Path;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class SalesforceOauthController extends AbstractStaticController {
	
	public static final class Template {
		public static final String SALESFORCE_OAUTH = String.format(APPLICATION_CONTEXT, "salesforce-oauth-callback.html");
	}
	
	public static void configureRoutes(Configuration configuration) {
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
	
	private static String oauth(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String oauthRedirect = new NowellpointClient(token)
				.salesforce()
				.getOauthRedirect();
		
		response.redirect(oauthRedirect);
		
		return "";
	};
	
	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * callback
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
	private static String callback(Configuration configuration, Request request, Response response) {
    	
    	Optional<String> code = Optional.ofNullable(request.queryParams("code"));
    	
    	if (! code.isPresent()) {
    		throw new BadRequestException("missing OAuth code from Salesforce");
    	}
    	
    	return render(SalesforceOauthController.class, configuration, request, response, getModel(), Template.SALESFORCE_OAUTH);
    };
	
    /**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * 
	 * getSalesforceToken
	 * 
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 */
	
    private static String getSalesforceToken(Configuration configuration, Request request, Response response) {
    	Token token = getToken(request);
    	
    	OauthToken oauthToken = new NowellpointClient(token)
    			.salesforce()
    			.getOauthToken(request.queryParams("code"));
    	
    	CreateSalesforceConnectorRequest createSalesforceConnectorRequest = new CreateSalesforceConnectorRequest()
    			.withId(oauthToken.getId())
    			.withAccessToken(oauthToken.getAccessToken())
    			.withInstanceUrl(oauthToken.getInstanceUrl())
    			.withRefreshToken(oauthToken.getRefreshToken());
    	
    	CreateResult<SalesforceConnector> createResult = new NowellpointClient(token)
    			.salesforceConnector()
    			.create(createSalesforceConnectorRequest);
    	
    	SalesforceConnector salesforceConnector = createResult.getTarget();
    	
    	response.redirect(String.format("/app/connectors/salesforce/%s", salesforceConnector.getId()));
    	
    	return "";
	};
}