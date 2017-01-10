package com.nowellpoint.client.model;

public interface CollectionResource<T extends Resource> extends Resource, Iterable<T> {

}