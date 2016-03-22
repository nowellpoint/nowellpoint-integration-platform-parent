package com.nowellpoint.aws.api.util;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.nowellpoint.aws.data.CacheManager;

@WebListener
public class Startup implements ServletContextListener {
	
	@Inject
	private CacheManager cacheManager;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		cacheManager.getCache();
	}
}