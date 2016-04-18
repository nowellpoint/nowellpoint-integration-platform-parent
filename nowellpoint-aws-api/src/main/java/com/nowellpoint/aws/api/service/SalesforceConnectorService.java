package com.nowellpoint.aws.api.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.Status;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.esotericsoftware.yamlbeans.YamlWriter;
import com.nowellpoint.aws.api.dto.ParentDTO;
import com.nowellpoint.aws.api.dto.SalesforceConnectorDTO;
import com.nowellpoint.aws.api.dto.ServiceInstanceDTO;
import com.nowellpoint.aws.api.resource.SalesforceConnectorResource;
import com.nowellpoint.aws.data.mongodb.SalesforceConnector;
import com.nowellpoint.aws.data.mongodb.ServiceInstance;

public class SalesforceConnectorService extends AbstractDocumentService<SalesforceConnectorDTO, SalesforceConnector> {
	
	public SalesforceConnectorService() {
		super(SalesforceConnectorDTO.class, SalesforceConnector.class);
	}
	
	public Set<SalesforceConnectorDTO> getAll(String subject) {
		Set<SalesforceConnectorDTO> resources = hscan( subject, SalesforceConnectorDTO.class );		
		if (resources.isEmpty()) {
			resources = findAllByOwner(subject);
			hset( subject, resources );
		}
		return resources;
	}

	public SalesforceConnectorDTO createSalesforceConnector(SalesforceConnectorDTO resource) {
		create( resource );
		hset( resource.getSubject(), SalesforceConnectorDTO.class.getName().concat( resource.getId()), resource );
		hset( resource.getId(), resource.getSubject(), resource );
		return resource;
	}
	
	public SalesforceConnectorDTO updateSalesforceConnector(SalesforceConnectorDTO resource) {
		replace( resource );
		hset( resource.getSubject(), SalesforceConnectorDTO.class.getName().concat( resource.getId()), resource );
		hset( resource.getId(), resource.getSubject(), resource );
		return resource;
	}
	
	public void deleteSalesforceConnector(String id, String subject) {
		SalesforceConnectorDTO resource = new SalesforceConnectorDTO(id);
		delete(resource);
		hdel( subject, SalesforceConnectorDTO.class.getName().concat(id) );
		hdel( id, subject );
	}
	
	public SalesforceConnectorDTO findSalesforceConnector(String subject, String id) {
		SalesforceConnectorDTO resource = hget( SalesforceConnectorDTO.class, id, subject );
		if ( resource == null ) {		
			resource = find(id);
			hset( id, subject, resource );
		}
		return resource;
	}
	
	public void addServiceConfiguration(String subject, String id, String key) {
		SalesforceConnectorDTO salesforceConnector = findSalesforceConnector(subject, id);
		
		Optional<ServiceInstance> serviceInstance = salesforceConnector.getServiceInstances().stream().filter(p -> p.getKey().equals(key)).findFirst();
		
		Map<String,Object> configParams = new HashMap<String,Object>();
		configParams.put("id", salesforceConnector.getId());
		configParams.put("identityId", salesforceConnector.getIdentity().getId());
		configParams.put("organizationName", salesforceConnector.getOrganization().getName());
		configParams.put("organizationType", salesforceConnector.getOrganization().getOrganizationType());
		configParams.put("instanceName", salesforceConnector.getOrganization().getInstanceName());
		configParams.put("key", serviceInstance.get().getKey());
		configParams.put("providerName", serviceInstance.get().getProviderName());
		configParams.put("providerType", serviceInstance.get().getProviderType());
		
		
		AmazonS3 s3Client = new AmazonS3Client();
		
		try {
					
			StringWriter sw = new StringWriter();
			
			YamlWriter writer = new YamlWriter(sw);
			writer.write(configParams);
			writer.close();
			
			byte[] bytes = sw.toString().getBytes(StandardCharsets.UTF_8);
			
			ObjectMetadata objectMetadata = new ObjectMetadata();
	    	objectMetadata.setContentLength(bytes.length);
	    	objectMetadata.setContentType(MediaType.TEXT_PLAIN);
			
	    	PutObjectRequest putObjectRequest = new PutObjectRequest("nowellpoint-configuration-files", key, new ByteArrayInputStream(bytes), objectMetadata);
	    	
	    	s3Client.putObject(putObjectRequest);	
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	public ServiceInstanceDTO getServiceInstance(String subject, String id, String key) {
		SalesforceConnectorDTO salesforceConnector = findSalesforceConnector(subject, id);
		
		Optional<ServiceInstance> serviceInstance = salesforceConnector.getServiceInstances().stream().filter(p -> p.getKey().equals(key)).findFirst();
		
		ServiceInstanceDTO resource = null;
		
		if (serviceInstance.isPresent()) {
			URI uri = UriBuilder.fromResource(SalesforceConnectorResource.class)
					.path("/{id}")
					.build(id);
			
			resource = modelMapper.map(serviceInstance.get(), ServiceInstanceDTO.class);
			resource.setParent(new ParentDTO(SalesforceConnector.class.getSimpleName(), id, uri.toString()));
		}
		
		return resource;
	}
}