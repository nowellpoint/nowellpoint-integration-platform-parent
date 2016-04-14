package com.nowellpoint.aws.api.dto;

import com.nowellpoint.aws.data.mongodb.Service;

public class ServiceProviderDTO extends AbstractDTO {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1297944724385795255L;
	
	private String type;
	
	private Boolean isActive;
	
	private String name;
	
	private String description;
	
	private String image;
	
	private String localeSidKey;
	
	private String languageLocaleKey;
	
	private Service service;

	public ServiceProviderDTO() {
		
	}
	
	public ServiceProviderDTO(String id) {
		setId(id);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	public String getLocaleSidKey() {
		return localeSidKey;
	}

	public void setLocaleSidKey(String localeSidKey) {
		this.localeSidKey = localeSidKey;
	}

	public String getLanguageLocaleKey() {
		return languageLocaleKey;
	}

	public void setLanguageLocaleKey(String languageLocaleKey) {
		this.languageLocaleKey = languageLocaleKey;
	}
}