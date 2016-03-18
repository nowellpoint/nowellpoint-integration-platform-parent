package com.nowellpoint.aws.api.resource;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Optional;

import javax.annotation.security.PermitAll;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.tools.TokenParser;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

@Provider
public class SecurityInterceptor implements ContainerRequestFilter {
	
	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		Method method = resourceInfo.getResourceMethod();
		
		if (! method.isAnnotationPresent(PermitAll.class)) {
			
			Optional<String> authorization = Optional.ofNullable(requestContext.getHeaderString("Authorization"));
			
			if (! authorization.isPresent()) {
				throw new BadRequestException("Missing Authorization Header");
			}
			
			if (! authorization.get().startsWith("Bearer ")) {
				throw new BadRequestException("Invalid authorization. Should be of type Bearer");
			}
			
			String bearerToken = authorization.get().replaceFirst("Bearer", "").trim();
				
			String subject = null;
			
			try {
				String key = System.getProperty(Properties.STORMPATH_API_KEY_SECRET);
				subject = TokenParser.parseToken(key, bearerToken);
			} catch (MalformedJwtException e) {
				throw new NotAuthorizedException("Invalid token. Bearer token is invalid");
			} catch (SignatureException e) {
				throw new NotAuthorizedException("Invalid token. Signature is invalid");
			} catch (ExpiredJwtException e) {
				throw new NotAuthorizedException("Invalid token. Bearer token has expired");
			} catch (IllegalArgumentException e) {	
				throw new NotAuthorizedException("Invalid authorization. Bearer token is missing");
			}
				
			UserPrincipal user = new UserPrincipal(subject);
				
			requestContext.setSecurityContext(new UserPrincipalSecurityContext(user));
		}
	}
	
	class UserPrincipal implements Principal {
		
		private String name;
		
		public UserPrincipal(String subject) {
			this.name = subject;
		}

		@Override
		public String getName() {
			return name;
		}
	}
	
	class UserPrincipalSecurityContext implements SecurityContext {
		
		private UserPrincipal user;
		
		public UserPrincipalSecurityContext(UserPrincipal user) {
			this.user = user;
		}

		@Override
		public String getAuthenticationScheme() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Principal getUserPrincipal() {
			return user;
		}

		@Override
		public boolean isSecure() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isUserInRole(String arg0) {
			// TODO Auto-generated method stub
			return false;
		}
	}
}