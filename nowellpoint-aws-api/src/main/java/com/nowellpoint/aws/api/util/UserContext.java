package com.nowellpoint.aws.api.util;

import java.security.Principal;
import java.util.Base64;

import javax.ws.rs.core.SecurityContext;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class UserContext {
	
	private static ThreadLocal<SecurityContext> threadLocal = new ThreadLocal<SecurityContext>();
	
	public static void setUserContext(String accessToken) {
		Jws<Claims> claims = Jwts.parser()
				.setSigningKey(Base64.getUrlEncoder().encodeToString(System.getProperty("stormpath.api.key.secret").getBytes()))
				.parseClaimsJws(accessToken); 
		
		String subject = claims.getBody().getSubject();
		
		Principal principal = new UserPrincipal(subject); 
		SecurityContext securityContext = new UserPrincipalSecurityContext(principal);
		threadLocal.set(securityContext);
	}

	public static Principal getPrincipal() {
		return threadLocal.get().getUserPrincipal();
	}
	
	public static SecurityContext getSecurityContext() {
		return threadLocal.get();
	}
	
	public static void clear() {
		threadLocal.remove();
	}
	
	static class UserPrincipal implements Principal {
		
		private String name;
		
		public UserPrincipal(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}
	
	static class UserPrincipalSecurityContext implements SecurityContext {
		
		private Principal principal;
		
		public UserPrincipalSecurityContext(Principal principal) {
			this.principal = principal;
		}

		@Override
		public String getAuthenticationScheme() {
			return null;
		}

		@Override
		public Principal getUserPrincipal() {
			return principal;
		}

		@Override
		public boolean isSecure() {
			return false;
		}

		@Override
		public boolean isUserInRole(String role) {
			return false;
		}
	}
}