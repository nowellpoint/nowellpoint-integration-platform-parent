package com.nowellpoint.sdk.salesforce.model.sobject;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChildRelationship implements Serializable {
	
	private static final long serialVersionUID = -7988759722991600823L;

	/**

      "junctionIdListNames": [],
      "junctionReferenceTo": [],

	 */
	
    private String field;

    private Boolean restrictedDelete;

    private Boolean cascadeDelete;

    private String childSObject;

   // private String junctionIdListName;

    private String relationshipName;

    private Boolean deprecatedAndHidden;

   // private String[] junctionReferenceTo;
    
    public ChildRelationship() {
    	
    }

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Boolean getRestrictedDelete() {
		return restrictedDelete;
	}

	public void setRestrictedDelete(Boolean restrictedDelete) {
		this.restrictedDelete = restrictedDelete;
	}

	public Boolean getCascadeDelete() {
		return cascadeDelete;
	}

	public void setCascadeDelete(Boolean cascadeDelete) {
		this.cascadeDelete = cascadeDelete;
	}

	public String getChildSObject() {
		return childSObject;
	}

	public void setChildSObject(String childSObject) {
		this.childSObject = childSObject;
	}

	public String getRelationshipName() {
		return relationshipName;
	}

	public void setRelationshipName(String relationshipName) {
		this.relationshipName = relationshipName;
	}

	public Boolean getDeprecatedAndHidden() {
		return deprecatedAndHidden;
	}

	public void setDeprecatedAndHidden(Boolean deprecatedAndHidden) {
		this.deprecatedAndHidden = deprecatedAndHidden;
	}
}