package com.nowellpoint.aws.app.data;


import static com.nowellpoint.aws.app.data.Datastore.connect;
import static com.nowellpoint.aws.app.data.Datastore.close;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ApplicationEventListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		
		//
		// close datastore connection
		//
		
		close();		
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		
		//
		// connect to read datastore
		//
		
		connect();		
	}
}