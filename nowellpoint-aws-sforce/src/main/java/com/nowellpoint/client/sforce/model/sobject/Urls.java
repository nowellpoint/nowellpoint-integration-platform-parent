package com.nowellpoint.client.sforce.model.sobject;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Urls implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1592500440339469657L;
	
	private String compactLayouts;
	
	private String rowTemplate;
	
	private String approvalLayouts;
	
	private String uiDetailTemplate;
	
	private String uiEditTemplate;
	
	private String uiNewRecord;
	
	private String defaultValues;
	
	private String listviews;
	
	private String describe;
	
	private String quickActions;
	
	private String layouts;
	
	private String sobject;
	
	public Urls() {
		
	}

	public String getCompactLayouts() {
		return compactLayouts;
	}

	public void setCompactLayouts(String compactLayouts) {
		this.compactLayouts = compactLayouts;
	}

	public String getRowTemplate() {
		return rowTemplate;
	}

	public void setRowTemplate(String rowTemplate) {
		this.rowTemplate = rowTemplate;
	}

	public String getApprovalLayouts() {
		return approvalLayouts;
	}

	public void setApprovalLayouts(String approvalLayouts) {
		this.approvalLayouts = approvalLayouts;
	}

	public String getUiDetailTemplate() {
		return uiDetailTemplate;
	}

	public void setUiDetailTemplate(String uiDetailTemplate) {
		this.uiDetailTemplate = uiDetailTemplate;
	}

	public String getUiEditTemplate() {
		return uiEditTemplate;
	}

	public void setUiEditTemplate(String uiEditTemplate) {
		this.uiEditTemplate = uiEditTemplate;
	}

	public String getUiNewRecord() {
		return uiNewRecord;
	}

	public void setUiNewRecord(String uiNewRecord) {
		this.uiNewRecord = uiNewRecord;
	}

	public String getDefaultValues() {
		return defaultValues;
	}

	public void setDefaultValues(String defaultValues) {
		this.defaultValues = defaultValues;
	}

	public String getListviews() {
		return listviews;
	}

	public void setListviews(String listviews) {
		this.listviews = listviews;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getQuickActions() {
		return quickActions;
	}

	public void setQuickActions(String quickActions) {
		this.quickActions = quickActions;
	}

	public String getLayouts() {
		return layouts;
	}

	public void setLayouts(String layouts) {
		this.layouts = layouts;
	}

	public String getSobject() {
		return sobject;
	}

	public void setSobject(String sobject) {
		this.sobject = sobject;
	}
}