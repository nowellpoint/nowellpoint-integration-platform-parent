package com.nowellpoint.sdk.salesforce.model;

import java.util.List;

public class ThemeItem {
	
	private String name;
	
	private List<Color> colors;
	
	private List<Icon> icons;
	
	public ThemeItem() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Color> getColors() {
		return colors;
	}

	public void setColors(List<Color> colors) {
		this.colors = colors;
	}

	public List<Icon> getIcons() {
		return icons;
	}

	public void setIcons(List<Icon> icons) {
		this.icons = icons;
	}
}