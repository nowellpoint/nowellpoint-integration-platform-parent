package com.nowellpoint.aws.idp.model;

import java.io.Serializable;
import java.util.List;

public class SearchResult implements Serializable {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1989276718486847591L;

	private Integer size;
	
	private String href;
	
	private List<Account> items;
	
	public SearchResult() {
		
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public List<Account> getItems() {
		return items;
	}

	public void setItems(List<Account> items) {
		this.items = items;
	}
}