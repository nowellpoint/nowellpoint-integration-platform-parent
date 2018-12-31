package com.nowellpoint.sdk.salesforce.model.sobject;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SObject implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -4468699493936717073L;
	
	private Boolean activateable;
	
	private Boolean createable;
	
	private Boolean custom;
	
	private Boolean customSetting;
	
	private Boolean deletable;
	
	private Boolean deprecatedAndHidden;
	
	private Boolean feedEnabled;
	
	private String keyPrefix;
	
	private String label;
	
	private String labelPlural;
	
	private Boolean layoutable;
	
	private Boolean mergeable;
	
	private Boolean mruEnabled;
	
	private String name;
	
	private Boolean queryable;
	
	private Boolean replicateable;
	
	private Boolean retrieveable;
	
	private Boolean searchable;
	
	private Boolean triggerable;
	
	private Boolean undeletable;
	
	private Boolean updateable;
	
	private Urls urls;

	public SObject() {
		
	}

	public Boolean getActivateable() {
		return activateable;
	}

	public void setActivateable(Boolean activateable) {
		this.activateable = activateable;
	}

	public Boolean getCreateable() {
		return createable;
	}

	public void setCreateable(Boolean createable) {
		this.createable = createable;
	}

	public Boolean getCustom() {
		return custom;
	}

	public void setCustom(Boolean custom) {
		this.custom = custom;
	}

	public Boolean getCustomSetting() {
		return customSetting;
	}

	public void setCustomSetting(Boolean customSetting) {
		this.customSetting = customSetting;
	}

	public Boolean getDeletable() {
		return deletable;
	}

	public void setDeletable(Boolean deletable) {
		this.deletable = deletable;
	}

	public Boolean getDeprecatedAndHidden() {
		return deprecatedAndHidden;
	}

	public void setDeprecatedAndHidden(Boolean deprecatedAndHidden) {
		this.deprecatedAndHidden = deprecatedAndHidden;
	}

	public Boolean getFeedEnabled() {
		return feedEnabled;
	}

	public void setFeedEnabled(Boolean feedEnabled) {
		this.feedEnabled = feedEnabled;
	}

	public String getKeyPrefix() {
		return keyPrefix;
	}

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabelPlural() {
		return labelPlural;
	}

	public void setLabelPlural(String labelPlural) {
		this.labelPlural = labelPlural;
	}

	public Boolean getLayoutable() {
		return layoutable;
	}

	public void setLayoutable(Boolean layoutable) {
		this.layoutable = layoutable;
	}

	public Boolean getMergeable() {
		return mergeable;
	}

	public void setMergeable(Boolean mergeable) {
		this.mergeable = mergeable;
	}

	public Boolean getMruEnabled() {
		return mruEnabled;
	}

	public void setMruEnabled(Boolean mruEnabled) {
		this.mruEnabled = mruEnabled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getQueryable() {
		return queryable;
	}

	public void setQueryable(Boolean queryable) {
		this.queryable = queryable;
	}

	public Boolean getReplicateable() {
		return replicateable;
	}

	public void setReplicateable(Boolean replicateable) {
		this.replicateable = replicateable;
	}

	public Boolean getRetrieveable() {
		return retrieveable;
	}

	public void setRetrieveable(Boolean retrieveable) {
		this.retrieveable = retrieveable;
	}

	public Boolean getSearchable() {
		return searchable;
	}

	public void setSearchable(Boolean searchable) {
		this.searchable = searchable;
	}

	public Boolean getTriggerable() {
		return triggerable;
	}

	public void setTriggerable(Boolean triggerable) {
		this.triggerable = triggerable;
	}

	public Boolean getUndeletable() {
		return undeletable;
	}

	public void setUndeletable(Boolean undeletable) {
		this.undeletable = undeletable;
	}

	public Boolean getUpdateable() {
		return updateable;
	}

	public void setUpdateable(Boolean updateable) {
		this.updateable = updateable;
	}

	public Urls getUrls() {
		return urls;
	}

	public void setUrls(Urls urls) {
		this.urls = urls;
	}
}