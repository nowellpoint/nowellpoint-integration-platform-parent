package com.nowellpoint.console.sforce;

import static org.junit.Assert.assertNotNull;

import org.jboss.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nowellpoint.client.sforce.model.Limits;
import com.nowellpoint.client.sforce.model.Resources;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.console.model.Organization;
import com.nowellpoint.console.service.ServiceClient;

public class TestSalesforceServices {

	private static final Logger logger = Logger.getLogger(TestSalesforceServices.class.getName());
	
	@BeforeClass
	public static void start() {
		
	}
	
	@Test
	public void testDashboardComponents() {
		Organization organization = ServiceClient.getInstance()
				.organization()
				.get("5bac3c0e0626b951816064f5");
		
		assertNotNull(organization.getName());
		assertNotNull(organization.getNumber());
		assertNotNull(organization.getConnection());
		assertNotNull(organization.getConnection().getRefreshToken());
		
		logger.info(organization.getConnection().getRefreshToken());
		
		Token token = ServiceClient.getInstance()
				.salesforce()
				.refreshToken(organization.getConnection().getRefreshToken());
		
		long startTime = System.currentTimeMillis();
		
		logger.info(token.getRefreshToken());
		
		Organization updatedOrganization = ServiceClient.getInstance()
				.organization()
				.update(organization.getId(), token);
		
		long executionTime = System.currentTimeMillis() - startTime;
		
		logger.info("execution time: " + Long.valueOf(executionTime));
		
		logger.info(updatedOrganization.getLastUpdatedOn());
		
		Resources resources = ServiceClient.getInstance()
				.salesforce()
				.getResources(token);
		
		logger.info(resources.getIdentity());
		logger.info(resources.getLimits());
		logger.info(resources.getTooling());
		
		Limits limits = ServiceClient.getInstance()
				.salesforce()
				.getLimits(token);
		
		logger.info(limits.getDailyApiRequests().getMax());
		
	}
	
	@AfterClass
	public static void stop() {
		
	}
}