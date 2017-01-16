package com.nowellpoint.api.model.domain;

import java.util.Set;

public interface CollectionResource<T extends Resource> extends Resource, Iterable<T> {
	int getSize();
	Set<T> getItems();
}