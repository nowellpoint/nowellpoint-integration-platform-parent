package com.nowellpoint.client.sforce.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.client.sforce.model.sobject.ActionOverride;
import com.nowellpoint.client.sforce.model.sobject.ChildRelationship;
import com.nowellpoint.client.sforce.model.sobject.Field;
import com.nowellpoint.client.sforce.model.sobject.NamedLayoutInfo;
import com.nowellpoint.client.sforce.model.sobject.RecordTypeInfo;
import com.nowellpoint.client.sforce.model.sobject.SupportedScope;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DescribeResult {
	
	private String networkScopeFieldName;
	
	private Boolean queryable;
	
	private Boolean replicateable;
	
	private Boolean retrieveable;
	
	private Boolean searchLayoutable;
	
	private Boolean searchable;
	
	private List<SupportedScope> supportedScopes;
	
	private Boolean triggerable;
	
	private Boolean undeletable;
	
	private Boolean updateable;
    
    private List<RecordTypeInfo> recordTypeInfos;
	
	private List<Field> fields;
	
	private List<ActionOverride> actionOverrides;
	
	private Boolean activateable;
	
	private List<ChildRelationship> childRelationships;
	
	private Boolean compactLayoutable;
	
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
	
	private Boolean listviewable;
	
	private Boolean lookupLayoutable;
	
	private Boolean mergeable;
	
	private Boolean mruEnabled;
	
	private String name;
	
	private List<NamedLayoutInfo> namedLayoutInfos;
	
	public DescribeResult() {
		
	}
	
	public List<ActionOverride> getActionOverrides() {
		return actionOverrides;
	}

	public void setActionOverrides(List<ActionOverride> actionOverrides) {
		this.actionOverrides = actionOverrides;
	}

	public List<ChildRelationship> getChildRelationships() {
		return childRelationships;
	}

	public void setChildRelationships(List<ChildRelationship> childRelationships) {
		this.childRelationships = childRelationships;
	}

	public Boolean getCompactLayoutable() {
		return compactLayoutable;
	}

	public void setCompactLayoutable(Boolean compactLayoutable) {
		this.compactLayoutable = compactLayoutable;
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

	public Boolean getListviewable() {
		return listviewable;
	}

	public void setListviewable(Boolean listviewable) {
		this.listviewable = listviewable;
	}

	public Boolean getLookupLayoutable() {
		return lookupLayoutable;
	}

	public void setLookupLayoutable(Boolean lookupLayoutable) {
		this.lookupLayoutable = lookupLayoutable;
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

	public List<NamedLayoutInfo> getNamedLayoutInfos() {
		return namedLayoutInfos;
	}

	public void setNamedLayoutInfos(List<NamedLayoutInfo> namedLayoutInfos) {
		this.namedLayoutInfos = namedLayoutInfos;
	}

	public String getNetworkScopeFieldName() {
		return networkScopeFieldName;
	}

	public void setNetworkScopeFieldName(String networkScopeFieldName) {
		this.networkScopeFieldName = networkScopeFieldName;
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

	public Boolean getSearchLayoutable() {
		return searchLayoutable;
	}

	public void setSearchLayoutable(Boolean searchLayoutable) {
		this.searchLayoutable = searchLayoutable;
	}

	public Boolean getSearchable() {
		return searchable;
	}

	public void setSearchable(Boolean searchable) {
		this.searchable = searchable;
	}

	public List<SupportedScope> getSupportedScopes() {
		return supportedScopes;
	}

	public void setSupportedScopes(List<SupportedScope> supportedScopes) {
		this.supportedScopes = supportedScopes;
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

	public List<RecordTypeInfo> getRecordTypeInfos() {
		return recordTypeInfos;
	}

	public void setRecordTypeInfos(List<RecordTypeInfo> recordTypeInfos) {
		this.recordTypeInfos = recordTypeInfos;
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

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
}