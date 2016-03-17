package com.nowellpoint.aws.api.resource;

import java.io.IOException;
import java.security.Principal;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.tools.TokenParser;

@Provider
public class SecurityInterceptor implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		String authorization = requestContext.getHeaderString("Authorization");
		
		if (authorization != null && ! authorization.isEmpty()) {
			String bearerToken = authorization.replaceFirst("Bearer", "").trim();
			
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