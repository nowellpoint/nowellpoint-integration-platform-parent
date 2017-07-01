package com.nowellpoint.api.rest.domain;

import static com.sforce.soap.partner.Connector.newConnection;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.common.base.Preconditions;
import com.nowellpoint.api.model.dynamodb.VaultEntry;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;
import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.Client;
import com.nowellpoint.client.sforce.DescribeGlobalSobjectsRequest;
import com.nowellpoint.client.sforce.DescribeSobjectRequest;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.RefreshTokenGrantRequest;
import com.nowellpoint.client.sforce.ThemeRequest;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Organization;
import com.nowellpoint.client.sforce.model.Theme;
import com.nowellpoint.client.sforce.model.sobject.DescribeGlobalSobjectsResult;
import com.nowellpoint.client.sforce.model.sobject.DescribeSobjectResult;
import com.nowellpoint.client.sforce.model.sobject.Sobject;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;
import com.nowellpoint.util.DigitalSignature;
import com.nowellpoint.util.Properties;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.fault.LoginFault;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.nowellpoint.client.sforce.model.Token;

public class SalesforceConnector extends AbstractResource {
	
	private static final DynamoDBMapper dynamoDBMapper = DynamoDBMapperProvider.getDynamoDBMapper();
	
	private String name;
	
	private UserInfo createdBy;
	
	private UserInfo lastUpdatedBy;
	
	private UserInfo owner;

	private Identity identity;
	
	private Organization organization;
	
	private String connectionString;
	
	private Date lastTestedOn;
	
	private Boolean isValid;
	
	private String serviceEndpoint;
	
	private String status;
	
	private String tag;
	
	private Theme theme;
	
	private Set<Job> jobs = new HashSet<>();
	
	private Set<Service> services = new HashSet<>();
	
	private Set<Sobject> sobjects = new HashSet<>();
	
	public SalesforceConnector() {
		
	}
	
	private SalesforceConnector(
			String name, 
			UserInfo createdBy, 
			UserInfo lastUpdatedBy, 
			UserInfo owner, 
			Date createdOn, 
			Date lastUpdatedOn, 
			Identity identity, 
			Organization organization, 
			String connectionString, 
			Boolean isValid, 
			String serviceEndpoint,
			Date lastTestedOn) {
		
		this.name = name;
		this.createdBy = createdBy;
		this.lastUpdatedBy = lastUpdatedBy;
		this.owner = owner;
		this.createdOn = createdOn;
		this.lastUpdatedOn = lastUpdatedOn;
		this.identity = identity;
		this.organization = organization;
		this.connectionString = connectionString;
		this.isValid = isValid;
		this.serviceEndpoint = serviceEndpoint;
		this.lastTestedOn = lastTestedOn;
	}
	
	private SalesforceConnector(SalesforceConnector instance, UpdateSalesforceConnectorRequest request) {
		this.id = instance.getId();
		this.name = instance.getName();
		this.createdBy = instance.getCreatedBy();
		this.createdOn = instance.getCreatedOn();
		this.meta = instance.getMeta();
		this.owner = instance.getOwner();
		this.identity = instance.getIdentity();
		this.organization = instance.getOrganization();
		this.connectionString = instance.getConnectionString();
		this.lastTestedOn = instance.getLastTestedOn();
		this.isValid = instance.getIsValid();
		this.serviceEndpoint = instance.getServiceEndpoint();
		this.status = instance.getStatus();
		this.tag = instance.getTag();
		this.services = instance.getServices();
		this.sobjects = instance.getSobjects();
		this.theme = instance.getTheme();
		this.lastUpdatedBy = request.getLastUpdatedBy();
		this.lastUpdatedOn = Date.from(Instant.now());
		
		if (Assert.isNotNull(request.getName())) {
			this.name = request.getName();
		}
		
		if (Assert.isNotNull(request.getOwner())) {
			this.owner = request.getOwner();
		}
		
		if (request.getTag().isPresent()) {
			this.tag = request.getTag().get();
		} else if (Assert.isEmpty(request.getTag().get())) {
			this.tag = null;
		}
	}
	
	public static SalesforceConnector of(
			UserInfo createdBy,
			Identity identity, 
			Organization organization, 
			ConnectionString connectionString, 
			Token token) {
		
		String name = organization.getName().concat(":").concat(organization.getId());
		Date now = Date.from(Instant.now());
		
		VaultEntry vaultEntry = VaultEntry.of(connectionString.get());
		dynamoDBMapper.save(vaultEntry);
		
		return new SalesforceConnector(
				name, 
				createdBy, 
				createdBy, 
				createdBy, 
				now, 
				now, 
				identity, 
				organization, 
				vaultEntry.getToken(), 
				Boolean.TRUE, 
				token.getInstanceUrl(),
				now);
	}
	
	public static SalesforceConnector of(
			SalesforceConnector instance, 
			UpdateSalesforceConnectorRequest request) {
		
		Preconditions.checkNotNull(instance, "instance");
		return new SalesforceConnector(instance, request);
	}
	
	private <T> SalesforceConnector(T document) {
		modelMapper.map(document, this);
	}
	
	public static SalesforceConnector of(MongoDocument document) {
		return new SalesforceConnector(document);
	}

	public String getName() {
		return name;
	}

	public UserInfo getCreatedBy() {
		return createdBy;
	}

	public UserInfo getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public UserInfo getOwner() {
		return owner;
	}

	public Identity getIdentity() {
		return identity;
	}

	public Organization getOrganization() {
		return organization;
	}
	
	public String getConnectionString() {
		return connectionString;
	}

	public Date getLastTestedOn() {
		return lastTestedOn;
	}

	public Boolean getIsValid() {
		return isValid;
	}

	private void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}

	public String getServiceEndpoint() {
		return serviceEndpoint;
	}

	public String getStatus() {
		return status;
	}

	public String getTag() {
		return tag;
	}
	
	public Set<Job> getJobs() {
		return jobs;
	}

	public void addJobs(Set<Job> jobs) {
		this.jobs = jobs;
	}

	public void addService(Service service) {
		if (services == null) {
			services = new HashSet<>();
		} else {
			if (services.contains(service)) {
				throw new IllegalArgumentException(String.format("Unable to add %s since it has already been added to Salesforce Connector", service.getName()));
			}
		}
		
		services.add(service);
	}
	
	public Service getService(String serviceId) {
		
		if (services == null) {
			services = new HashSet<>();
		}
		
		Optional<Service> optional = services.stream()
				.filter(s -> serviceId.equals(s.getServiceId()))
				.findFirst();
		
		if (! optional.isPresent()) {
			throw new IllegalArgumentException(String.format("Service Id: %s does not exist", serviceId));
		}
		
		return optional.get();
	}
	
	public Set<Service> getServices() {
		return services;
	}

	public Set<Sobject> getSobjects() {
		return sobjects;
	}

	public Theme getTheme() {
		return theme;
	}

	@Override
	public MongoDocument toDocument() {
		return modelMapper.map(this, com.nowellpoint.api.model.document.SalesforceConnector.class);
	}
	
	public void removeConnection() {
		String token = SalesforceConnectionString.of(this.connectionString).getCredentials();
		VaultEntry vaultEntry = VaultEntry.of(token);
		dynamoDBMapper.delete(vaultEntry);
	}
	
	public Token connect() {
		
		VaultEntry vaultEntry = dynamoDBMapper.load(VaultEntry.class, this.connectionString);
		
		SalesforceConnectionString salesforceConnectionString = SalesforceConnectionString.of(vaultEntry.getValue());
		
		Token token = null;
		try {
			token = login(salesforceConnectionString);
			setIsValid(Boolean.TRUE);
		} catch (OauthException e) {
			setIsValid(Boolean.FALSE);
			this.status = "Error: " + e.getErrorDescription();
		} catch (ConnectionException e) {
			setIsValid(Boolean.FALSE);
			this.status = "Error: " + e.getMessage();
		} finally {
			this.lastTestedOn = Date.from(Instant.now());
		}
		
		return token;
		
	}
	
	public DescribeSobjectResult describeSObject(String sobject) {
		Token token = connect();
		
		Client client = new Client();
		
		DescribeSobjectRequest request = new DescribeSobjectRequest()
				.withAccessToken(token.getAccessToken())
				.withSobject(sobject)
				.withSobjectsUrl(getIdentity().getUrls().getSobjects());
		
		DescribeSobjectResult result = client.describeSobject(request);
		
		return result;
	}
	
	public void build() {
		DescribeGlobalSobjectsResult describeGlobalSobjectsResult = describe();
		this.sobjects = describeGlobalSobjectsResult.getSobjects().stream().collect(Collectors.toSet());
		this.theme = describeTheme();
	}
	
	public DescribeGlobalSobjectsResult describe() {
		
		Token token = connect();
		
		Client client = new Client();
		
		DescribeGlobalSobjectsRequest describeGlobalSobjectsRequest = new DescribeGlobalSobjectsRequest()
				.setAccessToken(token.getAccessToken())
				.setSobjectsUrl(getIdentity().getUrls().getSobjects());
		
		DescribeGlobalSobjectsResult describeGlobalSobjectsResult = client.describeGlobal(describeGlobalSobjectsRequest);
		
		return describeGlobalSobjectsResult;
	}
	
	public Theme describeTheme() {
		
		Token token = connect();
		
		Client client = new Client();
		
		ThemeRequest themeRequest = new ThemeRequest()
				.withAccessToken(token.getAccessToken())
				.withRestEndpoint(getIdentity().getUrls().getRest());
		
		Theme theme = client.getTheme(themeRequest);
		
		return theme;
	}
	
	private Token login(SalesforceConnectionString connectionString) throws ConnectionException, OauthException {
		Token token = null;
		
		if (SalesforceConnectionString.PASSWORD.equals(connectionString.getGrantType())) {
			
			String[] credentials = connectionString.getCredentials().split(":");
			
			String authEndpoint = connectionString.getHostname();
			String username = credentials[0];
			String password = credentials[1];
			String securityToken = credentials[2];
			
			token = login(authEndpoint, username, password, securityToken);
			
			return token;
			
		} else {
			
			String refreshToken = connectionString.getCredentials();
			
			OauthAuthenticationResponse authenticationResponse = refreshToken(refreshToken);
			
			token = authenticationResponse.getToken();
			
			return token;
		}
	}
	
	private Token login(String authEndpoint, String username, String password, String securityToken) throws ConnectionException {
		ConnectorConfig config = new ConnectorConfig();
		config.setAuthEndpoint(String.format("%s/services/Soap/u/%s", authEndpoint, System.getProperty(Properties.SALESFORCE_API_VERSION)));
		config.setUsername(username);
		config.setPassword(password.concat(securityToken));
		
		try {
			PartnerConnection connection = newConnection(config);
			
			String id = String.format("%s/id/%s/%s", authEndpoint, connection.getUserInfo().getOrganizationId(), connection.getUserInfo().getUserId());
			String accessToken = connection.getConfig().getSessionId();
			String instanceUrl = connection.getConfig().getServiceEndpoint().substring(0, connection.getConfig().getServiceEndpoint().indexOf("/services"));
			String issuedAt = String.valueOf(connection.getServerTimestamp().getTimestamp().getTimeInMillis());
			String signature = DigitalSignature.sign(System.getenv("SALESFORCE_CLIENT_SECRET"), id.concat(issuedAt));
			
			Token token = new Token();
			token.setId(id);
			token.setAccessToken(accessToken);
			token.setInstanceUrl(instanceUrl);
			token.setIssuedAt(issuedAt);
			token.setTokenType("Bearer");
			token.setSignature(signature);
			
			return token;
			
		} catch (ConnectionException e) {
			if (e instanceof LoginFault) {
				LoginFault loginFault = (LoginFault) e;
				throw new ConnectionException(loginFault.getExceptionCode().name().concat(": ").concat(loginFault.getExceptionMessage()));
			} else {
				throw e;
			}
		}
	}
	
	private OauthAuthenticationResponse refreshToken(String refreshToken) {
		RefreshTokenGrantRequest request = OauthRequests.REFRESH_TOKEN_GRANT_REQUEST.builder()
				.setClientId(System.getProperty(Properties.SALESFORCE_CLIENT_ID))
				.setClientSecret(System.getProperty(Properties.SALESFORCE_CLIENT_SECRET))
				.setRefreshToken(refreshToken)
				.build();
		
		OauthAuthenticationResponse authenticationResponse = Authenticators.REFRESH_TOKEN_GRANT_AUTHENTICATOR
				.authenticate(request);
		
		return authenticationResponse;
	}
}