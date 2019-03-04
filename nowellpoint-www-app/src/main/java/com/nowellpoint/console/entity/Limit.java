package com.nowellpoint.console.entity;

import java.io.Serializable;

public class Limit implements Serializable {
	private static final long serialVersionUID = 8845240136479794409L;
	private Long max;
	private Long remaining;
	
	public Limit() {
		
	}

	public Long getMax() {
		return max;
	}

	public Long getRemaining() {
		return remaining;
	}

	public void setMax(Long max) {
		this.max = max;
	}

	public void setRemaining(Long remaining) {
		this.remaining = remaining;
	}
}