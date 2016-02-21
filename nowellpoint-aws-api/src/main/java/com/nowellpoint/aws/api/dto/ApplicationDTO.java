package com.nowellpoint.aws.api.dto;

public class ApplicationDTO extends AbstractDTO {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -365479731136738071L;
	
	private Boolean isSandbox;
	
	private String name;
	
	private String type;
	
	public ApplicationDTO() {
		
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
}