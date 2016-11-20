package com.nowellpoint.api.model.domain.idp;

import java.io.Serializable;
import java.util.List;


public class SearchResult implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1989276718486847591L;

	private Integer size;
	
	private Integer offset;
	
	private Integer limit;
	
	private String href;
	
	private List<User> items;
	
	public SearchResult() {
		
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
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

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public List<User> getItems() {
		return items;
	}

	public void setItems(List<User> items) {
		this.items = items;
	}
}