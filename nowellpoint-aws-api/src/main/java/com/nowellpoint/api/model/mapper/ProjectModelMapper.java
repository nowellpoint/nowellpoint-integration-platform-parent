package com.nowellpoint.api.model.mapper;

import com.nowellpoint.api.model.domain.Project;

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
	
	protected Project findServiceProvider(String id) {
		com.nowellpoint.api.model.document.Project document = fetch(id);
		return modelMapper.map(document, Project.class);
	}	
	
	protected void createServiceProvider(Project project) {
		com.nowellpoint.api.model.document.Project document = modelMapper.map(project, com.nowellpoint.api.model.document.Project.class);
		create(document);
		hset(encode(getSubject()), document);
		modelMapper.map(document, project);
	}
	
	protected void updateServiceProvider(Project project) {
		com.nowellpoint.api.model.document.Project document = modelMapper.map(project, com.nowellpoint.api.model.document.Project.class);
		replace(document);
		hset(encode(getSubject()), document);
		modelMapper.map(document, project);
	}
	
	protected void deleteServiceProvider(Project project) {
		com.nowellpoint.api.model.document.Project document = modelMapper.map(project, com.nowellpoint.api.model.document.Project.class);
		hdel(encode(getSubject()), document);
	}
}