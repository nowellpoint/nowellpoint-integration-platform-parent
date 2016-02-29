package com.nowellpoint.aws.api.dto;

public class ApplicationDTO extends AbstractDTO {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -365479731136738071L;
	
	private Boolean isSandbox;
	
	private String name;
	
	private String type;
	
	private String key;
	
	private String instanceName;
	
	private String url;
	
	public ApplicationDTO() {
		
	}
	
	public ApplicationDTO(String id) {
		setId(id);
	}

	public Boolean getIsSandbox() {
		return isSandbox;
	}

	public void setIsSandbox(Boolean isSandbox) {
		this.isSandbox = isSandbox;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}