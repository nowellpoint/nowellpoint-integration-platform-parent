package com.nowellpoint.aws.api.util;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		Logger logger = LoggerFactory.getLogger(Startup.class);
		logger.info("from logback");
	}
}