package com.nowellpoint.aws.api.util;

import java.security.Principal;

public class Subject implements Principal {
	
	private String name;
	
	public Subject(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}