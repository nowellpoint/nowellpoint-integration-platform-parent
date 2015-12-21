package com.nowellpoint.aws.api.util;

import java.util.Base64;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;

public class HttpServletRequestUtil {

	public static String getBearerToken(HttpServletRequest servletRequest) throws BadRequestException {
		
		//
		// get the authorization header from HttpServletRequest
		//
		
		String authorization = getAuthorizationHeader(servletRequest);
		
		//
		// ensure that the authorization header has a bearer token
		//
		
		if (! authorization.startsWith("Bearer ")) {
			throw new BadRequestException("Invalid authorization. Should be of type Bearer");
		}
		
		//
		// parse the authorization header to get the bearer token
		//
		
		String bearerToken = new String(authorization.replace("Bearer ", ""));
		
		//
		// return the bearer token
		//
		
		return bearerToken;
	}
	
	public static String getBasicToken(HttpServletRequest servletRequest) throws BadRequestException {
		
		//
		// get the authorization header from HttpServletRequest
		//
		
		String authorization = getAuthorizationHeader(servletRequest);
		
		//
		// ensure that the authorization header has a basic token
		//
		
		if (! authorization.startsWith("Basic ")) {
			throw new BadRequestException("Invalid authorization. Should be of type Basic");
		}
		
		//
		// parse the authorization token to get the base64 basic token
		//
		
		String basicToken = new String(Base64.getDecoder().decode(authorization.replace("Basic ", "")));
		
		//
		// return the basic token
		//
		
		return basicToken;
	}
	
	private static String getAuthorizationHeader(HttpServletRequest servletRequest) throws BadRequestException {
		
		//
		// extract the authorization header from the HttpServletRequest
		//
		
		Optional<String> authorization = Optional.ofNullable(servletRequest.getHeader("Authorization"));
		
		//
		// ensure the request has an Authorization header parameter
		//
		
		if (! authorization.isPresent()) {
			throw new BadRequestException("Missing Authorization Header");
		}
		
		return authorization.get();
	}
}