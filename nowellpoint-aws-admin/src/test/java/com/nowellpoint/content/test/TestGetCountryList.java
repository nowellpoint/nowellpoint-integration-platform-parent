package com.nowellpoint.content.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.nowellpoint.content.model.IsoCountryList;
import com.nowellpoint.content.model.PlanList;
import com.nowellpoint.content.service.ContentService;

public class TestGetCountryList {
	
	@Test
	public void testGetCountries() {
		ContentService service = new ContentService();
		IsoCountryList countryList = service.getCountries();
		assertTrue(countryList.getSize() > 0);
	}
	
	@Test
	public void testGetPlans() {
		ContentService service = new ContentService();
		PlanList planList = service.getPlans();
		assertTrue(planList.getSize() > 0);
	}
}