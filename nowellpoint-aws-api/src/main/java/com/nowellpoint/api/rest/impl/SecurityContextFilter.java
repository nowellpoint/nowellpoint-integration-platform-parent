package com.nowellpoint.api.rest.impl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.api.service.TokenService;
import com.nowellpoint.api.util.ClaimsContext;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.aws.data.LogManager;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

@Provider
public class SecurityContextFilter implements ContainerRequestFilter, ContainerResponseFilter {
	
	private static final Logger LOG = Logger.getLogger(SecurityContextFilter.class);
	
	@Context
	private ResourceInfo resourceInfo;
	
	@Context
	private HttpServletRequest httpRequest;
	
	@Inject
	private TokenService tokenService;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		if (requestContext.getUriInfo().getPath().contains("/swagger")) {
			return;
		}
		
		Method method = resourceInfo.getResourceMethod();
		
		if (! method.isAnnotationPresent(PermitAll.class)) {
			
			Optional<String> authorization = Optional.ofNullable(requestContext.getHeaderString("Authorization"));
			
			if (! authorization.isPresent()) {
				throw new BadRequestException("Missing Authorization Header");
			}
			
			if (! authorization.get().startsWith("Bearer ")) {
				throw new BadRequestException("Invalid authorization. Should be of type Bearer");
			}
			
			String accessToken = authorization.get().replaceFirst("Bearer", "").trim();
				
			try {
				Jws<Claims> claims = tokenService.verifyToken(accessToken);
				ClaimsContext.setClaims(claims);
				UserContext.setUserContext(claims);
				requestContext.setSecurityContext(UserContext.getSecurityContext());
			} catch (MalformedJwtException e) {
				LOG.error(e);
				throw new NotAuthorizedException("Invalid token. Bearer token is invalid");
			} catch (SignatureException e) {
				LOG.error(e);
				throw new NotAuthorizedException("Invalid token. Signature is invalid");
			} catch (ExpiredJwtException e) {
				LOG.error(e);
				throw new NotAuthorizedException("Invalid token. Bearer token has expired");
			} catch (IllegalArgumentException e) {	
				LOG.error(e);
				throw new NotAuthorizedException("Invalid authorization. Bearer token is missing");
			}
			
			if (method.isAnnotationPresent(RolesAllowed.class)) {
				
				String[] roles = method.getAnnotation(RolesAllowed.class).value();
				
				Arrays.asList(roles)
					.stream()
					.filter(role -> UserContext.getSecurityContext().isUserInRole(role))
					.findFirst()
					.orElseThrow(() -> new NotAuthorizedException("Unauthorized: your account is not authorized to access this resource"));
			}
		}
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		
		final String subject = UserContext.getSecurityContext() != null ? UserContext.getSecurityContext().getUserPrincipal().getName() : null;
		final String path = httpRequest.getPathInfo().concat(httpRequest.getQueryString() != null ? "?".concat(httpRequest.getQueryString()) : "");
		final Integer statusCode = responseContext.getStatus();
		final String statusInfo = responseContext.getStatusInfo().toString();
		final String requestMethod = requestContext.getMethod();
		
		ObjectNode node = JsonNodeFactory.instance.objectNode()
				.put("hostname", httpRequest.getLocalAddr())
				.put("subject", subject)
				.put("date", System.currentTimeMillis())
				.put("method", requestMethod)
				.put("path", path)
				.put("statusCode", statusCode)
				.put("statusInfo", statusInfo);
		
		LogManager.writeLogEntry("api", node.toString());
		
		if (UserContext.getSecurityContext() != null) {
			UserContext.clear();
		}
	}
}