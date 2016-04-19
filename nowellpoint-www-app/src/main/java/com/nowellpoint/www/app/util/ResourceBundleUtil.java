package com.nowellpoint.www.app.util;

import java.util.Locale;
import java.util.ResourceBundle;

import freemarker.ext.beans.ResourceBundleModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;

public class ResourceBundleUtil {
	
	//public static ResourceBundleModel getResourceBundle(Locale locale) {
	//	ResourceBundleModel resourceBundleModel = new ResourceBundleModel(ResourceBundle.getBundle("messages", locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build());
	//	return resourceBundleModel;
	//}
	
	public static ResourceBundleModel getResourceBundle(String baseName, Locale locale) {
		ResourceBundleModel resourceBundleModel = new ResourceBundleModel(ResourceBundle.getBundle(baseName, locale), new DefaultObjectWrapperBuilder(Configuration.getVersion()).build());
		return resourceBundleModel;
	}
}