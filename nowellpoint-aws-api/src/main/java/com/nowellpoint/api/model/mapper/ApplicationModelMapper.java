package com.nowellpoint.api.model.mapper;

import java.util.Set;

import org.modelmapper.TypeToken;

import com.nowellpoint.api.model.document.ApplicationDocument;
import com.nowellpoint.api.model.dto.Application;
import com.nowellpoint.api.model.dto.Id;

/**************************************************************************************************************************
 * 
 * 
 * @author jherson
 *
 * 
 *************************************************************************************************************************/

public class ApplicationModelMapper extends AbstractModelMapper<ApplicationDocument> {
	
	
	/**************************************************************************************************************************
	 * 
	 * 
	 * constructor
	 * 
	 * 
	 *************************************************************************************************************************/
	
	public ApplicationModelMapper() {
		super(ApplicationDocument.class);
	}
	
	public Application findApplication(Id id) {
		ApplicationDocument document = findById(id.getValue());
		return modelMapper.map(document, Application.class);
	}	
	
	public void createApplication(Application application) {
		ApplicationDocument document = modelMapper.map(application, ApplicationDocument.class);
		create(getSubject(), document);
		modelMapper.map(document, application);
	}
	
	public void updateApplication(Application application) {
		ApplicationDocument document = modelMapper.map(application, ApplicationDocument.class);
		replace(getSubject(), document);
		modelMapper.map(document, application);
	}
	
	public Set<Application> findAllByOwner() {
		Set<ApplicationDocument> documents = findAllByOwner(getSubject());
		Set<Application> resources = modelMapper.map(documents, new TypeToken<Set<Application>>() {}.getType());
		return resources;
	}
	
	public void deleteApplication(Application application) {
		ApplicationDocument document = modelMapper.map(application, ApplicationDocument.class);
		delete(getSubject(), document);
	}
}