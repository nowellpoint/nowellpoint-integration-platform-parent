package com.nowellpoint.aws.api.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;
import org.modelmapper.TypeToken;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.MongoCollection;
import com.nowellpoint.aws.api.data.Datastore;
import com.nowellpoint.aws.api.dto.ProjectDTO;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.EventAction;
import com.nowellpoint.aws.model.EventBuilder;
import com.nowellpoint.aws.model.data.Project;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class ProjectService extends AbstractDataService {
	
	private static final Logger LOGGER = Logger.getLogger(ProjectService.class);
	
	private static final String COLLECTION_NAME = "projects";
	
	private DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();
	
	@PostConstruct
	public void postConstruct() {
		
	}
	
	/**
	 * 
	 * @param subjectId
	 * @return the list of Projects that is associated to the subject
	 */
	
	public Set<ProjectDTO> getAll(String subject) {
		
		//
		//
		//
		
		Set<ProjectDTO> resources = hscan( subject, ProjectDTO.class );
		
		//
		//
		//
		
		if (resources.isEmpty()) {
			
			MongoCollection<Project> collection = Datastore.getDatabase()
					.getCollection( COLLECTION_NAME )
					.withDocumentClass( Project.class );
				
			Set<Project> projects = StreamSupport.stream( collection.find( eq ( "owner", subject ) ).spliterator(), false )
						.collect( Collectors.toSet() );
			
			Type type = new TypeToken<Set<ProjectDTO>>() {}.getType();
			
			resources = modelMapper.map( projects, type );
			
			hset( getSubjectId(subject), resources );
		}
		
		//
		//
		//
		
		return resources;
	}

	/**
	 * 
	 * @param subjectId
	 * @param project
	 * @param eventSource
	 * @return the created project
	 */
	
	public ProjectDTO create(String subject, ProjectDTO resource, URI eventSource) {
		
		//
		//
		//
		
		Project project = modelMapper.map( resource, Project.class );
		
		//
		//
		//
		
		project.setId(UUID.randomUUID().toString());
		project.setCreatedDate(Date.from(Instant.now()));
		project.setLastModifiedDate(Date.from(Instant.now()));
		project.setCreatedById(subject);
		project.setLastModifiedById(subject);
		
		//
		//
		//
				
		Event event = null;
		try {			
			event = new EventBuilder()
					.withSubjectId(subject)
					.withEventAction(EventAction.CREATE)
					.withEventSource(eventSource)
					.withPropertyStore(System.getenv("NCS_PROPERTY_STORE"))
					.withPayload(project)
					.withType(Project.class)
					.build();
			
			mapper.save( event );
			
		} catch (JsonProcessingException e) {
			LOGGER.error( "Create Project exception", e.getCause() );
			throw new WebApplicationException(e);
		}
		
		//
		//
		//
		
		modelMapper.map( project, resource );
		
		//
		//
		//
System.out.println("creating: " + resource.getId() + subject);
		hset( getSubjectId(subject), ProjectDTO.class.getName().concat(resource.getId()), resource );
		hset( resource.getId(), getSubjectId(subject), resource );
		
		//
		//
		//
		
		return resource;
	}
	
	/**
	 * 
	 * @param subjectId
	 * @param project
	 * @param eventSource
	 * @return
	 */

	public ProjectDTO update(String subject, ProjectDTO resource, URI eventSource) {
		
		ProjectDTO current = hget( resource.getId(), getSubjectId(subject) );
		
		//
		//
		//
		
		Project project = modelMapper.map( resource, Project.class );
		project.setCreatedDate(current.getCreatedDate());
		project.setCreatedById(current.getCreatedById());
		project.setLastModifiedDate(Date.from(Instant.now()));
		project.setLastModifiedById(subject);
		
		//
		//
		//
				
		Event event = null;
		try {			
			event = new EventBuilder()
					.withSubjectId(subject)
					.withEventAction(EventAction.UPDATE)
					.withEventSource(eventSource)
					.withPropertyStore(System.getenv("NCS_PROPERTY_STORE"))
					.withPayload(project)
					.withType(Project.class)
					.build();
			
			mapper.save(event);
			
		} catch (JsonProcessingException e) {
			throw new WebApplicationException(e);
		}
		
		//
		// merge create only fields
		//
		
		
		
		modelMapper.map( project, resource );
		
		resource.setCreatedById(current.getCreatedById());
		resource.setCreatedDate(current.getCreatedDate());
		
		System.out.println("created date: " + resource.getCreatedDate());
		System.out.println("last modified date: " +resource.getLastModifiedDate());
		
		//
		//
		//
		System.out.println(this.getClass().getName() + " " + resource.getId() + " " + subject);
		hset( getSubjectId(subject), ProjectDTO.class.getName().concat(resource.getId()), resource );
		hset( resource.getId(), getSubjectId(subject), resource );

		//
		//
		//
		
		return resource;
	}
	
	/**
	 * 
	 * @param projectId
	 * @param subjectId
	 * @param eventSource
	 */
	
	public void delete(String id, String subject, URI eventSource) {
		
		//
		//
		//
			
		Event event = null;
		try {			
			event = new EventBuilder()
					.withSubjectId(subject)
					.withEventAction(EventAction.DELETE)
					.withEventSource(eventSource)
					.withPropertyStore(System.getenv("NCS_PROPERTY_STORE"))
					.withPayload(new Project(id))
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
		
		hdel( getSubjectId(subject), ProjectDTO.class.getName().concat(id) );
		hdel( id, getSubjectId(subject) );
	}
	
	/**
	 * 
	 * @param subjectId
	 * @param project
	 * @param eventSource
	 * @throws IOException
	 */
	
	public void share(String subject, ProjectDTO resource, URI eventSource) throws IOException {
		
		//
		//
		//
		
		Project project = modelMapper.map( resource, Project.class );
		
		//
		//
		//
		
		project.setLastModifiedDate(Date.from(Instant.now()));
		project.setLastModifiedById(subject);
		
		//
		//
		//
				
		Event event = null;
		try {			
			event = new EventBuilder()
					.withSubjectId(subject)
					.withEventAction(EventAction.SHARE)
					.withEventSource(eventSource)
					.withPropertyStore(System.getenv("NCS_PROPERTY_STORE"))
					.withPayload(project)
					.withType(Project.class)
					.build();
			
			mapper.save(event);
			
		} catch (JsonProcessingException e) {
			LOGGER.error( "Share Project exception", e.getCause() );
			throw new WebApplicationException(e);
		}
		
		hset( getSubjectId(subject), ProjectDTO.class.getName().concat(resource.getId()), resource );
		hset( resource.getId(), getSubjectId(subject), resource );
	}
	
	
	public void restrict(String subjectId, ProjectDTO resource, URI eventSource) {
		
		//
		//
		//
		
		Project project = modelMapper.map( resource, Project.class );
		
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
					.withPropertyStore(System.getenv("NCS_PROPERTY_STORE"))
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
		
		hdel( resource.getId(), subjectId );
	}
	
	/**
	 * 
	 * @param projectId
	 * @param subjectId
	 * @return
	 */
	
	public ProjectDTO get(String id, String subject) {
		
		//
		//
		//
		System.out.println(id);
		System.out.println(subject);
		ProjectDTO resource = null;
		try {
		resource = hget( id, getSubjectId(subject) );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//
		//
		//
		
		if ( resource == null ) {
			System.out.println("resource not found");
			
			Project project = Datastore.getDatabase().getCollection( COLLECTION_NAME )
					.withDocumentClass( Project.class )
					.find( and ( eq ( "_id", id ), eq ( "owner", subject ) ) )
					.first();
			
			if ( project == null ) {
				throw new WebApplicationException( String.format( "Project Id: %s does not exist or you do not have access to view", id ), Status.NOT_FOUND );
			}
			
			resource = modelMapper.map( project, ProjectDTO.class );
			System.out.println("adding resource for " + id + " " + subject);
			hset( id, getSubjectId(subject), resource );
		}
		
		//
		//
		//
		
		return resource;
	}
}