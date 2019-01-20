package com.nowellpoint.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.nowellpoint.listener.connection.MongoConnection;

@WebListener
public class StreamingEventContextListener implements ServletContextListener {
	private MongoConnection connection;
    private StreamingEventListener listener;
    
    @Override
	public void contextInitialized(ServletContextEvent event) {
    	System.out.println("**** Context start");
    	connection = MongoConnection.getInstance();
    	connection.connect();
        
        listener = new StreamingEventListener();
        listener.start();
    }
    
    @Override
	public void contextDestroyed(ServletContextEvent event) {
    	listener.stop();
    	connection.disconnect();
    }
}