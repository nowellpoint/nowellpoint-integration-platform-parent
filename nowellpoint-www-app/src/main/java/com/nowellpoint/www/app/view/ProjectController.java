package com.nowellpoint.www.app.view;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Token;
import com.nowellpoint.www.app.model.Project;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

public class ProjectController {
	
	private static final Logger logger = Logger.getLogger(ProjectController.class.getName());
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public ProjectController(Configuration cfg) {
		
		//
		// GET /app/projects
		//
		
		get("/app/projects", (request, response) -> getProjects(request, response), new FreeMarkerEngine(cfg));

		//
		// GET /app/projects/:id
		//
		
		get("/app/projects/:id", (request, response) -> getProject(request, response), new FreeMarkerEngine(cfg));
		

		//
		// POST "/app/projects"
		//
		
		post("/app/projects", (request, response) -> postProjects(request, response), new FreeMarkerEngine(cfg));
		
		//
		// DELETE /app/projects/:id
		//
		
		delete("/app/projects/:id", (request, response) -> deleteProject(request, response));
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static ModelAndView getProjects(Request request, Response response) throws IOException {
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.get(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("project")
				.execute();
		
		logger.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		String json = httpResponse.getEntity();
		
		List<Project> projects = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Project.class));
		
		projects = projects.stream().sorted((p1, p2) -> p1.getCreatedDate().compareTo(p2.getCreatedDate())).collect(Collectors.toList());
		
		Account account = request.attribute("account");
		
		Project project = new Project();
		project.setOwner(account.getHref());
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", account);
		model.put("project", project);
		model.put("projectList", projects);
		
		return new ModelAndView(model, "secure/project-list.html");
		
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static ModelAndView getProject(Request request, Response response) throws IOException {
		
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
			throw new NotFoundException(httpResponse.getEntity());
		}
		
		Project project = httpResponse.getEntity(Project.class);
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		model.put("project", project);
		
		return new ModelAndView(model, "secure/project.html");	
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static ModelAndView postProjects(Request request, Response response) throws IOException {
		
		Token token = request.attribute("token");
		
		ObjectNode node = objectMapper.createObjectNode();
    	request.queryParams().stream().forEach(param -> {
    		node.put(param, request.queryParams(param));
    	});

		HttpResponse httpResponse = null;
		
		if (request.queryParams("id").trim().isEmpty()) {
			
			node.putNull("id");
			
			httpResponse = RestResource.post(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_JSON)
					.path("project")
					.body(node)
					.execute();
			
		} else {
			
			httpResponse = RestResource.put(System.getenv("NCS_API_ENDPOINT"))
					.header("x-api-key", System.getenv("NCS_API_KEY"))
					.bearerAuthorization(token.getAccessToken())
					.contentType(MediaType.APPLICATION_JSON)
					.path("project")
					.body(node)
					.execute();
		}
		
		if (httpResponse.getStatusCode() != Status.OK.getStatusCode() && httpResponse.getStatusCode() != Status.CREATED.getStatusCode()) {
			throw new BadRequestException(httpResponse.getEntity());
		}
		
		Project project = httpResponse.getEntity(Project.class);
		
		logger.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo() + " : " + httpResponse.getHeaders().get("Location"));
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("account", request.attribute("account"));
		model.put("project", project);
		
		return new ModelAndView(model, "secure/project.html");
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	
	private static String deleteProject(Request request, Response response) throws IOException {
		
		String projectId = request.params(":id");
		
		Token token = request.attribute("token");
		
		HttpResponse httpResponse = RestResource.delete(System.getenv("NCS_API_ENDPOINT"))
				.header("x-api-key", System.getenv("NCS_API_KEY"))
				.bearerAuthorization(token.getAccessToken())
				.path("project")
				.path(projectId)
				.execute();
		
		logger.info("Status Code: " + httpResponse.getStatusCode() + " Method: " + request.requestMethod() + " : " + request.pathInfo());
		
		return "";	
	}
}