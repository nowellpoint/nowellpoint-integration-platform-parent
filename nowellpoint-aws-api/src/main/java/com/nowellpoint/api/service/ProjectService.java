package com.nowellpoint.api.service;

import java.util.Set;

import com.nowellpoint.api.model.domain.Project;
import com.nowellpoint.api.model.mapper.ProjectModelMapper;

public class ProjectService extends ProjectModelMapper {
	
	public ProjectService() {
		super();
	}
	
	/**
	 * @return
	 */
	
	public Set<Project> findAllByOwner() {
		return super.findAllByOwner();
	}

	/**
	 * 
	 * @param project
	 */
	
	public void createProject(Project project) {
		super.createServiceProvider(project);
	}
	
	/**
	 * 
	 * @param id
	 * @param project
	 */

	public void updateProject(String id, Project project) {
		Project original = findProject( id );
		project.setId(id);
		project.setCreatedDate(original.getCreatedDate());
		
		super.updateServiceProvider(project);
	}
	
	/**
	 * 
	 * @param id
	 */
	
	public void deleteProject(String id) {
		Project project = findProject(id);
		super.deleteServiceProvider(project);
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	public Project findProject(String id) {
		Project project = super.findServiceProvider(id);
		return project;
	}
}