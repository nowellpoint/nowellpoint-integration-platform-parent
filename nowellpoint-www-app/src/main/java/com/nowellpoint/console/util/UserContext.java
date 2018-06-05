package com.nowellpoint.console.util;

import com.nowellpoint.console.model.Identity;

public class UserContext {
	
	public static final ThreadLocal<Identity> THREAD_LOCAL = new ThreadLocal<Identity>();
	
	public static Identity get() {
		return THREAD_LOCAL.get();
	}

	public static void set(Identity value) {
		THREAD_LOCAL.set(value);
	}
	
	public static void unset() {
		THREAD_LOCAL.remove();
	}
}