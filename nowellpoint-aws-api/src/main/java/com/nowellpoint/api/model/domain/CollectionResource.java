package com.nowellpoint.api.model.domain;

public interface CollectionResource<T extends Resource> extends Resource, Iterable<T> {

}