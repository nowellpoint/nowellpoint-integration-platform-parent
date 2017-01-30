package com.nowellpoint.api;

import java.util.Optional;
import java.util.TimeZone;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.jaxrs.JAXRSArchive;

import com.nowellpoint.util.Properties;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
		//
		// build the container
		//
		
        Swarm container = new Swarm(args); 
		
		//
		// set default system properties
		//
		
		System.setProperty("swarm.http.port", getHttpPort());
		System.setProperty("swarm.https.port", getHttpsPort());
        
		//
        // set system properties from configuration
        //

        Properties.loadProperties(container
                .stageConfig()
                .resolve("propertyStore.name")
                .getValue());
        
        //
        // set default time zone to UTC
        //
        
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        
        //
        // create the JAX-RS deployment archive
        // 
        
        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class, "nowellpoint-api.war")
        		.addPackages(true, Package.getPackage("com.nowellpoint.api"))
        		.addAsWebInfResource(new ClassLoaderAsset("WEB-INF/web.xml", Main.class.getClassLoader()), "web.xml")
        		.addAsWebResource(new ClassLoaderAsset("ValidationMessages.properties", Main.class.getClassLoader()), "ValidationMessages.properties")
        		.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
        		.addAllDependencies();
        
        //
        // start the container and deploy the archive
        //
        
        container.start().deploy(deployment);

    }
	
	public static String getHttpPort() {
		return Optional.ofNullable(System.getenv().get("PORT")).orElse("5000");
	}
	
	public static String getHttpsPort() {
		return String.valueOf(Integer.valueOf(getHttpPort()) + 100);
	}
}