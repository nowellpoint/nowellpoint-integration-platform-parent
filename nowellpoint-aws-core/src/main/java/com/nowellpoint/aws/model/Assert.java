package com.nowellpoint.aws.model;

import java.util.Optional;

public class Assert {

	protected Assert() {
		
	}
	
	public static boolean assertNotNull(Object object) {
		return Optional.ofNullable(object).isPresent();
	}
}