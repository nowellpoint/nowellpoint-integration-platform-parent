package com.nowellpoint.aws.api.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.nowellpoint.aws.api.data.CacheManager.serialize;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.MongoCollection;
import com.nowellpoint.aws.api.data.CacheManager;
import com.nowellpoint.aws.api.data.Datastore;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.EventAction;
import com.nowellpoint.aws.model.EventBuilder;
import com.nowellpoint.aws.model.data.Project;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class ProjectService {
	
	private static final Logger LOGGER = Logger.getLogger(ProjectService.class);
	
	private static final String COLLECTION_NAME = "projects";
	
	private DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();
	
	@Inject
	private CacheManager cacheManager;
	
	@PostConstruct
	public void postConstruct() {
		
	}
	
	/**
	 * 
	 * @param subjectId
	 * @return the list of Projects that is associated to the subject
	 */
	
	public Set<Project> getAll(String subjectId) {
		
		//
		//
		//
		
		Set<Project> projects = cacheManager.hscanByClassType( subjectId, Project.class );
		
		//
		//
		//
		
		if (projects.isEmpty()) {
			
			MongoCollection<Project> collection = Datastore.getDatabase()
					.getCollection( COLLECTION_NAME )
					.withDocumentClass( Project.class );
				
			projects = StreamSupport.stream( collection.find( eq ( "ownerId", subjectId ) ).spliterator(), false )
						.collect( Collectors.toSet() );
			
			cacheManager.hsetByClassType( subjectId, projects );
		}
		
		//
		//
		//
		
		projects.stream().sorted((p1, p2) -> p1.getCreatedDate().compareTo(p2.getCreatedDate()));
		
		//
		//
		//
		
		return projects;
	}

	/**
	 * 
	 * @param subjectId
	 * @param project
	 * @param eventSource
	 * @return the created project
	 */
	
	public Project create(String subjectId, Project project, URI eventSource) {
		
		//
		//
		//
		
		project.setId(UUID.randomUUID().toString());
		project.setCreatedDate(Date.from(Instant.now()));
		project.setLastModifiedDate(Date.from(Instant.now()));
		project.setCreatedById(subjectId);
		project.setLastModifiedById(subjectId);
		
		//
		//
		//
				
		Event event = null;
		try {			
			event = new EventBuilder()
					.withSubjectId(subjectId)
					.withEventAction(EventAction.CREATE)
					.withEventSource(eventSource)
					.withPropertyStore(System.getenv("PROPERTY_STORE"))
					.withPayload(project)
					.withType(Project.class)
					.build();
			
			mapper.save( event );
			
		} catch (JsonProcessingException e) {
			LOGGER.error( "Create Project exception", e.getCause() );
			throw new WebApplicationException(e);
		}
		
		//
		// set the user specific cache entry
		//
		
		cacheManager.getCache().hset( subjectId.getBytes(), Project.class.getName().concat(project.getId()).getBytes(), serialize( project ) );
		
		//
		// 
		//
		
		cacheManager.hset( project.getId(), subjectId, project );
		
		//
		//
		//
		
		return project;
	}
	
	/**
	 * 
	 * @param subjectId
	 * @param project
	 * @param eventSource
	 * @return
	 */

	public Project update(String subjectId, Project project, URI eventSource) {
		
		//
		//
		//
		
		project.setLastModifiedDate(Date.from(Instant.now()));
		project.setLastModifiedById(subjectId);
		
		//
		//
		//
				
		Event event = null;
		try {			
			event = new EventBuilder()
					.withSubjectId(subjectId)
					.withEventAction(EventAction.UPDATE)
					.withEventSource(eventSource)
					.withPropertyStore(System.getenv("PROPERTY_STORE"))
					.withPayload(project)
					.withType(Project.class)
					.build();
			
			mapper.save(event);
			
		} catch (JsonProcessingException e) {
			throw new WebApplicationException(e);
		}
		
		//
		//
		//
		
		cacheManager.hset(project.getId(), subjectId, project);
		
		//
		//
		//
		
		return project;
	}
	
	/**
	 * 
	 * @param projectId
	 * @param subjectId
	 * @param eventSource
	 */
	
	public void delete(String projectId, String subjectId, URI eventSource) {
		
		//
		//
		//
			
		Event event = null;
		try {			
			event = new EventBuilder()
					.withSubjectId(subjectId)
					.withEventAction(EventAction.DELETE)
					.withEventSource(eventSource)
					.withPropertyStore(System.getenv("PROPERTY_STORE"))
					.withPayload(new Project(projectId))
					.withType(Project.class)
					.build();
			
			mapper.save( event );
			
		} catch (JsonProcessingException e) {
			LOGGER.error( "Delete Project exception", e.getCause() );
			throw new WebApplicationException( e, Status.INTERNAL_SERVER_ERROR );
		}
		
		//
		//
		//
		
		cacheManager.hdel( projectId, subjectId );
	}
	
	/**
	 * 
	 * @param subjectId
	 * @param project
	 * @param eventSource
	 * @throws IOException
	 */
	
	public void share(String subjectId, Project project, URI eventSource) throws IOException {
		
		//
		//
		//
		
		project.setLastModifiedDate(Date.from(Instant.now()));
		project.setLastModifiedById(subjectId);
		
		//
		//
		//
				
		Event event = null;
		try {			
			event = new EventBuilder()
					.withSubjectId(subjectId)
					.withEventAction(EventAction.SHARE)
					.withEventSource(eventSource)
					.withPropertyStore(System.getenv("PROPERTY_STORE"))
					.withPayload(project)
					.withType(Project.class)
					.build();
			
			mapper.save(event);
			
		} catch (JsonProcessingException e) {
			LOGGER.error( "Share Project exception", e.getCause() );
			throw new WebApplicationException(e);
		}
		
		cacheManager.hset(project.getId(), subjectId, project);
	}
	
	
	public void restrict(String subjectId, Project project, URI eventSource) {
		
		//
		//
		//
		
		project.setLastModifiedDate(Date.from(Instant.now()));
		project.setLastModifiedById(subjectId);
		
		//
		//
		//
				
		Event event = null;
		try {			
			event = new EventBuilder()
					.withSubjectId(subjectId)
					.withEventAction(EventAction.RESTRICT)
					.withEventSource(eventSource)
					.withPropertyStore(System.getenv("PROPERTY_STORE"))
					.withPayload(project)
					.withType(Project.class)
					.build();
			
			mapper.save(event);
			
		} catch (JsonProcessingException e) {
			LOGGER.error( "Share Project exception", e.getCause() );
			throw new WebApplicationException(e);
		}
		
		//
		//
		//
		
		cacheManager.hdel(project.getId(), subjectId);
	}
	
	/**
	 * 
	 * @param projectId
	 * @param subjectId
	 * @return
	 */
	
	public Project get(String projectId, String subjectId) {
		
		//
		//
		//
		
		Project project = cacheManager.hget( projectId, subjectId );
		
		//
		//
		//
		
		if ( project == null ) {
			
			project = Datastore.getDatabase().getCollection( COLLECTION_NAME )
					.withDocumentClass( Project.class )
					.find( and ( eq ( "_id", projectId ), eq ( "ownerId", subjectId ) ) )
					.first();
			
			if ( project == null ) {
				throw new WebApplicationException( String.format( "Project Id: %s does not exist or you do not have access to view", projectId ), Status.NOT_FOUND );
			}

			cacheManager.hset( projectId, subjectId, project );
		}
		
		//
		//
		//
		
		return project;
	}
}