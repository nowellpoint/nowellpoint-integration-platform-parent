package com.nowellpoint.aws.api.resource;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import com.nowellpoint.aws.api.service.IdentityProviderService;
import com.nowellpoint.aws.api.util.UserContext;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Group;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

@Provider
public class SecurityContextFilter implements ContainerRequestFilter, ContainerResponseFilter {
	
	@Inject
	private IdentityProviderService identityProviderService;
	
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
				
			try {
				UserContext.setUserContext(bearerToken);
				requestContext.setSecurityContext(UserContext.getSecurityContext());
			} catch (MalformedJwtException e) {
				throw new NotAuthorizedException("Invalid token. Bearer token is invalid");
			} catch (SignatureException e) {
				throw new NotAuthorizedException("Invalid token. Signature is invalid");
			} catch (ExpiredJwtException e) {
				throw new NotAuthorizedException("Invalid token. Bearer token has expired");
			} catch (IllegalArgumentException e) {	
				throw new NotAuthorizedException("Invalid authorization. Bearer token is missing");
			}
			
			String subject = UserContext.getPrincipal().getName();
			
			if (method.isAnnotationPresent(RolesAllowed.class)) {
				
				Account account;
				try {
					account = identityProviderService.getAccountBySubject(subject);
				} catch (IOException e) {
					throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
				}
				
				Predicate<Group> isUserInRole = new Predicate<Group>() {
				    @Override
				    public boolean test(Group group) {
				    	String[] roles = method.getAnnotation(RolesAllowed.class).value();
				    	return Arrays.asList(roles)
				    			.stream()
				    			.filter(role -> role.equals(group.getName()))
				    			.findFirst()
				    			.isPresent();
				    }
				};
				
				Optional<Group> group = account
						.getGroups()
						.getItems()
						.stream()
						.filter(isUserInRole)
						.findFirst();
				
				if (! group.isPresent()) {
					throw new NotAuthorizedException("Unauthorized: your account is not authorized to access this resource");
				}
			}
		}
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		if (UserContext.getSecurityContext() != null) {
			UserContext.clear();
		}
	}
}