package com.nowellpoint.www.app.view;

import com.nowellpoint.www.app.util.ResourceBundleUtil;

import freemarker.ext.beans.ResourceBundleModel;
import freemarker.template.Configuration;

abstract class AbstractController {
	
	protected static ResourceBundleModel labels;
	
	public AbstractController(Configuration cfg) {		
		labels = ResourceBundleUtil.getResourceBundle(SalesforceConnectorController.class.getName(), cfg.getLocale());
	}
}