package com.nowellpoint.console.sforce;

import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
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
	public void testCreatePassword() {
		String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
	    String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
	    String numbers = RandomStringUtils.randomNumeric(2);
	    String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
	    String totalChars = RandomStringUtils.randomAlphanumeric(2);
	    
	    String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
	    		.concat(numbers)
	    		.concat(specialChar)
	    		.concat(totalChars);
	    
	    List<Character> pwdChars = combinedChars.chars()
	    		.mapToObj(c -> (char) c)
	    		.collect(Collectors.toList());
	    
	    Collections.shuffle(pwdChars);
	    
	    String password = pwdChars.stream()
	    		.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
	    		.toString();
	    
	    logger.info(password);
	}
	
	//@Test
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