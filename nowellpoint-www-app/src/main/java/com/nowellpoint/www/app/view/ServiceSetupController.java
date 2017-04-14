package com.nowellpoint.www.app.view;

import java.util.Map;

import com.nowellpoint.client.NowellpointClientOrig;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.Service;
import com.nowellpoint.client.model.Token;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class ServiceSetupController extends AbstractStaticController {
	
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	public static String setupService(Configuration configuration, Request request, Response response) {
		Token token = getToken(request);
		
		String id = request.params(":id");
		String serviceId = request.params(":serviceId");
		
		Service service = new NowellpointClientOrig(token)
				.salesforceConnector()
				.service()
				.get(id, serviceId);
		
		service.setHref(String.format(APPLICATION_CONTEXT, service.getHref().replace("${salesforceConnector.id}", id)));
		
		Map<String, Object> model = getModel();
		model.put("salesforceConnector", new SalesforceConnector(id));
		model.put("service", service);
		return render(ServiceSetupController.class, configuration, request, response, model, String.format(APPLICATION_CONTEXT, service.getTemplate()));
	}
}