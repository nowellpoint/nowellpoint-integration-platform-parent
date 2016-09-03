package com.nowellpoint.api.document.service;

import java.util.Set;

import org.modelmapper.TypeToken;

import com.nowellpoint.api.dto.ApplicationDTO;
import com.nowellpoint.api.dto.Id;
import com.nowellpoint.api.model.Application;
import com.nowellpoint.api.service.AbstractModelMapper;

/**************************************************************************************************************************
 * 
 * 
 * @author jherson
 *
 * 
 *************************************************************************************************************************/

public class ApplicationDocumentService extends AbstractModelMapper<Application> {
	
	
	/**************************************************************************************************************************
	 * 
	 * 
	 * constructor
	 * 
	 * 
	 *************************************************************************************************************************/
	
	public ApplicationDocumentService() {
		super(Application.class);
	}
	
	public ApplicationDTO findApplication(Id id) {
		Application document = findById(id.getValue());
		return modelMapper.map(document, ApplicationDTO.class);
	}	
	
	public void createApplication(ApplicationDTO application) {
		Application document = modelMapper.map(application, Application.class);
		create(getSubject(), document);
		modelMapper.map(document, application);
	}
	
	public void updateApplication(ApplicationDTO application) {
		Application document = modelMapper.map(application, Application.class);
		replace(getSubject(), document);
		modelMapper.map(document, application);
	}
	
	public Set<ApplicationDTO> findAllByOwner() {
		Set<Application> documents = findAllByOwner(getSubject());
		Set<ApplicationDTO> resources = modelMapper.map(documents, new TypeToken<Set<ApplicationDTO>>() {}.getType());
		return resources;
	}
	
	public void deleteApplication(ApplicationDTO application) {
		Application document = modelMapper.map(application, Application.class);
		delete(document);
	}
}