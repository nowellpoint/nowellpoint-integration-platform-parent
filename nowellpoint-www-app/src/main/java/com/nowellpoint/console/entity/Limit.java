package com.nowellpoint.console.entity;

import java.io.Serializable;

public class Limit implements Serializable {
	private static final long serialVersionUID = 8845240136479794409L;
	private Integer max;
	private Integer available;
	
	public Limit() {
		
	}

	public Integer getMax() {
		return max;
	}

	public Integer getAvailable() {
		return available;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	public void setAvailable(Integer available) {
		this.available = available;
	}
}