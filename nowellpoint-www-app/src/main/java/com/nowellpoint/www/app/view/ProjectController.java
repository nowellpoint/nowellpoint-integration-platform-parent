package com.nowellpoint.www.app.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response.Status;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.AccountProfile;
import com.nowellpoint.www.app.model.Project;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.Request;
import spark.Response;
import spark.Route;

public class ProjectController extends AbstractController {
	
	private static final Logger logger = Logger.getLogger(ProjectController.class.getName());
	
	public ProjectController(Configuration cfg) {
		super(ProjectController.class, cfg);
	}
	
	public void configureRoutes(Configuration cfg) {
		
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	public Route getProjects = (Request request, Response response) -> {
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("project")
				.execute();
		
		logger.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		List<Project> projects = httpResponse.getEntityList(Project.class);
		
		projects = projects.stream().sorted((p1, p2) -> p1.getCreatedDate().compareTo(p2.getCreatedDate())).collect(Collectors.toList());
		
		Account account = request.attribute("account");
		
		AccountProfile owner = new AccountProfile();
		owner.setName(account.getFullName());
		owner.setHref(account.getHref());
		
		Project project = new Project();
		project.setOwner(owner);
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", account);
		model.put("project", project);
		model.put("projectList", projects);
		
		return render(request, model, Path.Template.PROJECT_LIST);
	};
	
	public Route getProject = (Request request, Response response) -> {
		
		String projectId = request.params(":id");
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("project")
				.path(projectId)
				.execute();
		
		logger.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		if (httpResponse.getStatusCode() != Status.OK.getStatusCode()) {
			throw new NotFoundException(httpResponse.getAsString());
		}
		
		Project project = httpResponse.getEntity(Project.class);
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		model.put("project", project);
		
		return render(request, model, Path.Template.PROJECT);	
	};
	
	public Route saveProject = (Request request, Response response) -> {
		
		Token token = request.attribute("token");
		Account account = request.attribute("account");
		
		AccountProfile owner = new AccountProfile();
		owner.setId(request.queryParams("ownerId").trim().isEmpty() ? null : request.queryParams("ownerId"));
		owner.setHref(account.getHref());
		
		Project project = new Project();
		project.setDescription(request.queryParams("description"));
		project.setId(request.queryParams("id").trim().isEmpty() ? null : request.queryParams("id"));
		project.setName(request.queryParams("name"));
		project.setStage(request.queryParams("stage"));
		project.setOwner(owner);
		
		HttpResponse httpResponse = null;
		
		if (request.queryParams("id").trim().isEmpty()) {
			
			httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_JSON)
					.path("project")
					.body(project)
					.execute();
			
		} else {
			
			httpResponse = RestResource.put(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_JSON)
					.path("project")
					.path(request.queryParams("id"))
					.body(project)
					.execute();
		}
		
		if (httpResponse.getStatusCode() != Status.OK.getStatusCode() && httpResponse.getStatusCode() != Status.CREATED.getStatusCode()) {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		project = httpResponse.getEntity(Project.class);
		
		logger.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo() + " : " + httpResponse.getHeaders().get("Location"));
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", account);
		model.put("project", project);
		
		return render(request, model, Path.Template.PROJECT);
	};
	
	public Route deleteProject = (Request request, Response response) -> {
		
		String projectId = request.params(":id");
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.delete(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("project")
				.path(projectId)
				.execute();
		
		logger.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		if (httpResponse.getStatusCode() != Status.NO_CONTENT.getStatusCode()) {
			throw new BadRequestException(httpResponse.getAsString());
		}
		
		return "";	
	};
}