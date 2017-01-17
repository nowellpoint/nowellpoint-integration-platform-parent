package com.nowellpoint.api.model.mapper;

import com.nowellpoint.api.model.domain.Application;

/**
 * 
 * 
 * @author jherson
 *
 * 
 */

public class ApplicationModelMapper extends AbstractModelMapper<com.nowellpoint.api.model.document.Application> {
	
	
	/**
	 * 
	 * 
	 * constructor
	 * 
	 * 
	 */
	
	public ApplicationModelMapper() {
		super(com.nowellpoint.api.model.document.Application.class);
	}
	
	protected Application findApplication(String id) {
		com.nowellpoint.api.model.document.Application document = find(id);
		return modelMapper.map(document, Application.class);
	}	
	
	protected void createApplication(Application application) {
		com.nowellpoint.api.model.document.Application document = modelMapper.map(application, com.nowellpoint.api.model.document.Application.class);
		create(document);
		hset(encode(getSubject()), document);
		modelMapper.map(document, application);
	}
	
	protected void updateApplication(Application application) {
		com.nowellpoint.api.model.document.Application document = modelMapper.map(application, com.nowellpoint.api.model.document.Application.class);
		replace(document);
		hset(encode(getSubject()), document);
		modelMapper.map(document, application);
	}
	
	protected void deleteApplication(Application application) {
		com.nowellpoint.api.model.document.Application document = modelMapper.map(application, com.nowellpoint.api.model.document.Application.class);
		delete(document);
		hdel(encode(getSubject()), document);
	}
}