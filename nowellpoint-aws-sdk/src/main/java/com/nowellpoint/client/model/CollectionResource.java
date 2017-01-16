package com.nowellpoint.client.model;

import java.util.List;

public interface CollectionResource<T extends Resource> extends Resource, Iterable<T> {
	int getSize();
	List<T> getItems();
}