package com.nowellpoint.api.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.modelmapper.ModelMapper;

import com.nowellpoint.api.model.dto.AccountProfile;
import com.nowellpoint.api.model.dto.PropertyDTO;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.admin.Property;

public class PropertyService {
	
	@Inject
	private AccountProfileService accountProfileService;
	
	protected final ModelMapper modelMapper = new ModelMapper();
	
	public PropertyService() {
		
	}
	
	public Set<PropertyDTO> getProperties() {
		String subject = UserContext.getPrincipal().getName();
		AccountProfile accountProfile = accountProfileService.findAccountProfileByHref(subject);
		if (! accountProfile.getHasFullAccess()) {
			throw new ServiceException(Response.Status.UNAUTHORIZED, String.format("Your profile does not have permission to access the requested resource"));
		}
		Set<PropertyDTO> resources = new HashSet<PropertyDTO>();
		Map<String, Property> properties = Properties.getProperties(System.getenv("NCS_PROPERTY_STORE"));	
		properties.values().stream().forEach(property -> {
			resources.add(modelMapper.map(property, PropertyDTO.class));
		});
		return resources;
	}
}