package com.nowellpoint.api.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.modelmapper.ModelMapper;

import com.nowellpoint.api.model.dto.Property;
import com.nowellpoint.aws.model.admin.Properties;

public class PropertyService {
	
	protected final ModelMapper modelMapper = new ModelMapper();
	
	public PropertyService() {
		
	}
	
	public Set<Property> getProperties() {
		Set<Property> resources = new HashSet<Property>();
		Map<String, com.nowellpoint.aws.model.admin.Property> properties = Properties.getProperties(System.getenv("NCS_PROPERTY_STORE"));	
		properties.values().stream().forEach(property -> {
			resources.add(modelMapper.map(property, Property.class));
		});
		return resources;
	}
}