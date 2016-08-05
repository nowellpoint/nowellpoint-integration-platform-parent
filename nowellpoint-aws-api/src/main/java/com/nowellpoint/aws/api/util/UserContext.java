package com.nowellpoint.aws.api.util;

import java.security.Principal;

public class UserContext {
	
	private static ThreadLocal<Principal> threadLocal = new ThreadLocal<Principal>();
	
	public static void setUserPrincipal(Principal principal) {
		threadLocal.set(principal);
	}

	public static Principal getUserPrincipal() {
		return threadLocal.get();
	}
}