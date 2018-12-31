package com.nowellpoint.sdk.salesforce.model;

import java.io.Serializable;
import java.util.List;

public class Theme implements Serializable {
	
	private static final long serialVersionUID = -8690811511992145361L;
	
	private List<ThemeItem> themeItems;

	public Theme() {
		
	}

	public List<ThemeItem> getThemeItems() {
		return themeItems;
	}

	public void setThemeItems(List<ThemeItem> themeItems) {
		this.themeItems = themeItems;
	}
}