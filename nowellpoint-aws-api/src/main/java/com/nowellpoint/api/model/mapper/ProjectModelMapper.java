package com.nowellpoint.api.model.mapper;

import java.util.HashSet;
import java.util.Set;

import org.modelmapper.TypeToken;

import com.nowellpoint.api.model.dto.Id;
import com.nowellpoint.api.model.dto.Project;

/**
 * 
 * 
 * @author jherson
 *
 * 
 */

public class ProjectModelMapper extends AbstractModelMapper<com.nowellpoint.api.model.document.Project> {
	
	
	/**
	 * 
	 * 
	 * constructor
	 * 
	 * 
	 */
	
	public ProjectModelMapper() {
		super(com.nowellpoint.api.model.document.Project.class);
	}
	
	public Project findServiceProvider(Id id) {
		com.nowellpoint.api.model.document.Project document = findById(id.getValue());
		return modelMapper.map(document, Project.class);
	}	
	
	public void createServiceProvider(Project project) {
		com.nowellpoint.api.model.document.Project document = modelMapper.map(project, com.nowellpoint.api.model.document.Project.class);
		create(getSubject(), document);
		modelMapper.map(document, project);
	}
	
	public void updateServiceProvider(Project project) {
		com.nowellpoint.api.model.document.Project document = modelMapper.map(project, com.nowellpoint.api.model.document.Project.class);
		replace(getSubject(), document);
		modelMapper.map(document, project);
	}
	
	public Set<Project> findAllByOwner() {
		Set<com.nowellpoint.api.model.document.Project> documents = findAllByOwner(getSubject());
		Set<Project> resources = modelMapper.map(documents, new TypeToken<HashSet<Project>>() {}.getType());
		return resources;
	}
	
	public void deleteServiceProvider(Project project) {
		com.nowellpoint.api.model.document.Project document = modelMapper.map(project, com.nowellpoint.api.model.document.Project.class);
		delete(getSubject(), document);
	}
}