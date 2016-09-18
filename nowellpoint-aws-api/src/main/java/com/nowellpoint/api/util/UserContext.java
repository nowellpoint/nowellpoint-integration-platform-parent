package com.nowellpoint.api.util;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;

import javax.ws.rs.core.SecurityContext;

import com.nowellpoint.aws.model.admin.Properties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

public class UserContext {
	
	private static ThreadLocal<SecurityContext> threadLocal = new ThreadLocal<SecurityContext>();
	
	public static void setUserContext(String accessToken) {
		Jws<Claims> claims = parseClaims(accessToken); 
		
		String subject = claims.getBody().getSubject();
		
		@SuppressWarnings("unchecked")
		ArrayList<String> groups = (ArrayList<String>) claims.getBody().getOrDefault("groups", Collections.emptyList());
		
		SecurityContext securityContext = new UserPrincipalSecurityContext(subject, groups);
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
	
	public static Jws<Claims> parseClaims(String accessToken) {
		Jws<Claims> claims = Jwts.parser()
				.setSigningKey(Base64.getUrlEncoder().encodeToString(System.getProperty(Properties.STORMPATH_API_KEY_SECRET).getBytes()))
				.parseClaimsJws(accessToken); 
		
		return claims;
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
		private String authenticationScheme;
		private ArrayList<String> groups;
		
		public UserPrincipalSecurityContext(String subject, ArrayList<String> groups) {
			this.authenticationScheme = subject.split("-")[0];
			this.principal = new UserPrincipal(subject);
			this.groups = groups;
		}

		@Override
		public String getAuthenticationScheme() {
			return authenticationScheme;
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
			if (groups == null) {
				return false;
			}
			return groups.contains(role);
		}
	}
}