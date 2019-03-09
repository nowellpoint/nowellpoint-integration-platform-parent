package com.nowellpoint.client.sforce.model;

import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;

public class QueryRes<T> {
	
	private Class<?> classType;
	
	//@SuppressWarnings("unchecked")
	public QueryRes() { 
		Type mySuperclass = getClass().getGenericSuperclass();
        Type tType = ((ParameterizedType)mySuperclass).getActualTypeArguments()[0];
        System.out.println(tType.getClass().getName());
		
		
        
	}

}
