package com.nowellpoint.sdk.salesforce.model.sobject;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionOverride implements Serializable {
	
	private static final long serialVersionUID = 282410126553466679L;
	
	private String actionName;
	
	private String comment;
	
	private String content;
	
	private String formFactor;
	
	private Boolean skipRecordTypeSelect;
	
	private String type;

	public ActionOverride() {
		
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFormFactor() {
		return formFactor;
	}

	public void setFormFactor(String formFactor) {
		this.formFactor = formFactor;
	}

	public Boolean getSkipRecordTypeSelect() {
		return skipRecordTypeSelect;
	}

	public void setSkipRecordTypeSelect(Boolean skipRecordTypeSelect) {
		this.skipRecordTypeSelect = skipRecordTypeSelect;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}