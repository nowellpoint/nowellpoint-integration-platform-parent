package com.nowellpoint.aws.app.api;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * @author John Herson
 */

@ApplicationPath("/rest")
public class JaxRsActivator extends Application {
	
	@Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        resources.add(HealthCheckResource.class);
        resources.add(RegistrationResource.class);
        return resources;
	}
}