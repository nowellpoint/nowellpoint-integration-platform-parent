package com.nowellpoint.aws.app;

import java.util.Optional;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.wildfly.swarm.container.Container;
import org.wildfly.swarm.jaxrs.JAXRSArchive;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
		//
		// set system properties
		//
		
		System.setProperty("jboss.http.port", getPort());
		
		//
		// build and start the container
		//
		
        Container container = new Container();
        container.start();
        
        //
        // create the JAX-RS deployment archive
        // 
        
        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class)
        		.addPackage("com.nowellpoint.aws.app.api")
        		.addPackage("com.nowellpoint.aws.app.data")
        		.addAllDependencies();
        
        //
        // deploy archives
        //
 
        container.deploy(deployment);
    }
	
	private static String getPort() {
		return Optional.ofNullable(System.getenv().get("PORT")).orElse("9090");
	}
}