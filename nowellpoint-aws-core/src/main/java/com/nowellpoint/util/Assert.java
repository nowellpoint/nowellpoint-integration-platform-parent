package com.nowellpoint.util;

public class Assert {

	private Assert() {
		
	}
	
	public static void assertNotNull(Object value, String message) {
		if (value == null) {
			throw new IllegalArgumentException(message);
		}
	}
	
	public static void assertNotNullOrEmpty(String value, String message) {
		if (value == null || value.isEmpty()) {
			throw new IllegalArgumentException(message);
		}
	}
	
	public static Boolean isNullOrEmpty(String value) {
		if (value == null || value.isEmpty()) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
	
	public static Boolean isNotNullOrEmpty(String value) {
		if (value == null || value.isEmpty()) {
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}
	
	public static Boolean isNull(Object value) {
		if (value == null) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
	
	public static Boolean isEmpty(String value) {
		if (value.isEmpty()) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
	
	public static Boolean isNotNull(Object value) {
		if (value == null) {
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}
	
	public static Boolean isEqual(String value1, String value2) {
		if (value1 == null && value2 == null) {
			return Boolean.TRUE;
		} else if (value1 == null && value2 != null) {
			return Boolean.FALSE;
		} else if (value1.equals(value2)) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
	
	public static Boolean isNotEqual(String value1, String value2) {
		if (value1 == null && value2 == null) {
			return Boolean.FALSE;
		} else if (value1 == null && value2 != null) {
			return Boolean.TRUE;
		} else if (value1.equals(value2)) {
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}
	
	public static Boolean isNumber(String value) {
		try {
			Integer.valueOf(value);
			return Boolean.TRUE;
		} catch (NumberFormatException e) {
			return Boolean.FALSE;
		}
	}
}