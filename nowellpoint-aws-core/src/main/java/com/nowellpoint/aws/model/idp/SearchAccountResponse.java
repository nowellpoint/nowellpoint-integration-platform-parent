package com.nowellpoint.aws.model.idp;

import java.util.List;

import com.nowellpoint.aws.model.AbstractLambdaResponse;

public class SearchAccountResponse extends AbstractLambdaResponse {
	
	private static final long serialVersionUID = 7454196046925853087L;
	private String href;
	private Integer size;
	private List<Account> items;
	
	public SearchAccountResponse() {
		
	}
	
	public String getHref() {
		return href;
	}
	
	public void setHref(String href) {
		this.href = href;
	}
	
	public Integer getSize() {
		return size;
	}
	
	public void setSize(Integer size) {
		this.size = size;
	}
	
	public List<Account> getItems() {
		return items;
	}
	
	public void setItems(List<Account> items) {
		this.items = items;
	}
}