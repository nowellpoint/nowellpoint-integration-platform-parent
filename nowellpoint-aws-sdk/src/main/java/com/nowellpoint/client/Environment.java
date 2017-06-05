package com.nowellpoint.client;

public class Environment {
	
    public static final Environment PRODUCTION = new Environment("https://api.nowellpoint.com/rest", "production");

    public static final Environment SANDBOX = new Environment("https://localhost:5100/rest", "sandbox");
    
    private String environmentUrl;
    
    private String environmentName;
    
    public Environment(String environmentUrl, String environmentName) {
    	this.environmentUrl = environmentUrl;
    	this.environmentName = environmentName;
    }
    
    public static Environment parseEnvironment(String environment) {
    	if (environment.equalsIgnoreCase("sandbox")) {
    		return SANDBOX;
    	} else if (environment.equalsIgnoreCase("production")) {
    		return PRODUCTION;
    	} else {
            throw new IllegalArgumentException("Unknown environment: " + environment);
        }
    }
    
    public String getEnvironmentUrl() {
    	return environmentUrl;
    }
    
    public String getEnvironmentName() {
    	return environmentName;
    }
}