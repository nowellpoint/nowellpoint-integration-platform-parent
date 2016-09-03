package com.nowellpoint.api.service;

import java.util.Set;

import com.nowellpoint.api.document.service.ProjectDocumentService;
import com.nowellpoint.api.dto.Id;
import com.nowellpoint.api.dto.ProjectDTO;

public class ProjectService extends ProjectDocumentService {
	
	public ProjectService() {
		super();
	}
	
	/**
	 * 
	 * @param subject
	 * @return the list of Projects that is associated to the subject
	 */
	
	public Set<ProjectDTO> findAllByOwner() {
		return super.findAllByOwner();
	}

	/**
	 * 
	 * @param subjectId
	 * @param project
	 * @param eventSource
	 * @return the created project
	 */
	
	public void createProject(ProjectDTO project) {
		super.createServiceProvider(project);
	}
	
	/**
	 * 
	 * @param subjectId
	 * @param project
	 * @param eventSource
	 * @return
	 */

	public void updateProject(Id id, ProjectDTO project) {
		ProjectDTO original = findProject( id );
		project.setId(id.getValue());
		project.setCreatedById(original.getCreatedById());
		project.setCreatedDate(original.getCreatedDate());
		
		super.updateServiceProvider(project);
	}
	
	/**
	 * 
	 * @param projectId
	 * @param subjectId
	 * @param eventSource
	 */
	
	public void deleteProject(Id id) {
		ProjectDTO project = findProject(id);
		super.deleteServiceProvider(project);
	}
	
	/**
	 * 
	 * @param id
	 * @param subject
	 * @return
	 */
	
	public ProjectDTO findProject(Id id) {
		ProjectDTO project = super.findServiceProvider(id);
		return project;
	}
}