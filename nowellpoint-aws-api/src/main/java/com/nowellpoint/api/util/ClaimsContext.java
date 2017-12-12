package com.nowellpoint.api.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public class ClaimsContext {
	
	private static ThreadLocal<Jws<Claims>> threadLocal = new ThreadLocal<Jws<Claims>>();
	
	public static void setClaims(Jws<Claims> claims) {
		threadLocal.set(claims);
	}
	
	public static Jws<Claims> getClaims() {
		return threadLocal.get();
	}
}