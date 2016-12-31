package com.nowellpoint.client.model.sforce;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Field {
	
	private Boolean autonumber;
	
	private String type;
	
	private Integer byteLength;

	private Boolean calculated;
	
	private Boolean caseSensitive;
	
	private Boolean createable;
	
	private Boolean custom;
	
	private Boolean defaultedOnCreate;
	
	private Integer digits;
	
	private String label;
	
	private Integer length;
	
	private String name;
	
	private Boolean nameField;
	
	private Boolean nillable;
	
	private Boolean unique;
	
	private Boolean updateable;
	
	public Field() {
		
	}

	public Boolean getAutonumber() {
		return autonumber;
	}

	public void setAutonumber(Boolean autonumber) {
		this.autonumber = autonumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getByteLength() {
		return byteLength;
	}

	public void setByteLength(Integer byteLength) {
		this.byteLength = byteLength;
	}

	public Boolean getCalculated() {
		return calculated;
	}

	public void setCalculated(Boolean calculated) {
		this.calculated = calculated;
	}

	public Boolean getCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(Boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
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

	public Boolean getDefaultedOnCreate() {
		return defaultedOnCreate;
	}

	public void setDefaultedOnCreate(Boolean defaultedOnCreate) {
		this.defaultedOnCreate = defaultedOnCreate;
	}

	public Integer getDigits() {
		return digits;
	}

	public void setDigits(Integer digits) {
		this.digits = digits;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getNameField() {
		return nameField;
	}

	public void setNameField(Boolean nameField) {
		this.nameField = nameField;
	}

	public Boolean getNillable() {
		return nillable;
	}

	public void setNillable(Boolean nillable) {
		this.nillable = nillable;
	}

	public Boolean getUnique() {
		return unique;
	}

	public void setUnique(Boolean unique) {
		this.unique = unique;
	}

	public Boolean getUpdateable() {
		return updateable;
	}

	public void setUpdateable(Boolean updateable) {
		this.updateable = updateable;
	}
}