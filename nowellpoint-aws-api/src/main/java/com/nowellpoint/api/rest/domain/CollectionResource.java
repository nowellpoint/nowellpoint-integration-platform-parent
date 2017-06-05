package com.nowellpoint.api.rest.domain;

import java.util.Set;

public interface CollectionResource<T extends Resource> extends Resource, Iterable<T> {
	int getSize();
	Set<T> getItems();
}