package com.nowellpoint.www.app.view;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.delete;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response.Status;

import com.nowellpoint.client.model.AccountProfile;
import com.nowellpoint.client.model.Project;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.http.HttpResponse;
import com.nowellpoint.http.MediaType;
import com.nowellpoint.http.RestResource;
import com.nowellpoint.www.app.util.Path;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.Request;
import spark.Response;

public class ProjectController extends AbstractController {
	
	private static final Logger logger = Logger.getLogger(ProjectController.class.getName());
	
	public static class Template {
		public static final String PROJECT = String.format(APPLICATION_CONTEXT, "project.html");
		public static final String PROJECT_LIST = String.format(APPLICATION_CONTEXT, "project-list.html");
	}
	
	public ProjectController() {
		super(ProjectController.class);
	}
	
	public void configureRoutes(Configuration configuration) {
		get(Path.Route.PROJECTS, (request, response) -> getProjects(configuration, request, response));
		get(Path.Route.PROJECTS.concat("/:id"), (request, response) -> getProject(configuration, request, response));
		post(Path.Route.PROJECTS, (request, response) -> saveProject(configuration, request, response));
		delete(Path.Route.PROJECTS.concat("/:id"), (request, response) -> deleteProject(configuration, request, response));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private String getProjects(Configuration configuration, Request request, Response response) {
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("project")
				.execute();
		
		logger.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		List<Project> projects = httpResponse.getEntityList(Project.class);
		
		projects = projects.stream().sorted((p1, p2) -> p1.getCreatedOn().compareTo(p2.getCreatedOn())).collect(Collectors.toList());
		
		AccountProfile account = request.attribute("account");
		
		Project project = new Project();
		project.setOwner(account);
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", account);
		model.put("project", project);
		model.put("projectList", projects);
		
		return render(configuration, request, response, model, Template.PROJECT_LIST);
	}
	
	private String getProject(Configuration configuration, Request request, Response response) {
		
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
		
		return render(configuration, request, response, model, Template.PROJECT);	
	}
	
	private String saveProject(Configuration configuration, Request request, Response response) {
		
		Token token = request.attribute("token");
		AccountProfile account = request.attribute("account");
		
		AccountProfile owner = new AccountProfile();
		owner.setId(request.queryParams("ownerId").trim().isEmpty() ? null : request.queryParams("ownerId"));
		
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
		
		return render(configuration, request, response, model, Template.PROJECT);
	}
	
	private String deleteProject(Configuration configuration, Request request, Response response) {
		
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
	}
}