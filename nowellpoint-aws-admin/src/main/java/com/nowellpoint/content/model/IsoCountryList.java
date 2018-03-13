package com.nowellpoint.content.model;

import java.util.List;

public class IsoCountryList {
	
	private List<IsoCountry> items = null;
	
	public IsoCountryList(List<IsoCountry> items) {
		this.items = items;
	}

	public List<IsoCountry> getItems() {
		return items;
	}
	
	public Integer getSize() {
		return items.size();
	}
}