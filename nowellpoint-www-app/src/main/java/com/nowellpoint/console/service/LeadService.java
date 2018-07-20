package com.nowellpoint.console.service;

import com.nowellpoint.console.entity.Lead;
import com.nowellpoint.console.model.LeadRequest;

public interface LeadService {
	
	public Lead createLead(LeadRequest request);
}