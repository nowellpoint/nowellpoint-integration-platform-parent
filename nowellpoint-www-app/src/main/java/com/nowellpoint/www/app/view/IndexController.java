package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.Environment;
import com.nowellpoint.client.model.Plan;
import com.nowellpoint.client.model.PlanList;
import com.nowellpoint.client.model.exception.ServiceUnavailableException;
import com.nowellpoint.www.app.util.MessageProvider;
import com.nowellpoint.www.app.util.Path;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class IndexController extends AbstractController {
	
	private static final Logger logger = Logger.getLogger(IndexController.class.getName());
	
	public static class Template {
		public static final String INDEX = "index.html";
	}
	
	public IndexController(Configuration configuration) {
		super(IndexController.class);
		configureRoutes(configuration);
	}
	
	private void configureRoutes(Configuration configuration) {
		get(Path.Route.INDEX, (request, response) -> index(configuration, request, response));
		post(Path.Route.CONTACT, (request, response) -> contactUs(configuration, request, response));
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String index(Configuration configuration, Request request, Response response) {
		
		HttpResponse httpResponse = RestResource.get(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")).getEnvironmentUrl())
				.path("plans")
				.queryParameter("localeSidKey", "en_US")
				.queryParameter("languageSidKey", "en_US")
				.execute();
		
		PlanList planList = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			planList = httpResponse.getEntity(PlanList.class);
		} else {
			throw new ServiceUnavailableException(httpResponse.getAsString());
		}
		
		List<Plan> plans = planList.getItems()
				.stream()
				.sorted((p1, p2) -> p1.getPrice().getUnitPrice().compareTo(p2.getPrice().getUnitPrice()))
				.collect(Collectors.toList());
		
		Map<String,Object> model = getModel();
		model.put("planList", plans);
		
		return render(configuration, request, response, model, Template.INDEX);
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String contactUs(Configuration configuration, Request request, Response response) {
		
    	HttpResponse httpResponse = RestResource.post(Environment.parseEnvironment(System.getenv("NOWELLPOINT_ENVIRONMENT")).getEnvironmentUrl())
    			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
    			.path("leads")
				.parameter("firstName", request.queryParams("firstName"))
				.parameter("lastName", request.queryParams("lastName"))
				.parameter("email", request.queryParams("email"))
				.parameter("phone", request.queryParams("phone"))
				.parameter("company", request.queryParams("company"))
				.parameter("message", request.queryParams("message"))
    			.execute();
    	
    	logger.info(httpResponse.getHeaders().get("Location"));
    	
    	return MessageProvider.getMessage(Locale.US, "contactConfirm");
	};
}