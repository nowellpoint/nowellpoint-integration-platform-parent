package com.nowellpoint.aws.api.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.esotericsoftware.yamlbeans.YamlWriter;
import com.nowellpoint.aws.api.dto.SalesforceConnectorDTO;
import com.nowellpoint.aws.data.mongodb.Environment;
import com.nowellpoint.aws.data.mongodb.EnvironmentVariable;
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
		SalesforceConnectorDTO original = findSalesforceConnector(resource.getSubject(), resource.getId());
		resource.setCreatedById(original.getCreatedById());
		resource.setCreatedDate(original.getCreatedDate());
		
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
	
	public SalesforceConnectorDTO addEnvironments(String subject, String id, String key, Set<Environment> environments) {
		SalesforceConnectorDTO resource = findSalesforceConnector(subject, id);
		resource.setSubject(subject);
		
		Optional<ServiceInstance> serviceInstance = resource.getServiceInstances().stream().filter(p -> p.getKey().equals(key)).findFirst();
		
		if (serviceInstance.isPresent()) {
			serviceInstance.get().setEnvironments(environments);
		}
		
		updateSalesforceConnector(resource);
		
		return resource;
		
	}
	
	public SalesforceConnectorDTO addVariables(String subject, String id, String key, String environmentName, Set<EnvironmentVariable> environmentVariables) {
		SalesforceConnectorDTO resource = findSalesforceConnector(subject, id);
		resource.setSubject(subject);
		
		Optional<ServiceInstance> serviceInstance = resource.getServiceInstances().stream().filter(p -> p.getKey().equals(key)).findFirst();
		
		if (serviceInstance.isPresent()) {
			if (environmentName == null) {
				serviceInstance.get().setEnvironmentVariables(environmentVariables);
			} else {
				Optional<Environment> environment = serviceInstance.get().getEnvironments().stream().filter(p -> p.getName().equals(environmentName)).findFirst();
				if (environment.isPresent()) {
					environment.get().setEnvironmentVariables(environmentVariables);
				}
			}
		}
		
		updateSalesforceConnector(resource);
		
		return resource;
	}
	
	public void addServiceConfiguration(String subject, String id, String key, Map<String, Object> configParams) {
		SalesforceConnectorDTO salesforceConnector = findSalesforceConnector(subject, id);
		
		Optional<ServiceInstance> serviceInstance = salesforceConnector.getServiceInstances().stream().filter(p -> p.getKey().equals(key)).findFirst();
		
		Map<String,Object> configFile = new HashMap<String,Object>();
		configFile.put("id", salesforceConnector.getIdentity().getId());
		configFile.put("organizationId", salesforceConnector.getOrganization().getId());
		configFile.put("instanceName", salesforceConnector.getOrganization().getInstanceName());
		configFile.put("url.sobjects", salesforceConnector.getIdentity().getUrls().getSobjects());
		configFile.put("url.metadata", salesforceConnector.getIdentity().getUrls().getMetadata());
		configFile.put("key", serviceInstance.get().getKey());
		configFile.put("providerType", serviceInstance.get().getProviderType());
		configFile.putAll(configParams);
		
		AmazonS3 s3Client = new AmazonS3Client();
		
		try {
					
			StringWriter sw = new StringWriter();
			
			YamlWriter writer = new YamlWriter(sw);
			writer.write(configFile);
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
}