package com.nowellpoint.aws.model.sforce;

import java.io.Serializable;

public class Notification implements Serializable {

	/**
	 * 
	 */

	private static final long serialVersionUID = -6669913219066631132L;

	/**
	 * 
	 */

	private String id;

	/**
	 * 
	 */

	private SObject sobject;

	public Notification() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SObject getSobject() {
		return sobject;
	}

	public void setSobject(SObject sobject) {
		this.sobject = sobject;
	}
}