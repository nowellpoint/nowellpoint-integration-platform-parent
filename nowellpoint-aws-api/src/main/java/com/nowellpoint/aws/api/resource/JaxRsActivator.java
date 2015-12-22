package com.nowellpoint.aws.api.resource;

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
        resources.add(LeadResource.class);
        resources.add(TokenResource.class);
        resources.add(IsoCountryResource.class);
        resources.add(AccountResource.class);
        resources.add(ConfigurationResource.class);
        return resources;
	}
}