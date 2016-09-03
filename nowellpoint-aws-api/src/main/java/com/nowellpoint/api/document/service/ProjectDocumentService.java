package com.nowellpoint.api.document.service;

import java.util.Set;

import org.modelmapper.TypeToken;

import com.nowellpoint.api.dto.ProjectDTO;
import com.nowellpoint.api.dto.Id;
import com.nowellpoint.api.model.Project;
import com.nowellpoint.api.service.AbstractModelMapper;

/**************************************************************************************************************************
 * 
 * 
 * @author jherson
 *
 * 
 *************************************************************************************************************************/

public class ProjectDocumentService extends AbstractModelMapper<Project> {
	
	
	/**************************************************************************************************************************
	 * 
	 * 
	 * constructor
	 * 
	 * 
	 *************************************************************************************************************************/
	
	public ProjectDocumentService() {
		super(Project.class);
	}
	
	public ProjectDTO findServiceProvider(Id id) {
		Project document = findById(id.getValue());
		return modelMapper.map(document, ProjectDTO.class);
	}	
	
	public void createServiceProvider(ProjectDTO project) {
		Project document = modelMapper.map(project, Project.class);
		create(getSubject(), document);
		modelMapper.map(document, project);
	}
	
	public void updateServiceProvider(ProjectDTO project) {
		Project document = modelMapper.map(project, Project.class);
		replace(getSubject(), document);
		modelMapper.map(document, project);
	}
	
	public Set<ProjectDTO> findAllByOwner() {
		Set<Project> documents = findAllByOwner(getSubject());
		Set<ProjectDTO> resources = modelMapper.map(documents, new TypeToken<Set<ProjectDTO>>() {}.getType());
		return resources;
	}
	
	public void deleteServiceProvider(ProjectDTO project) {
		Project document = modelMapper.map(project, Project.class);
		delete(document);
	}
}