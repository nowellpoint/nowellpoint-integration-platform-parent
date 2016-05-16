package com.nowellpoint.aws.idp.model;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Groups implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4028402953654987070L;
	
	@JsonProperty(value="href")
	private String href;
	
	@JsonProperty(value="offset")
	private Integer offset;
	
	@JsonProperty(value="limit")
	private Integer limit;
	
	@JsonProperty(value="size")
	private Integer size;
	
	@JsonProperty(value="items")
	private Set<Group> items;
	
	public Groups() {
		
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Set<Group> getItems() {
		return items;
	}

	public void setItems(Set<Group> items) {
		this.items = items;
	}
}