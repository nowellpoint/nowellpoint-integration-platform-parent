package com.nowellpoint.console.service;

import com.nowellpoint.console.service.impl.IdentityServiceImpl;
import com.nowellpoint.console.service.impl.LeadServiceImpl;
import com.nowellpoint.console.service.impl.NotificationServiceImpl;
import com.nowellpoint.console.service.impl.OrganizationServiceImpl;
import com.nowellpoint.console.service.impl.PlanServiceImpl;
import com.nowellpoint.console.service.impl.SalesforceServiceImpl;
import com.nowellpoint.console.service.impl.ConsoleServiceImpl;

public class ServiceClient {
	
	private static final ServiceClient INSTANCE = new ServiceClient();
	
	private ConsoleService console;
	private IdentityService identity;
	private LeadService lead;
	private OrganizationService organization;
	private NotificationService notification;
	private PlanService plan;
	private SalesforceService salesforce;
	
	private ServiceClient() {
		
	}
	
	public static ServiceClient getInstance() {
		return INSTANCE;
	}
	
	public ConsoleService console() {
		if (console == null) {
			console = new ConsoleServiceImpl();
		}
		return console;
	}
	
	public IdentityService identity() {
		if (identity == null) {
			identity = new IdentityServiceImpl();
		}
		return identity;
	}
	
	public LeadService lead() {
		if (lead == null) {
			lead = new LeadServiceImpl();
		} 
		return lead;
	}
	
	public OrganizationService organization() {
		if (organization == null) {
			organization = new OrganizationServiceImpl();
		}
		return organization;
	}
	
	public NotificationService notification() {
		if (notification == null) {
			notification = new NotificationServiceImpl();
		}
		return notification;
	}
	
	public PlanService plan() {
		if (plan == null) {
			plan = new PlanServiceImpl();
		}
		return plan;
	}
	
	public SalesforceService salesforce() {
		if (salesforce == null) {
			salesforce = new SalesforceServiceImpl();
		}
		return salesforce;
	}
}