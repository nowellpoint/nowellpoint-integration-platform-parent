package com.nowellpoint.aws.api.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.xml.bind.JAXBException;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.nowellpoint.aws.api.dto.EnvironmentDTO;
import com.nowellpoint.aws.api.dto.EnvironmentVariableDTO;
import com.nowellpoint.aws.api.dto.EventListenerDTO;
import com.nowellpoint.aws.api.dto.SalesforceConnectorDTO;
import com.nowellpoint.aws.api.dto.ServiceInstanceDTO;
import com.nowellpoint.aws.api.dto.ServiceProviderDTO;
import com.nowellpoint.aws.api.model.Environment;
import com.nowellpoint.aws.api.model.EnvironmentVariable;
import com.nowellpoint.aws.api.model.EventListener;
import com.nowellpoint.aws.api.model.SalesforceConnector;
import com.nowellpoint.aws.api.model.ServiceInstance;
import com.nowellpoint.aws.api.model.Targets;
import com.nowellpoint.aws.api.model.dynamodb.OutboundMessageHandlerConfiguration;
import com.nowellpoint.aws.api.model.dynamodb.Callback;
import com.nowellpoint.aws.api.tasks.BuildDefaultCallback;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.DescribeGlobalResult;
import com.sforce.soap.partner.DescribeGlobalSObjectResult;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.fault.ApiQueryFault;
import com.sforce.soap.partner.fault.InvalidSObjectFault;
import com.sforce.soap.partner.fault.LoginFault;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class SalesforceConnectorService extends AbstractDocumentService<SalesforceConnectorDTO, SalesforceConnector> {
	
	@Inject
	private OutboundMessageService outboundMessageService;
	
	private static DynamoDBMapper mapper = new DynamoDBMapper(new AmazonDynamoDBClient());
	
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
		serviceInstance.setUnitOfMeasure(serviceProvider.getService().getUnitOfMeasure());
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
		
		if (resource.getServiceInstances() == null) {
			resource.setServiceInstances(Collections.emptySet());
		}
		
		Map<String,ServiceInstance> map = resource.getServiceInstances().stream().collect(Collectors.toMap(p -> p.getKey(), (p) -> p));
		
		resource.getServiceInstances().clear();
		
		if (map.containsKey(key)) {
			ServiceInstance original = map.get(key);
			original.setSourceEnvironment(serviceInstance.getSourceEnvironment());
			original.setName(serviceInstance.getName());
			map.put(key, original);
		}
		
		resource.setServiceInstances(new HashSet<ServiceInstance>(map.values()));

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
	
	public SalesforceConnectorDTO addEventListeners(String subject, String id, String key, Set<EventListenerDTO> eventListeners) {
		SalesforceConnectorDTO resource = findSalesforceConnector(subject, id);
		resource.setSubject(subject);
		
		Optional<ServiceInstance> serviceInstance = resource.getServiceInstances().stream().filter(p -> p.getKey().equals(key)).findFirst();
		
		if (serviceInstance.isPresent()) {
			
			if (serviceInstance.get().getEventListeners() == null) {
				serviceInstance.get().setEventListeners(Collections.emptySet());
			}
			
			Map<String,EventListener> map = serviceInstance.get().getEventListeners().stream().collect(Collectors.toMap(p -> p.getName(), (p) -> p));
			
			serviceInstance.get().getEventListeners().clear();
			
			eventListeners.stream().forEach(p -> {
				EventListener eventListener =  map.get(p.getName());
				eventListener.setCreate(p.getCreate());
				eventListener.setUpdate(p.getUpdate());
				eventListener.setDelete(p.getDelete());
				eventListener.setCallback(p.getCallback());
				map.put(p.getName(), eventListener);
			});
			
			serviceInstance.get().setEventListeners(new HashSet<EventListener>(map.values()));
			
			updateSalesforceConnector(resource);
		}
		
		return resource;
	}
	
	public SalesforceConnectorDTO addTargets(String subject, String id, String key, Targets targets) {
		SalesforceConnectorDTO resource = findSalesforceConnector(subject, id);
		resource.setSubject(subject);
		
		Optional<ServiceInstance> serviceInstance = resource.getServiceInstances()
				.stream()
				.filter(p -> p.getKey().equals(key))
				.findFirst();
		
		if (serviceInstance.isPresent()) {
			
			serviceInstance.get().setTargets(targets);
			
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
	
	public SalesforceConnectorDTO describeGlobal(String subject, String id, String key) {
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
					.filter(p -> p.getName().equals(serviceInstance.get().getSourceEnvironment()))
					.findFirst();
			
			if (environment.isPresent()) {
				
				try {
					
					PartnerConnection connection = login(environment.get());
					
					DescribeGlobalResult result = connection.describeGlobal();
					
					DescribeGlobalSObjectResult[] sobjects = result.getSobjects();
					
					if (serviceInstance.get().getEventListeners() == null) {
						serviceInstance.get().setEventListeners(new HashSet<EventListener>());
					}
					
					Map<String,EventListener> map = serviceInstance.get().getEventListeners().stream().collect(Collectors.toMap(p -> p.getName(), (p) -> p));
					
					serviceInstance.get().getEventListeners().clear();
					
					Arrays.asList(sobjects).stream().forEach(p -> {
						
						EventListener eventListener = new EventListener();
						eventListener.setName(p.getName());
						eventListener.setLabel(p.getLabel());
						eventListener.setTriggerable(p.getTriggerable());
						eventListener.setCreateable(p.getCreateable());
						eventListener.setDeleteable(p.getDeletable());
						eventListener.setUpdateable(p.getUpdateable());
						eventListener.setReplicateable(p.getReplicateable());
						eventListener.setQueryable(p.getQueryable());
						
						if (map.containsKey(p.getName())) {
							eventListener.setCreate(map.get(p.getName()).getCreate());
							eventListener.setDelete(map.get(p.getName()).getDelete());
							eventListener.setUpdate(map.get(p.getName()).getUpdate());
							eventListener.setCallback(map.get(p.getName()).getCallback());
						} else {
							eventListener.setCreate(Boolean.FALSE);
							eventListener.setDelete(Boolean.FALSE);
							eventListener.setUpdate(Boolean.FALSE);
						}
						
						serviceInstance.get().getEventListeners().add(eventListener);
						
					});
					
					updateSalesforceConnector(resource);
					
				} catch (ConnectionException e) {
					if (e instanceof LoginFault) {
						LoginFault loginFault = (LoginFault) e;
						throw new BadRequestException(loginFault.getExceptionCode().name().concat(": ").concat(loginFault.getExceptionMessage()));
					} else {
						throw new InternalServerErrorException(e.getMessage());
					}
				}
			}
		}
		
		return resource;
	}
	
	public Field[] describeSobject(String subject, String id, String key, String sobject) {
		SalesforceConnectorDTO resource = findSalesforceConnector(subject, id);
		resource.setSubject(subject);
		
		Field[] fields = null;

		Optional<ServiceInstance> serviceInstance = resource.getServiceInstances()
				.stream()
				.filter(p -> p.getKey().equals(key))
				.findFirst();

		if (serviceInstance.isPresent()) {
			
			Optional<Environment> environment = serviceInstance.get()
					.getEnvironments()
					.stream()
					.filter(p -> p.getName().equals(serviceInstance.get().getSourceEnvironment()))
					.findFirst();
			
			if (environment.isPresent()) {
				
				try {
					
					PartnerConnection connection = login(environment.get());
					
					DescribeSObjectResult result = connection.describeSObject(sobject);
					
					fields = result.getFields();
					
				} catch (ConnectionException e) {
					if (e instanceof LoginFault) {
						LoginFault loginFault = (LoginFault) e;
						throw new BadRequestException(loginFault.getExceptionCode().name().concat(": ").concat(loginFault.getExceptionMessage()));
					} else if (e instanceof InvalidSObjectFault) {
						InvalidSObjectFault fault = (InvalidSObjectFault) e;
						throw new BadRequestException(fault.getExceptionCode().name().concat(": ").concat(fault.getExceptionMessage()));
					} else {
						throw new InternalServerErrorException(e.getMessage());
					}
				}
			}
		}
		
		return fields;
	}
	
	public SObject[] query(String subject, String id, String key, String queryString) {
		if (subject == null) {
			throw new IllegalArgumentException("Missing parameter: subject");
		}
		
		if (id == null) {
			throw new IllegalArgumentException("Missing parameter: id");
		}
		
		if (key == null) {
			throw new IllegalArgumentException("Missing parameter: id");
		}
		
		if (queryString == null) {
			throw new IllegalArgumentException("Missing parameter: queryString");
		}
		
		SalesforceConnectorDTO resource = findSalesforceConnector(subject, id);
		resource.setSubject(subject);
		
		SObject[] sobjects = null;

		Optional<ServiceInstance> serviceInstance = resource.getServiceInstances()
				.stream()
				.filter(p -> p.getKey().equals(key))
				.findFirst();

		if (serviceInstance.isPresent()) {

			Optional<Environment> environment = serviceInstance.get()
					.getEnvironments()
					.stream()
					.filter(p -> p.getName().equals(serviceInstance.get().getSourceEnvironment()))
					.findFirst();

			if (environment.isPresent()) {
				
				try {

					PartnerConnection connection = login(environment.get());

					QueryResult result = connection.query(queryString);

					sobjects = result.getRecords();

				} catch (ConnectionException e) {
					if (e instanceof LoginFault) {
						LoginFault loginFault = (LoginFault) e;
						throw new BadRequestException(loginFault.getExceptionCode().name().concat(": ").concat(loginFault.getExceptionMessage()));
					} else if (e instanceof ApiQueryFault) {
						ApiQueryFault fault = (ApiQueryFault) e;
						throw new BadRequestException(fault.getExceptionCode().name().concat(": ").concat(fault.getExceptionMessage()));
					} else {
						throw new InternalServerErrorException(e.getMessage());
					}
				}
			}
		}
		
		return sobjects;
		
	}
	
	public SalesforceConnectorDTO deploy(String subject, String id, String key, String environmentName) throws JAXBException, IOException, IllegalArgumentException, ConnectionException, InterruptedException, ExecutionException {
		SalesforceConnectorDTO resource = findSalesforceConnector(subject, id);
		resource.setSubject(subject);

		Optional<ServiceInstance> serviceInstance = resource.getServiceInstances()
				.stream()
				.filter(p -> p.getKey().equals(key))
				.findFirst();

		if (serviceInstance.isPresent()) {
			
			Optional<Environment> environment = serviceInstance.get().getEnvironments()
					.stream()
					.filter(p -> p.getName().equals(environmentName))
					.findFirst();
			
			if (environment.isPresent()) {
				
				PartnerConnection connection = login(environment.get());
				
				List<BuildDefaultCallback> tasks = serviceInstance.get()
						.getEventListeners()
						.stream()
						.filter(p -> p.getCreate() || p.getUpdate() || p.getDelete())
						.map(p -> new BuildDefaultCallback(connection, p))
						.collect(Collectors.toCollection(ArrayList::new));
				
				ExecutorService executor = Executors.newFixedThreadPool(tasks.size());
				
				List<Future<Callback>> futures = executor.invokeAll(tasks);
				executor.shutdown();
				executor.awaitTermination(30, TimeUnit.SECONDS);
				
				List<Callback> queries = new ArrayList<Callback>();
				
				for (Future<Callback> future : futures) {
					queries.add(future.get());
				}
				
				OutboundMessageHandlerConfiguration configuration = new OutboundMessageHandlerConfiguration();
				configuration.setOrganizationId(environment.get().getOrganization());
				configuration.setAwsAccessKey(serviceInstance.get().getTargets().getSimpleStorageService().getAwsAccessKey());
				configuration.setAwsSecretAccessKey(serviceInstance.get().getTargets().getSimpleStorageService().getAwsSecretAccessKey());
				configuration.setBucketName(serviceInstance.get().getTargets().getSimpleStorageService().getBucketName());
				configuration.setEnvironmentName(environment.get().getName());
				configuration.setServiceInstanceKey(serviceInstance.get().getKey());
				configuration.setQueries(queries);
				configuration.setDeploymentDate(new Date());
				configuration.setDeployedBy(subject);
				configuration.setIntegrationUser(connection.getConfig().getUsername());
				
				mapper.save(configuration);
				
				String packageKey = outboundMessageService.buildPackage(configuration);
				
				outboundMessageService.deployPackage(connection, packageKey);
				
				
				//https://na11.salesforce.com/services/Soap/u/36.0/00DG0000000kGBr
				//https://na45.salesforce.com/services/Soap/m/36.0/00D300000000lnE
				
				//outboundMessageService.deployPackage(loginResult, inputStream);
			}
		}
		
		return resource;
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