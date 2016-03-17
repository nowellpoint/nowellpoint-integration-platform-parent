package com.nowellpoint.aws.api.resource;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Optional;

import javax.annotation.security.PermitAll;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.tools.TokenParser;

@Provider
public class SecurityInterceptor implements ContainerRequestFilter {
	
	@Context
	ResourceInfo resourceInfo;

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
				
			if (bearerToken == null || bearerToken.isEmpty()) {
				Response response = Response.status(Status.BAD_REQUEST).build();
				requestContext.abortWith(response);
				return;
			}
				
			String subject = TokenParser.getSubject(System.getProperty(Properties.STORMPATH_API_KEY_SECRET), bearerToken);
				
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