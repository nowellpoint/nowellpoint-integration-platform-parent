package com.nowellpoint.aws.api.resource;

import static com.mongodb.client.model.Filters.eq;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.nowellpoint.aws.api.data.CacheManager;
import com.nowellpoint.aws.api.data.Datastore;
import com.nowellpoint.aws.api.util.HttpServletRequestUtil;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.EventAction;
import com.nowellpoint.aws.model.EventBuilder;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.data.Project;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;
import com.nowellpoint.aws.tools.TokenParser;

@Path("/project")
public class ProjectResource {
	
	private static final String COLLECTION_NAME = "projects";
	
	@Inject
	private CacheManager cacheManager;
	
	@Context
	private UriInfo uriInfo;
	
	@Context
	private HttpServletRequest servletRequest;
	
	private DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();
	
	/**
	 * 
	 * @return response
	 */
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
		
		//
		// get the bearer token from the header
		//
		
		String bearerToken = HttpServletRequestUtil.getBearerToken(servletRequest);
		
		//
		//
		//
		
		String subjectId = TokenParser.getSubjectId(System.getProperty(Properties.STORMPATH_API_KEY_SECRET), bearerToken);
		
		//
		//
		//
		cacheManager.getCache().del(subjectId.concat("::").concat("projectsList").getBytes());
		Set<Project> projects = cacheManager.smembers(subjectId.concat("::").concat("projectsList"));
		
		System.out.println("project size: " + projects.size());
		
		//
		//
		//
		
		//if (projects.isEmpty()) {
			
			MongoCollection<Project> collection = Datastore.getDatabase()
					.getCollection(COLLECTION_NAME)
					.withDocumentClass(Project.class);
				
			projects = StreamSupport.stream(collection.find( eq ( "ownerId", subjectId ) ).spliterator(), false)
						.collect(Collectors.toSet());
			
			cacheManager.sadd(subjectId.concat("::").concat("projectsList"), projects);
			projects = cacheManager.smembers(subjectId.concat("::").concat("projectsList"));
		//}
		
		return Response.ok(projects).build();
    }
	
	@GET
	@Path("/{projectId}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response getProject(@PathParam("projectId") String projectId) {
		
		//
		//
		//
		
		Project project = cacheManager.get(projectId);
		
		//
		//
		//
		
		if (project == null) {
			
			project = Datastore.getDatabase().getCollection( COLLECTION_NAME )
					.withDocumentClass( Project.class )
					.find( eq ( "_id", projectId ) )
					.first();
			
			cacheManager.set(project.getId(), 259200, project);
		}
		
		//
		//
		//
		
		return Response.status(Status.OK)
				.entity(project)
				.build();
	}
	
	
	@DELETE
	@Path("/{projectId}")
    @Produces(MediaType.APPLICATION_JSON)
	public Response deleteProject(@PathParam("projectId") String projectId) {
		
		//
		// get the bearer token from the header
		//
		
		String bearerToken = HttpServletRequestUtil.getBearerToken(servletRequest);
		
		//
		//
		//
		
		String subjectId = TokenParser.getSubjectId(System.getProperty(Properties.STORMPATH_API_KEY_SECRET), bearerToken);

		//
		//
		//
			
		Event event = null;
		try {			
			event = new EventBuilder()
					.withSubjectId(subjectId)
					.withEventAction(EventAction.DELETE)
					.withEventSource(uriInfo.getRequestUri())
					.withPropertyStore(System.getenv("PROPERTY_STORE"))
					.withPayload(new Project(projectId))
					.withType(Project.class)
					.build();
		} catch (JsonProcessingException e) {
			throw new WebApplicationException(e);
		}
		
		//
		//
		//
		
		mapper.save(event);
		
		Datastore.getDatabase().getCollection( COLLECTION_NAME ).withDocumentClass( Project.class ).deleteOne( eq ( "_id", projectId ) );
		
		//
		//
		//
		
		cacheManager.del(projectId);
		
		//
		//
		//
		
		return Response.noContent()
				.build();
	}
	
	/**
	 * 
	 * @param resource
	 * @return response
	 */

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	public Response createProject(Project resource) {
		
		//
		// get the bearer token from the header
		//
		
		String bearerToken = HttpServletRequestUtil.getBearerToken(servletRequest);
		
		//
		//
		//
		
		String subjectId = TokenParser.getSubjectId(System.getProperty(Properties.STORMPATH_API_KEY_SECRET), bearerToken);
		
		//
		//
		//
				
		Event event = null;
		try {			
			event = new EventBuilder()
					.withSubjectId(subjectId)
					.withEventAction(EventAction.CREATE)
					.withEventSource(uriInfo.getRequestUri())
					.withPropertyStore(System.getenv("PROPERTY_STORE"))
					.withPayload(resource)
					.withType(Project.class)
					.build();
		} catch (JsonProcessingException e) {
			throw new WebApplicationException(e);
		}
		
		//
		//
		//
		
		mapper.save(event);
		
		//
		//
		//
		
		cacheManager.set(event.getId(), resource);
		
		//
		//
		//
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(ProjectResource.class)
				.path("/{id}")
				.build(event.getId());
		
		//
		//
		//
		
		return Response.created(uri).build();
	}
	
	/**
	 * 
	 * @param resource
	 * @return response
	 */
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateProject(Project resource) {
		
		//
		// get the bearer token from the header
		//
		
		String bearerToken = HttpServletRequestUtil.getBearerToken(servletRequest);
		
		//
		//
		//
		
		String subjectId = TokenParser.getSubjectId(System.getProperty(Properties.STORMPATH_API_KEY_SECRET), bearerToken);
		
		//
		//
		//
				
		Event event = null;
		try {			
			event = new EventBuilder()
					.withSubjectId(subjectId)
					.withEventAction(EventAction.UPDATE)
					.withEventSource(uriInfo.getRequestUri())
					.withPropertyStore(System.getenv("PROPERTY_STORE"))
					.withPayload(resource)
					.withType(Project.class)
					.build();
		} catch (JsonProcessingException e) {
			throw new WebApplicationException(e);
		}
		
		//
		//
		//
		
		mapper.save(event);
		
		//
		//
		//
		
		cacheManager.set(event.getId(), resource);
		
		//
		//
		//
		
		URI uri = UriBuilder.fromUri(uriInfo.getBaseUri())
				.path(ProjectResource.class)
				.path("/{id}")
				.build(event.getId());
		
		//
		//
		//
		
		return Response.created(uri).build();
	}	
}