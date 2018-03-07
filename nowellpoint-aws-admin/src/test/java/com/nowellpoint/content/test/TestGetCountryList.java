package com.nowellpoint.content.test;

import org.junit.Test;

import com.nowellpoint.content.model.IsoCountryList;
import com.nowellpoint.content.model.PlanList;
import com.nowellpoint.content.service.IsoCountryService;
import com.nowellpoint.content.service.PlanService;

public class TestGetCountryList {
	
	@Test
	public void testGetCountries() {
		IsoCountryService service = new IsoCountryService();
		IsoCountryList countryList = service.getCountries();
		
		countryList.getItems().stream().forEach(c -> {
			System.out.println(c.getCode() + " " + c.getName());
		});
	}
	
	@Test
	public void testGetPlans() {
		PlanService service = new PlanService();
		PlanList planList = service.getPlans();
		
		planList.getItems().stream().forEach(p -> {
			System.out.println(p.getPlanCode() + " " + p.getPlanName());
		});
	}
}