package com.nowellpoint.aws.api.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
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

import org.jboss.logging.Logger;

import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.aws.api.service.IdentityProviderService;
import com.nowellpoint.aws.api.util.UserContext;
import com.nowellpoint.aws.idp.model.Account;
import com.nowellpoint.aws.idp.model.Group;
import com.nowellpoint.aws.model.admin.Properties;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

@Provider
public class SecurityContextFilter implements ContainerRequestFilter, ContainerResponseFilter {
	
	private static final Logger LOGGER = Logger.getLogger(SecurityContextFilter.class);
	
	@Inject
	private IdentityProviderService identityProviderService;
	
	@Context
	private ResourceInfo resourceInfo;
	
	@Context
	private HttpServletRequest httpRequest;

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
				    	System.out.println("role: " + roles[0]);
				    	return Arrays.asList(roles)
				    			.stream()
				    			.filter(role -> role.equals(group.getName()))
				    			.findFirst()
				    			.isPresent();
				    }
				};
				
				System.out.println(account.getGroups() == null);
				
//				Optional<Group> group = account
//						.getGroups()
//						.getItems()
//						.stream()
//						.filter(isUserInRole)
//						.findFirst();
//				
//				if (! group.isPresent()) {
//					throw new NotAuthorizedException("Unauthorized: your account is not authorized to access this resource");
//				}
			}
		}
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		
		final String subject = UserContext.getSecurityContext() != null ? UserContext.getSecurityContext().getUserPrincipal().getName() : null;
		final String address = httpRequest.getLocalName();
		final String path = httpRequest.getPathInfo().concat(httpRequest.getQueryString() != null ? "?".concat(httpRequest.getQueryString()) : "");
		final Integer statusCode = responseContext.getStatus();
		final String statusInfo = responseContext.getStatusInfo().toString();
		final String requestMethod = requestContext.getMethod();
		
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				
				ObjectNode node = new ObjectMapper().createObjectNode()
						.put("subject", subject)
						.put("date", System.currentTimeMillis())
						.put("address", address)
						.put("method", requestMethod)
						.put("path", path)
						.put("statusCode", statusCode)
						.put("statusInfo", statusInfo);
				
				HttpURLConnection connection;
				try {
					connection = (HttpURLConnection) new URL(System.getProperty(Properties.LOGGLY_API_ENDPOINT)
							.concat("/")
							.concat(System.getProperty(Properties.LOGGLY_API_KEY))
							.concat("/tag")
							.concat("/api")
							.concat("/")
					).openConnection();
					
					connection.setRequestMethod("GET");
					connection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
					connection.setDoOutput(true);
					
					byte[] outputInBytes = node.toString().getBytes("UTF-8");
					OutputStream os = connection.getOutputStream();
					os.write( outputInBytes );    
					os.close();
					
					connection.connect();
					
					if (connection.getResponseCode() != 200) {
						LOGGER.error(IOUtils.toString(connection.getErrorStream()));
					}
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		});
		
		if (UserContext.getSecurityContext() != null) {
			UserContext.clear();
		}
	}
}