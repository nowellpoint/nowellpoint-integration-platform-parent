package com.nowellpoint.www.app.view;

import java.util.Optional;

import javax.ws.rs.BadRequestException;

import com.nowellpoint.client.NowellpointClient;
import com.nowellpoint.client.model.Token;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class SalesforceOauthController extends AbstractStaticController {
	
	public static final class Template {
		public static final String SALESFORCE_OAUTH = String.format(APPLICATION_CONTEXT, "salesforce-oauth-callback.html");
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String oauth(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String oauthRedirect = NowellpointClient.defaultClient(token)
				.salesforce()
				.getOauthRedirect();
		
		response.redirect(oauthRedirect);
		
		return "";
	};
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String callback(Configuration configuration, Request request, Response response) {

		Optional<String> code = Optional.ofNullable(request.queryParams("code"));

		if (!code.isPresent()) {
			throw new BadRequestException("missing OAuth code from Salesforce");
		}

		return render(SalesforceOauthController.class, configuration, request, response, getModel(),
				Template.SALESFORCE_OAUTH);
	};
}