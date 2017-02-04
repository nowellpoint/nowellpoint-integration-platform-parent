package com.nowellpoint.api.service;

import java.time.Instant;
import java.util.Date;
import java.util.Set;

import org.bson.types.ObjectId;

import com.nowellpoint.api.rest.domain.Project;

public class ProjectService {
	
	public ProjectService() {
		super();
	}
	
	/**
	 * @return
	 */
	
	public Set<Project> findAllByOwner() {
		return null;
	}

	/**
	 * 
	 * @param project
	 */
	
	public void createProject(Project project) {
		
		Date now = Date.from(Instant.now());
		
		project.setCreatedOn(now);
		//project.setCreatedBy(userInfo);
		project.setLastUpdatedOn(now);
		//project.setLastModifiedBy(userInfo);
		
		//mongoDocumentService.create(project.toDocument());
	}
	
	/**
	 * 
	 * @param id
	 * @param project
	 */

	public void updateProject(String id, Project project) {
		Project original = findProject( id );
		project.setId(id);
		project.setCreatedOn(original.getCreatedOn());
		
		Date now = Date.from(Instant.now());
		
		//project.setCreatedBy(userInfo);
		project.setLastUpdatedOn(now);
		//project.setLastModifiedBy(userInfo);
		
		//mongoDocumentService.replace(project.toDocument());
	}
	
	/**
	 * 
	 * @param id
	 */
	
	public void deleteProject(String id) {
		Project project = findProject(id);
		//mongoDocumentService.delete(project.toDocument());
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	public Project findProject(String id) {
		com.nowellpoint.api.model.document.Project document = new com.nowellpoint.api.model.document.Project(); //.find(com.nowellpoint.api.model.document.Project.class, new ObjectId( id ) );
		Project project = new Project( document );
		return project;
	}
}