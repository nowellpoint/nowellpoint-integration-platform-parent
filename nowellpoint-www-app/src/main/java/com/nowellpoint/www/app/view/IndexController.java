package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import jdk.nashorn.api.scripting.NashornScriptEngine;
import spark.Request;
import spark.Response;

public class IndexController extends AbstractController {
	
	private static final Logger logger = Logger.getLogger(IndexController.class.getName());
	
	public static class Template {
		public static final String INDEX = "index2.html";
	}
	
	public IndexController(Configuration configuration) {
		super(IndexController.class);
		configureRoutes(configuration);
	}
	
	@Override
	public void configureRoutes(Configuration configuration) {
		get(Path.Route.INDEX, (request, response) -> index(configuration, request, response));
		post(Path.Route.CONTACT, (request, response) -> contactUs(configuration, request, response));
		get("/react", (request, response) -> react(configuration, request, response));
	}
	
	private String react(Configuration configuration, Request request, Response response) throws JsonProcessingException {
		List<Plan> comments = loadPlans();
        String content = renderCommentBox(request, comments);
        String data = objectMapper.writeValueAsString(comments);
        Map<String,Object> model = getModel();
        model.put("content", content);
        model.put("data", data);
        return render(configuration, request, response, getModel(), "index3.html");
	}
	
	/**
	 * 
	 * @param configuration
	 * @param request
	 * @param response
	 * @return
	 */
	
	private String index(Configuration configuration, Request request, Response response) {
		return render(configuration, request, response, getModel(), Template.INDEX);
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
	
	private Reader read(String path) throws FileNotFoundException {
		File file = new File(path);
	    return new FileReader(file);
	}
	
	private static List<Plan> loadPlans() {
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
		
		return plans;
	}
	
	private String renderCommentBox(Request request, List<Plan> comments) {
		NashornScriptEngine nashorn = (NashornScriptEngine) new ScriptEngineManager().getEngineByName("nashorn");
		
		String path = request.session().raw().getServletContext().getRealPath("js").concat("/");
		
		try {
			nashorn.eval(read(path.concat("nashorn-polyfill.js")));
			nashorn.eval(read(path.concat("react.min.js")));
			nashorn.eval(read(path.concat("react-dom.min.js")));
			nashorn.eval(read(path.concat("showdown.min.js")));
			nashorn.eval(read(path.concat("commentBox.js")));
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    try {
	        Object html = nashorn.invokeFunction("renderServer", comments);
	        return String.valueOf(html);
	    } catch (Exception e) {
	        throw new IllegalStateException("failed to render react component", e);
	    }
	}
}