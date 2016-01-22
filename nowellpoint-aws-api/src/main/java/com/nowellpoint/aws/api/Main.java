package com.nowellpoint.aws.api;

import java.util.Optional;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.wildfly.swarm.container.Container;
import org.wildfly.swarm.jaxrs.JAXRSArchive;

import com.nowellpoint.aws.model.admin.Properties;

public class Main {
	
	public static void main(String[] args) throws Exception {

		//
		// build and start the container
		//
		
        Container container = new Container();
        container.setArgs(new String[] {"-b=".concat(getPort())});
        container.start();
        
		//
        // set system properties from configuration
        //

        Properties.setSystemProperties(System.getenv("PROPERTY_STORE"));
        
        //
		// set system properties
		//
		
		//System.setProperty("jboss.bind.address", getPort());
        
        //
        // create the JAX-RS deployment archive
        // 
        
        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class)
        		.addPackage("com.nowellpoint.aws.api.data")
        		.addPackage("com.nowellpoint.aws.api.resource")
        		.addPackage("com.nowellpoint.aws.api.util")
        		.addPackage("com.nowellpoint.aws.api.exception")
        		.addAsWebInfResource(new ClassLoaderAsset("WEB-INF/web.xml", Main.class.getClassLoader()), "web.xml")
        		.addAsWebInfResource(new ClassLoaderAsset("META-INF/beans.xml", Main.class.getClassLoader()), "beans.xml")
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