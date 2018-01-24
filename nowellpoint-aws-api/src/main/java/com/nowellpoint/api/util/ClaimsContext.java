package com.nowellpoint.api.util;

import java.security.Principal;
import java.util.Date;

import javax.ws.rs.core.SecurityContext;

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
	
	public static void clear() {
		threadLocal.remove();
	}
	
	static class UserPrincipal implements Principal {
		
		private String id;
		private String issuer;
		private String audience;
		private String subject;
		private Date expiration;
		private Date issuedAt;
		private String scope;
		
		public UserPrincipal(Jws<Claims> claims) {
			id = claims.getBody().getId();
			issuer = claims.getBody().getIssuer();
			audience = claims.getBody().getAudience();
			subject = claims.getBody().getSubject();
			expiration = claims.getBody().getExpiration();
			issuedAt = claims.getBody().getIssuedAt();
			scope = claims.getBody().get("scope", String.class);
		}

		@Override
		public String getName() {
			return id;
		}

		public String getId() {
			return id;
		}

		public String getIssuer() {
			return issuer;
		}

		public String getAudience() {
			return audience;
		}

		public String getSubject() {
			return subject;
		}

		public Date getExpiration() {
			return expiration;
		}

		public Date getIssuedAt() {
			return issuedAt;
		}

		public String getScope() {
			return scope;
		}
	}
	
	static class UserPrincipalSecurityContext implements SecurityContext {
		
		private UserPrincipal principal;
		
		public UserPrincipalSecurityContext(Jws<Claims> claims) {
			this.principal = new UserPrincipal(claims);
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
			if (principal.getScope() == null) {
				return false;
			}
			return principal.getScope().equals(role);
		}
	}
}