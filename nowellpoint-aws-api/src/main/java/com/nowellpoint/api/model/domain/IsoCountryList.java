package com.nowellpoint.api.model.domain;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.modelmapper.ModelMapper;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;

public class IsoCountryList extends AbstractCollectionResource<IsoCountry> {
	
	protected final ModelMapper modelMapper = new ModelMapper();
	private Set<IsoCountry> items = new HashSet<IsoCountry>();
	private Meta meta;
	
	public IsoCountryList(FindIterable<com.nowellpoint.api.model.document.IsoCountry> documents) {
		
		documents.forEach(new Block<com.nowellpoint.api.model.document.IsoCountry>() {
			@Override
			public void apply(final com.nowellpoint.api.model.document.IsoCountry document) {
				items.add(modelMapper.map(document, IsoCountry.class));
		    }
		});
		
		meta = new Meta();
	}

	@Override
	public Meta getMeta() {
		return meta;
	}

	@Override
	public Iterator<IsoCountry> iterator() {
		return null;
	}

	@Override
	public int getSize() {
		return items.size();
	}

	@Override
	public Set<IsoCountry> getItems() {
		return items;
	}
}