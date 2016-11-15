package com.nowellpoint.api.service;

import java.util.Set;

import com.nowellpoint.api.model.domain.Project;
import com.nowellpoint.api.model.mapper.ProjectModelMapper;

public class ProjectService extends ProjectModelMapper {
	
	public ProjectService() {
		super();
	}
	
	/**
	 * 
	 * @param subject
	 * @return the list of Projects that is associated to the subject
	 */
	
	public Set<Project> findAllByOwner() {
		return super.findAllByOwner();
	}

	/**
	 * 
	 * @param subjectId
	 * @param project
	 * @param eventSource
	 * @return the created project
	 */
	
	public void createProject(Project project) {
		super.createServiceProvider(project);
	}
	
	/**
	 * 
	 * @param subjectId
	 * @param project
	 * @param eventSource
	 * @return
	 */

	public void updateProject(String id, Project project) {
		Project original = findProject( id );
		project.setId(id);
		project.setCreatedDate(original.getCreatedDate());
		
		super.updateServiceProvider(project);
	}
	
	/**
	 * 
	 * @param projectId
	 * @param subjectId
	 * @param eventSource
	 */
	
	public void deleteProject(String id) {
		Project project = findProject(id);
		super.deleteServiceProvider(project);
	}
	
	/**
	 * 
	 * @param id
	 * @param subject
	 * @return
	 */
	
	public Project findProject(String id) {
		Project project = super.findServiceProvider(id);
		return project;
	}
}