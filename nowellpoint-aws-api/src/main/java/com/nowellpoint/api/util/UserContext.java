package com.nowellpoint.api.util;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.SecurityContext;

import com.nowellpoint.api.util.EnvUtil.Variable;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public class UserContext {
	
	private static ThreadLocal<SecurityContext> threadLocal = new ThreadLocal<SecurityContext>() {
	    @Override 
	    protected SecurityContext initialValue() {
	        return new UserPrincipalSecurityContext(EnvUtil.getValue(Variable.DEFAULT_SUBJECT), new ArrayList<String>());
	    }
	};
	
	public static void setUserContext(Jws<Claims> claims) {		
		String subject = claims.getBody().getSubject();
		
		@SuppressWarnings("unchecked")
		ArrayList<String> scope = (ArrayList<String>) claims.getBody().getOrDefault("scope", new ArrayList<String>());
		
		SecurityContext securityContext = new UserPrincipalSecurityContext(subject, scope);
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
		private List<String> scope;
		
		public UserPrincipalSecurityContext(String subject, List<String> scope) {
			this.principal = new UserPrincipal(subject);
			this.scope = scope;
		}

		@Override
		public String getAuthenticationScheme() {
			return "Bearer";
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
			if (scope == null) {
				return false;
			}
			return scope.contains(role);
		}
	}
}