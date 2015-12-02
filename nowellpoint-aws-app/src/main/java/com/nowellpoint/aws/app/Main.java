package com.nowellpoint.aws.app;

import java.util.Optional;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.wildfly.swarm.container.Container;
import org.wildfly.swarm.jaxrs.JAXRSArchive;

import com.nowellpoint.aws.app.api.JaxRsActivator;

public class Main {

	
	public static void main(String[] args) throws Exception {
		System.setProperty("jboss.http.port", getPort());
		
        Container container = new Container();

        // Start the container
        container.start();
        
        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class);
        deployment.addPackage(JaxRsActivator.class.getPackage());
        deployment.addAllDependencies();
 
        // Deploy your JAX-RS app
        container.deploy(deployment);
    }
	
	private static String getPort() {
		return Optional.ofNullable(System.getenv().get("PORT")).orElse("9090");
	}
}
