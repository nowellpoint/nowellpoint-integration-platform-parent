package com.nowellpoint.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ServiceContextListener implements ServletContextListener {
    private StreamingEventListener listener;
    
    @Override
    public void contextInitialized(ServletContextEvent event) {        
        listener = StreamingEventListener.getInstance();
        listener.start();
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent event) {
    	listener.stop();
    }
}