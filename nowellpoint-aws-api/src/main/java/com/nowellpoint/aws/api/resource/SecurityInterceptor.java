package com.nowellpoint.aws.api.resource;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

@Provider
public class SecurityInterceptor implements ContainerRequestFilter {

	@Context
	private HttpServletRequest servletRequest;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		String authorization = requestContext.getHeaderString("Authorization");
		
		if (authorization != null && ! authorization.isEmpty()) {
			String token = authorization.replaceFirst("Bearer", "").trim();
			
			if (token == null || token.isEmpty()) {
				Response response = Response.status(Status.BAD_REQUEST).build();
				requestContext.abortWith(response);
				return;
			}
		}
		

			/**
			 * Check for null or empty authorization
			 */

			

			/**
			 * Decode the token into JWT
			 */

			Claims claims = null;
			try {
				claims = tokenProvider.decodeToken(token);
			} catch (ExpiredJwtException e) {
				requestContext.abortWith(INVALID_SESSION);
				return;
			} catch (SignatureException | MalformedJwtException | IllegalArgumentException e) {
				requestContext.abortWith(INVALID_WEB_TOKEN);
				return;
			}

			/**
			 * Lookup the user record
			 */

			User user = userRepository.find(claims.getSubject());

			/**
			 * validate that the user is active
			 */

			if (!user.getIsActive()) {
				requestContext.abortWith(INACTIVE_USER);
			}
	}
}