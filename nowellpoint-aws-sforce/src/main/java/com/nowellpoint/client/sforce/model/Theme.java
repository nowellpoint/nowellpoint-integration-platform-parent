package com.nowellpoint.client.sforce.model;

import java.util.List;

public class Theme {
	
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