package com.nowellpoint.aws.lambda.sforce.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Field {
	
    private Boolean unique;

    private Boolean nillable;

    private Boolean idLookup;

    private String mask;

    private String inlineHelpText;

    private String relationshipName;

    private String type;

    private String maskType;

    private String externalId;

    private Boolean calculated;

    private Boolean createable;

    private List<PicklistValue> picklistValues;

    private String name;

    private String length;

    private String defaultValue;

    private Boolean autoNumber;

    private Boolean custom;

    private String[] referenceTo;

    private String precision;

    private Boolean updateable;

    private String label;

    private Boolean dependentPicklist;

    private String queryByDistance;

    private String defaultValueFormula;

    private String encrypted;
    
    private Boolean mapped;

    public Field() {
    	
    }

	public Boolean getUnique() {
		return unique;
	}

	public void setUnique(Boolean unique) {
		this.unique = unique;
	}

	public Boolean getNillable() {
		return nillable;
	}

	public void setNillable(Boolean nillable) {
		this.nillable = nillable;
	}

	public Boolean getIdLookup() {
		return idLookup;
	}

	public void setIdLookup(Boolean idLookup) {
		this.idLookup = idLookup;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getInlineHelpText() {
		return inlineHelpText;
	}

	public void setInlineHelpText(String inlineHelpText) {
		this.inlineHelpText = inlineHelpText;
	}

	public String getRelationshipName() {
		return relationshipName;
	}

	public void setRelationshipName(String relationshipName) {
		this.relationshipName = relationshipName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMaskType() {
		return maskType;
	}

	public void setMaskType(String maskType) {
		this.maskType = maskType;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public Boolean getCalculated() {
		return calculated;
	}

	public void setCalculated(Boolean calculated) {
		this.calculated = calculated;
	}

	public Boolean getCreateable() {
		return createable;
	}

	public void setCreateable(Boolean createable) {
		this.createable = createable;
	}

	public List<PicklistValue> getPicklistValues() {
		return picklistValues;
	}

	public void setPicklistValues(List<PicklistValue> picklistValues) {
		this.picklistValues = picklistValues;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Boolean getAutoNumber() {
		return autoNumber;
	}

	public void setAutoNumber(Boolean autoNumber) {
		this.autoNumber = autoNumber;
	}

	public Boolean getCustom() {
		return custom;
	}

	public void setCustom(Boolean custom) {
		this.custom = custom;
	}

	public String[] getReferenceTo() {
		return referenceTo;
	}

	public void setReferenceTo(String[] referenceTo) {
		this.referenceTo = referenceTo;
	}

	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}

	public Boolean getUpdateable() {
		return updateable;
	}

	public void setUpdateable(Boolean updateable) {
		this.updateable = updateable;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Boolean getDependentPicklist() {
		return dependentPicklist;
	}

	public void setDependentPicklist(Boolean dependentPicklist) {
		this.dependentPicklist = dependentPicklist;
	}

	public String getQueryByDistance() {
		return queryByDistance;
	}

	public void setQueryByDistance(String queryByDistance) {
		this.queryByDistance = queryByDistance;
	}

	public String getDefaultValueFormula() {
		return defaultValueFormula;
	}

	public void setDefaultValueFormula(String defaultValueFormula) {
		this.defaultValueFormula = defaultValueFormula;
	}

	public String getEncrypted() {
		return encrypted;
	}

	public void setEncrypted(String encrypted) {
		this.encrypted = encrypted;
	}

	public Boolean getMapped() {
		return mapped;
	}

	public void setMapped(Boolean mapped) {
		this.mapped = mapped;
	}
}