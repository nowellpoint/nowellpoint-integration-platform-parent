package com.nowellpoint.aws.api.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.esotericsoftware.yamlbeans.YamlWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.api.dto.EnvironmentDTO;
import com.nowellpoint.aws.api.dto.EnvironmentVariableDTO;
import com.nowellpoint.aws.api.dto.SalesforceConnectorDTO;
import com.nowellpoint.aws.api.dto.ServiceInstanceDTO;
import com.nowellpoint.aws.api.dto.ServiceProviderDTO;
import com.nowellpoint.aws.api.model.Environment;
import com.nowellpoint.aws.api.model.EnvironmentVariable;
import com.nowellpoint.aws.api.model.SalesforceConnector;
import com.nowellpoint.aws.api.model.ServiceInstance;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.DescribeGlobalResult;
import com.sforce.soap.partner.DescribeGlobalSObjectResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.fault.LoginFault;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

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
		
		hset( resource.getSubject(), SalesforceConnectorDTO.class.getName().concat( resource.getId() ), resource );
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
	
	public SalesforceConnectorDTO addService(ServiceProviderDTO serviceProvider, String subject, String id) {
		SalesforceConnectorDTO resource = findSalesforceConnector(subject, id);
		
		ServiceInstance serviceInstance = new ServiceInstance();
		serviceInstance.setKey(UUID.randomUUID().toString().replace("-", ""));
		serviceInstance.setServiceType(serviceProvider.getService().getType());
		serviceInstance.setConfigurationPage(serviceProvider.getService().getConfigurationPage());
		serviceInstance.setCurrencyIsoCode(serviceProvider.getService().getCurrencyIsoCode());
		serviceInstance.setProviderName(serviceProvider.getName());
		serviceInstance.setIsActive(Boolean.FALSE);
		serviceInstance.setServiceName(serviceProvider.getService().getName());
		serviceInstance.setPrice(serviceProvider.getService().getPrice());
		serviceInstance.setProviderType(serviceProvider.getType());
		serviceInstance.setUom(serviceProvider.getService().getUnitOfMeasure());
		serviceInstance.setEnvironmentVariableValues(serviceProvider.getService().getEnvironmentVariableValues());
		
		Set<Environment> environments = new HashSet<Environment>();
		
		Environment environment = new Environment();
		environment.setActive(Boolean.TRUE);
		environment.setIndex(0);
		environment.setLabel("Production");
		environment.setLocked(Boolean.TRUE);
		environment.setName("PRODUCTION");
		environment.setStatus("NOT STARTED");
		environment.setTest(Boolean.FALSE);
		environment.setEnvironmentVariables(serviceProvider.getService().getEnvironmentVariables());
		environments.add(environment);
		
		for (int i = 0; i < serviceProvider.getService().getSandboxCount(); i++) {
			environment = new Environment();
			environment.setActive(Boolean.FALSE);
			environment.setIndex(i + 1);
			environment.setLocked(Boolean.FALSE);
			environment.setName("SANDBOX_" + (i + 1));
			environment.setStatus("NOT STARTED");
			environment.setTest(Boolean.FALSE);
			environment.setEnvironmentVariables(serviceProvider.getService().getEnvironmentVariables());
			environments.add(environment);
		}
		
		serviceInstance.setEnvironments(environments);
		
		resource.setSubject(subject);
		resource.addServiceInstance(serviceInstance);
		
		updateSalesforceConnector(resource);
		
		return resource;
	}
	
	public SalesforceConnectorDTO updateService(String subject, String id, String key, ServiceInstanceDTO serviceInstance) {
		SalesforceConnectorDTO resource = findSalesforceConnector(subject, id);
		resource.setSubject(subject);
		
		Map<String,ServiceInstance> map = resource.getServiceInstances().stream().collect(Collectors.toMap(p -> p.getKey(), (p) -> p));
		resource.getServiceInstances().clear();
		if (map.containsKey(key)) {
			ServiceInstance original = map.get(key);
			original.setDefaultEnvironment(serviceInstance.getDefaultEnvironment());
			original.setName(serviceInstance.getName());
			map.put(key, original);
		}
		
		resource.setServiceInstances(new ArrayList<ServiceInstance>(map.values()));

		updateSalesforceConnector(resource);
		
		return resource;
	}
	
	public SalesforceConnectorDTO removeService(String subject, String id, String key) {
		SalesforceConnectorDTO resource = findSalesforceConnector(subject, id);
		resource.setSubject(subject);
		
		resource.getServiceInstances().removeIf(p -> p.getKey().equals(key));

		updateSalesforceConnector(resource);
		
		return resource;
	}
	
	public SalesforceConnectorDTO addEnvironments(String subject, String id, String key, Set<EnvironmentDTO> environments) {
		SalesforceConnectorDTO resource = findSalesforceConnector(subject, id);
		resource.setSubject(subject);
		
		Optional<ServiceInstance> serviceInstance = resource.getServiceInstances().stream().filter(p -> p.getKey().equals(key)).findFirst();
		
		if (serviceInstance.isPresent()) {
			Map<Integer,Environment> map = serviceInstance.get().getEnvironments().stream().collect(Collectors.toMap(p -> p.getIndex(), (p) -> p));
			serviceInstance.get().getEnvironments().clear();
			serviceInstance.get().setActiveEnvironments(new Long(0));
			AtomicInteger index = new AtomicInteger();
			environments.stream().sorted((p1,p2) -> p2.getIndex().compareTo(p1.getIndex())).forEach(e -> {
				Environment environment = null;
				if (map.containsKey(e.getIndex())) {
					environment = map.get(e.getIndex());
				} else {
				    environment = new Environment();
				    environment.setLocked(Boolean.FALSE);
				    environment.setIndex(index.get());
				}
				environment.setName(e.getName());
				environment.setActive(e.getActive());
				environment.setLabel(e.getLabel());
				AtomicLong activeEnvironments = new AtomicLong(serviceInstance.get().getActiveEnvironments());
				if (e.getActive()) {
					activeEnvironments.incrementAndGet();
				}
				serviceInstance.get().setActiveEnvironments(activeEnvironments.get());
				serviceInstance.get().getEnvironments().add(environment);
				index.incrementAndGet();
			});
		}
		
		updateSalesforceConnector(resource);
		
		return resource;
		
	}
	
	public SalesforceConnectorDTO addEnvironmentVariables(String subject, String id, String key, String environmentName, Set<EnvironmentVariableDTO> environmentVariables) {
		SalesforceConnectorDTO resource = findSalesforceConnector(subject, id);
		resource.setSubject(subject);
		
		Set<String> variables = new HashSet<String>();
		environmentVariables.stream().forEach(variable -> {
			if (variables.contains(variable.getVariable())) {
				throw new UnsupportedOperationException("Duplicate variable names: " + variable.getVariable());
			}
			variables.add(variable.getVariable());
			if (variable.getVariable().contains(" ")) {
				throw new IllegalArgumentException("Environment variables must not contain spaces: " + variable.getVariable());
			}
		});
		
		Optional<ServiceInstance> serviceInstance = resource.getServiceInstances().stream().filter(p -> p.getKey().equals(key)).findFirst();
		
		if (serviceInstance.isPresent()) {
			Optional<Environment> environment = serviceInstance.get().getEnvironments().stream().filter(p -> p.getName().equals(environmentName)).findFirst();
			if (environment.isPresent()) {
				Map<String,EnvironmentVariable> map = environment.get().getEnvironmentVariables().stream().collect(Collectors.toMap(p -> p.getVariable(), (p) -> p));
				environment.get().getEnvironmentVariables().clear();
				environmentVariables.stream().forEach(e -> {
					EnvironmentVariable environmentVariable = null;
					if (map.containsKey(e.getVariable())) {
						environmentVariable = map.get(e.getVariable());
					} else {
						environmentVariable = new EnvironmentVariable();
						environmentVariable.setVariable(e.getVariable());
						environmentVariable.setLocked(Boolean.FALSE);
					}
					environmentVariable.setValue(e.getValue());
					environmentVariable.setEncrypted(e.getEncrypted());
					environment.get().getEnvironmentVariables().add(environmentVariable);
				});
			}
			
			updateSalesforceConnector(resource);
		}
		
		return resource;
	}
	
	public SalesforceConnectorDTO testConnection(String subject, String id, String key, String environmentName) {
		SalesforceConnectorDTO resource = findSalesforceConnector(subject, id);
		resource.setSubject(subject);

		Optional<ServiceInstance> serviceInstance = resource.getServiceInstances()
				.stream()
				.filter(p -> p.getKey().equals(key))
				.findFirst();

		if (serviceInstance.isPresent()) {
			
			Optional<Environment> environment = serviceInstance.get()
					.getEnvironments()
					.stream()
					.filter(p -> p.getName().equals(environmentName))
					.findFirst();
			
			if (environment.isPresent()) {
				
				try {
				
					PartnerConnection connection = login(environment.get());
					
					environment.get().setEndpoint(connection.getConfig().getServiceEndpoint());
					environment.get().setOrganization(connection.getUserInfo().getOrganizationId());
					environment.get().setTest(Boolean.TRUE);
					environment.get().setTestMessage("Success!");
					
				} catch (ConnectionException e) {
					if (e instanceof LoginFault) {
						LoginFault loginFault = (LoginFault) e;
						environment.get().setTest(Boolean.FALSE);
						environment.get().setTestMessage(loginFault.getExceptionCode().name().concat(": ").concat(loginFault.getExceptionMessage()));
					} else {
						throw new InternalServerErrorException(e.getMessage());
					}
				} catch (IllegalArgumentException e) {
					environment.get().setTest(Boolean.FALSE);
					environment.get().setTestMessage("Missing connection enviroment variables");
				} finally {
					updateSalesforceConnector(resource);
				}
			}
		}
		
		return resource;
	}
	
	public DescribeGlobalSObjectResult[] getSobjects(String subject, String id, String key, String environmentName) {
		SalesforceConnectorDTO resource = findSalesforceConnector(subject, id);
		resource.setSubject(subject);
		
		DescribeGlobalResult result = null;

		Optional<ServiceInstance> serviceInstance = resource.getServiceInstances()
				.stream()
				.filter(p -> p.getKey().equals(key))
				.findFirst();

		if (serviceInstance.isPresent()) {
			
			Optional<Environment> environment = serviceInstance.get()
					.getEnvironments()
					.stream()
					.filter(p -> p.getName().equals(environmentName))
					.findFirst();
			
			if (environment.isPresent()) {
				
				try {
					
					PartnerConnection connection = login(environment.get());
					
					result = connection.describeGlobal();
					
					AmazonS3 s3Client = new AmazonS3Client();
					
			        byte[] bytes = new ObjectMapper().writeValueAsString(result).getBytes(StandardCharsets.UTF_8);
			        InputStream fileInputStream = new ByteArrayInputStream(bytes);
			        
			        ObjectMetadata metadata = new ObjectMetadata();
			        metadata.setContentType("application/json");
			        metadata.setContentLength(bytes.length);
					
			    	PutObjectRequest putObjectRequest = new PutObjectRequest("nowellpoint-profile-photos", connection.getUserInfo().getOrganizationId(), fileInputStream, metadata);
			    	
			    	s3Client.putObject(putObjectRequest);
					
				} catch (ConnectionException e) {
					if (e instanceof LoginFault) {
						LoginFault loginFault = (LoginFault) e;
						throw new BadRequestException(loginFault.getExceptionCode().name().concat(": ").concat(loginFault.getExceptionMessage()));
					} else {
						throw new InternalServerErrorException(e.getMessage());
					}
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				} finally {
					updateSalesforceConnector(resource);
				}
			}
		}
		
		return result.getSobjects();
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
	
	private PartnerConnection login(Environment environment) throws IllegalArgumentException, ConnectionException {
		
		String instance = null;
		String username = null;
		String password = null;
		String securityToken = null;
		
		Iterator<EnvironmentVariable> variables = environment.getEnvironmentVariables().iterator();
		
		while (variables.hasNext()) {
			EnvironmentVariable environmentVariable = variables.next();
			if ("INSTANCE".equals(environmentVariable.getVariable())) {
				instance = environmentVariable.getValue();
			}
			if ("USERNAME".equals(environmentVariable.getVariable())) {
				username = environmentVariable.getValue();
			}
			if ("SECURITY_TOKEN".equals(environmentVariable.getVariable())) {
				securityToken = environmentVariable.getValue();
			}
			if ("PASSWORD".equals(environmentVariable.getVariable())) {
				password = environmentVariable.getValue();
			}
		}
			
		if (instance == null || username == null || securityToken == null || password == null) {
			throw new IllegalArgumentException();
		}
			
		ConnectorConfig config = new ConnectorConfig();
		config.setAuthEndpoint(String.format("%s/services/Soap/u/36.0", instance));
		config.setUsername(username);
		config.setPassword(password.concat(securityToken));
		
		PartnerConnection connection = Connector.newConnection(config);
			
		return connection;
	}
}