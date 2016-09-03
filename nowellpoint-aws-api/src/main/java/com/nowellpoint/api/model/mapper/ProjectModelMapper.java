package com.nowellpoint.api.model.mapper;

import java.util.Set;

import org.modelmapper.TypeToken;

import com.nowellpoint.api.model.document.ProjectDocument;
import com.nowellpoint.api.model.dto.Id;
import com.nowellpoint.api.model.dto.Project;

/**************************************************************************************************************************
 * 
 * 
 * @author jherson
 *
 * 
 *************************************************************************************************************************/

public class ProjectModelMapper extends AbstractModelMapper<ProjectDocument> {
	
	
	/**************************************************************************************************************************
	 * 
	 * 
	 * constructor
	 * 
	 * 
	 *************************************************************************************************************************/
	
	public ProjectModelMapper() {
		super(ProjectDocument.class);
	}
	
	public Project findServiceProvider(Id id) {
		ProjectDocument document = findById(id.getValue());
		return modelMapper.map(document, Project.class);
	}	
	
	public void createServiceProvider(Project project) {
		ProjectDocument document = modelMapper.map(project, ProjectDocument.class);
		create(getSubject(), document);
		modelMapper.map(document, project);
	}
	
	public void updateServiceProvider(Project project) {
		ProjectDocument document = modelMapper.map(project, ProjectDocument.class);
		replace(getSubject(), document);
		modelMapper.map(document, project);
	}
	
	public Set<Project> findAllByOwner() {
		Set<ProjectDocument> documents = findAllByOwner(getSubject());
		Set<Project> resources = modelMapper.map(documents, new TypeToken<Set<Project>>() {}.getType());
		return resources;
	}
	
	public void deleteServiceProvider(Project project) {
		ProjectDocument document = modelMapper.map(project, ProjectDocument.class);
		delete(getSubject(), document);
	}
}