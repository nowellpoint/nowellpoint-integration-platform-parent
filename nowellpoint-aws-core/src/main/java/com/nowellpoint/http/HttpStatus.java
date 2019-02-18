package com.nowellpoint.http;

import java.util.HashMap;
import java.util.Map;

public enum HttpStatus {
	
	OK(200),
	CREATED(201),
	ACCEPTED(202),
	NO_CONTENT(204),
	NOT_MODIFIED(304),
	BAD_REQUEST(400),
	NOT_AUTHORIZED(401),
	FORBIDDEN(403),
	NOT_FOUND(404),
	INTERNAL_SERVER_ERROR(500),
	SERVICE_UNAVAILABLE(503);
	
	private static final Map<Integer, HttpStatus> lookup = new HashMap<Integer, HttpStatus>();
	
    static {
    	lookup.values().forEach(s -> {
    		lookup.put(s.getStatusCode(), s);
    	});
    }
	
	private int statusCode;
	
	private HttpStatus(int statusCode) { this.statusCode = statusCode; }
	
	public int getStatusCode() { return statusCode; }
	
	public static String getMessage(int statusCode) { return lookup.get(statusCode).name(); }
}