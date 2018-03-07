package com.nowellpoint.content.model;

import java.util.Collections;
import java.util.List;

import com.amazonaws.services.s3.model.S3Object;

public class IsoCountryList extends S3Entity<IsoCountry> {
	
	private List<IsoCountry> items = Collections.emptyList();
	
	public IsoCountryList(S3Object object) {
		items = getCollection(IsoCountry.class, object);
	}

	public List<IsoCountry> getItems() {
		return items;
	}
	
	public Integer getSize() {
		return items.size();
	}
}