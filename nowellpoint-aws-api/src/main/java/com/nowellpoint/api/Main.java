package com.nowellpoint.api;

import java.util.Optional;
import java.util.TimeZone;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.config.management.SecurityRealm;
import org.wildfly.swarm.config.management.security_realm.SslServerIdentity;
import org.wildfly.swarm.config.undertow.BufferCache;
import org.wildfly.swarm.config.undertow.HandlerConfiguration;
import org.wildfly.swarm.config.undertow.Server;
import org.wildfly.swarm.config.undertow.ServletContainer;
import org.wildfly.swarm.config.undertow.server.Host;
import org.wildfly.swarm.config.undertow.server.HTTPListener;
import org.wildfly.swarm.config.undertow.server.HttpsListener;
import org.wildfly.swarm.config.undertow.servlet_container.JSPSetting;
import org.wildfly.swarm.config.undertow.servlet_container.WebsocketsSetting;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.wildfly.swarm.logging.LoggingFraction;
import org.wildfly.swarm.management.ManagementFraction;
import org.wildfly.swarm.undertow.UndertowFraction;

import com.nowellpoint.aws.model.admin.Properties;

public class Main {
	
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws Exception {
		
		//
		// set system properties
		//
		
		System.setProperty("swarm.https.port", getPort());
		
		//
		// configure the management fraction
		//
		
		ManagementFraction management = new ManagementFraction()
				.securityRealm(new SecurityRealm("SSLRealm")
						.sslServerIdentity(new SslServerIdentity<>()
								.keystorePath("my.jks")
								.keystorePassword("secret")
								.alias("mycert")
								.keyPassword("secret")));
		
		//
		// configure the undertow fraction
		//
		
		UndertowFraction undertow = new UndertowFraction()
        		.server(new Server("default-server")
        				.httpListener(new HTTPListener("http")
        						.redirectSocket("https")
        						.socketBinding("http"))
        				.httpsListener(new HttpsListener("https")
        						.securityRealm("SSLRealm")
        						.socketBinding("https"))
        				.host(new Host("default-host")))
        		.bufferCache(new BufferCache("default"))
        		.servletContainer(new ServletContainer("default")
        				.websocketsSetting(new WebsocketsSetting())
        				.jspSetting(new JSPSetting()))
        		.handlerConfiguration(new HandlerConfiguration());

		//
		// build and start the container
		//
		
        Swarm container = new Swarm().fraction(management).fraction(undertow);
 
        
		//
        // set system properties from configuration
        //

        Properties.setSystemProperties(System.getenv("NCS_PROPERTY_STORE"));
        
        //
        // set timezone to UTC
        //
        
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        
        //
        // create the JAX-RS deployment archive
        // 
        
        JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class, "nowellpoint-api.war")
        		.addPackages(true, Package.getPackage("com.nowellpoint.api"))
        		.addAsWebInfResource(new ClassLoaderAsset("WEB-INF/web.xml", Main.class.getClassLoader()), "web.xml")
        		.addAsWebResource(new ClassLoaderAsset("ValidationMessages.properties", Main.class.getClassLoader()), "ValidationMessages.properties")
        		.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        
        deployment.addAllDependencies();
        
        //
        // start the container and deploy the archives
        //

        container.fraction(LoggingFraction.createDefaultLoggingFraction()).start().deploy(deployment);
    }
	
	private static String getPort() {
		return Optional.ofNullable(System.getenv().get("PORT")).orElse("9443");
	}
}