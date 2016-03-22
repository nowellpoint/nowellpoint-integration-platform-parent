package com.nowellpoint.aws.api.service;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nowellpoint.aws.api.dto.ProjectDTO;
import com.nowellpoint.aws.data.dynamodb.Event;
import com.nowellpoint.aws.data.dynamodb.EventAction;
import com.nowellpoint.aws.data.dynamodb.EventBuilder;
import com.nowellpoint.aws.data.mongodb.Project;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class ProjectService extends AbstractDocumentService<ProjectDTO, Project> {
	
	private static final Logger LOGGER = Logger.getLogger(ProjectService.class);
	
	private DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();
	
	public ProjectService() {
		super(ProjectDTO.class, Project.class);
	}
	
	/**
	 * 
	 * @param subject
	 * @return the list of Projects that is associated to the subject
	 */
	
	public Set<ProjectDTO> getAll(String subject) {
		Set<ProjectDTO> resources = hscan( subject, ProjectDTO.class );
		
		if (resources.isEmpty()) {
			resources = findAllByOwner(subject);
			hset( subject, resources );
		}
		
		return resources;
	}

	/**
	 * 
	 * @param subjectId
	 * @param project
	 * @param eventSource
	 * @return the created project
	 */
	
	public ProjectDTO createProject(ProjectDTO resource) {
		create(resource);
		
		hset( resource.getSubject(), ProjectDTO.class.getName().concat( resource.getId()), resource );
		hset( resource.getId(), resource.getSubject(), resource );
		
		return resource;
	}
	
	/**
	 * 
	 * @param subjectId
	 * @param project
	 * @param eventSource
	 * @return
	 */

	public ProjectDTO updateProject(String subject, ProjectDTO resource, URI eventSource) {
		ProjectDTO original = findProject( subject, resource.getId() );
		resource.setCreatedById(original.getCreatedById());
		resource.setCreatedDate(original.getCreatedDate());
		
		update(subject, resource, eventSource);
		
		hset( subject, ProjectDTO.class.getName().concat(resource.getId()), resource );
		hset( resource.getId(), subject, resource );

		return resource;
	}
	
	/**
	 * 
	 * @param projectId
	 * @param subjectId
	 * @param eventSource
	 */
	
	public void deleteProject(String id, String subject, URI eventSource) {
		ProjectDTO resource = new ProjectDTO(id);
		
		delete(subject, resource, eventSource);
		
		hdel( subject, ProjectDTO.class.getName().concat(id) );
		hdel( id, subject );
	}
	
	/**
	 * 
	 * @param subjectId
	 * @param project
	 * @param eventSource
	 * @throws IOException
	 */
	
	public void shareProject(String subject, ProjectDTO resource, URI eventSource) throws IOException {
		Project project = modelMapper.map( resource, Project.class );
		
		project.setLastModifiedDate(Date.from(Instant.now()));
		project.setLastModifiedById(subject);
		
		Event event = null;
		try {			
			event = new EventBuilder()
					.withSubject(subject)
					.withEventAction(EventAction.SHARE)
					.withEventSource(eventSource)
					.withPropertyStore(System.getenv("NCS_PROPERTY_STORE"))
					.withPayload(project)
					.withType(Project.class)
					.build();
			
			mapper.save(event);
			
		} catch (JsonProcessingException e) {
			LOGGER.error( "Share Project exception", e.getCause() );
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
		
		hset( subject, ProjectDTO.class.getName().concat(resource.getId()), resource );
		hset( resource.getId(), subject, resource );
	}
	
	
	public void restrictProject(String subjectId, ProjectDTO resource, URI eventSource) {		
		Project project = modelMapper.map( resource, Project.class );
		
		project.setLastModifiedDate(Date.from(Instant.now()));
		project.setLastModifiedById(subjectId);
		
		Event event = null;
		try {			
			event = new EventBuilder()
					.withSubject(subjectId)
					.withEventAction(EventAction.RESTRICT)
					.withEventSource(eventSource)
					.withPropertyStore(System.getenv("NCS_PROPERTY_STORE"))
					.withPayload(project)
					.withType(Project.class)
					.build();
			
			mapper.save(event);
			
		} catch (JsonProcessingException e) {
			LOGGER.error( "Share Project exception", e.getCause() );
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
		
		hdel( resource.getId(), subjectId );
	}
	
	/**
	 * 
	 * @param id
	 * @param subject
	 * @return
	 */
	
	public ProjectDTO findProject(String subject, String id) {
		ProjectDTO resource = hget( ProjectDTO.class, id, subject );
		
		if ( resource == null ) {		
			resource = find(id);
			hset( id, subject, resource );
		}
		
		return resource;
	}
}