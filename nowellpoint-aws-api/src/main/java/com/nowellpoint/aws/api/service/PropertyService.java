package com.nowellpoint.aws.api.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.modelmapper.ModelMapper;

import com.nowellpoint.aws.api.dto.PropertyDTO;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.admin.Property;

public class PropertyService {
	
	protected final ModelMapper modelMapper = new ModelMapper();
	
	public Set<PropertyDTO> getProperties() {
		Set<PropertyDTO> resources = new HashSet<PropertyDTO>();
		Map<String, Property> properties = Properties.getProperties(System.getenv("NCS_PROPERTY_STORE"));	
		properties.values().stream().forEach(property -> {
			resources.add(modelMapper.map(property, PropertyDTO.class));
		});
		return resources;
	}
}