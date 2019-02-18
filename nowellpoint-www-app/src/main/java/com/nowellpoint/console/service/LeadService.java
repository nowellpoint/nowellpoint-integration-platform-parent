package com.nowellpoint.console.service;

import com.nowellpoint.console.model.Lead;
import com.nowellpoint.console.model.LeadRequest;

public interface LeadService {
	
	public Lead create(LeadRequest request);
}