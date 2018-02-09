package com.nowellpoint.api.service.impl;

import static com.mongodb.client.model.Filters.eq;
import static com.nowellpoint.util.Assert.assertNotNull;
import static com.nowellpoint.util.Assert.isNotNullOrEmpty;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.bson.types.ObjectId;

import com.nowellpoint.api.rest.domain.Connector;
import com.nowellpoint.api.rest.domain.ConnectorList;
import com.nowellpoint.api.rest.domain.ConnectorRequest;
import com.nowellpoint.api.rest.domain.ConnectorType;
import com.nowellpoint.api.rest.domain.SalesforceAdapter;
import com.nowellpoint.api.rest.domain.UserInfo;
import com.nowellpoint.api.util.ClaimsContext;
import com.nowellpoint.api.util.MessageConstants;
import com.nowellpoint.api.util.MessageProvider;
import com.nowellpoint.api.util.UserContext;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.document.MongoDocument;
import com.nowellpoint.util.Assert;

public class AbstractConnectorService extends AbstractCacheService {
	
	
	
	@Inject
	protected DocumentManagerFactory documentManagerFactory;
	
	private static final Map<String,ConnectorType> connectorTypes = createTypeMap();

	private static Map<String,ConnectorType> createTypeMap() {
		
		List<ConnectorType> connectorTypeList = new ArrayList<ConnectorType>();
		
		connectorTypeList.add(ConnectorType.builder()
				.name("SALESFORCE_SANDBOX")
				.scheme("salesforce")
				.grantType("password")
				.displayName("Salesforce Sandbox")
				.authEndpoint("https://test.salesforce.com")
				.iconHref("https://d3iep6okqojnln.cloudfront.net/salesforce-logo.png")
				.isSandbox(Boolean.TRUE)
				.build());
		
		connectorTypeList.add(ConnectorType.builder()
				.name("SALESFORCE_PRODUCTION")
				.scheme("salesforce")
				.grantType("password")
				.displayName("Salesforce Production")
				.authEndpoint("https://login.salesforce.com")
				.iconHref("https://d3iep6okqojnln.cloudfront.net/salesforce-logo.png")
				.isSandbox(Boolean.FALSE)
				.build());
		
		return Collections.unmodifiableMap(connectorTypeList.stream().collect(Collectors.toMap(t -> t.getName(), t -> t)));
	}
	
	protected Connector retrieve(String id) {		
		com.nowellpoint.api.model.document.Connector document = get(com.nowellpoint.api.model.document.Connector.class, id );
		if (Assert.isNull(document)) {
			DocumentManager documentManager = documentManagerFactory.createDocumentManager();
			document = documentManager.fetch(com.nowellpoint.api.model.document.Connector.class, new ObjectId( id ) );
			set(id, document);
		}
		Connector resource = Connector.of( document );
		return resource;
	}
	
	protected ConnectorList findAllByOwner(String ownerId) {
		DocumentManager documentManager = documentManagerFactory.createDocumentManager(); 
		Set<com.nowellpoint.api.model.document.Connector> documents = documentManager.find(
				com.nowellpoint.api.model.document.Connector.class,
				eq ( "owner", new ObjectId( ownerId ) ) );
		ConnectorList resources = new ConnectorList( documents );
		return resources;
	}
	
	protected void create(Connector connector) {
		MongoDocument document = connector.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.insertOne( document );
		connector.replace(document);
		set(connector.getId(), document);
	}
	
	protected void update(Connector connector) {
		MongoDocument document = connector.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.replaceOne( document );
		connector.replace(document);
		set(connector.getId(), document);
	}
	
	protected void delete(Connector connector) {
		MongoDocument document = connector.toDocument();
		DocumentManager documentManager = documentManagerFactory.createDocumentManager();
		documentManager.deleteOne(document);
		del(connector.getId());
	}
	
	protected Connector refresh(Connector connector) {
		
		if (! connector.getIsConnected()) {
			throw new IllegalArgumentException(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_CANNOT_REFRESH));
		}
		
		if ("SALESFORCE_SANDBOX".equals(connector.getConnectorType().getName()) || "SALESFORCE_PRODUCTION".equals(connector.getConnectorType().getName())) {
			
			SalesforceAdapter adapter = SalesforceAdapter.builder()
					.connector(connector)
					.status(Connector.CONNECTED)
					.build();
				
			return adapter.toConnector();	
		}
		
		return null;		
	}
	
	protected Connector disconnect(Connector connector) {
		
		if (! connector.getIsConnected()) {
			throw new IllegalArgumentException(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_ALREADY_DISCONNECTED));
		}
		
		UserInfo who = UserInfo.of(UserContext.getPrincipal().getName());
		
		Date now = Date.from(Instant.now());
		
		return Connector.builder()
				.from(connector)
				.lastUpdatedBy(who)
				.lastUpdatedOn(now)
				.connectedAs(null)
				.connectedOn(null)
				.username(null)
				.password(null)
				.clientId(null)
				.clientSecret(null)
				.status(Connector.DISCONNECTED)
				.isConnected(Boolean.FALSE)
				.build();
	}
	
	protected Connector build(Connector original, ConnectorRequest request) {
		
		if (original.getIsConnected()) {
			
			return Connector.builder()
					.from(original)
					.name(request.getName())
					.lastUpdatedBy(UserInfo.of(ClaimsContext.getClaims()))
					.lastUpdatedOn(Date.from(Instant.now()))
					.build();
		}
		
		if ("SALESFORCE_SANDBOX".equals(original.getConnectorType().getName()) || "SALESFORCE_PRODUCTION".equals(original.getConnectorType().getName())) {
			
			ConnectorType connectorType = null;
			
			if (isNotNullOrEmpty(request.getType())) {
				connectorType = getConnectorType(request.getType());
			}

			SalesforceAdapter adapter = SalesforceAdapter.builder()
					.connectorType(connectorType)
					.clientId(request.getClientId())
					.clientSecret(request.getClientSecret())
					.connector(original)
					.name(request.getName())
					.password(request.getPassword())
					.username(request.getUsername())
					.status(isNotNullOrEmpty(request.getStatus()) ? request.getStatus() : null)
					.build();
				
			return adapter.toConnector();
		}
		
		return null;
	}
	
	protected Connector build(ConnectorRequest request) {
		
		ConnectorType connectorType = getConnectorType(request.getType());
		
		if ("SALESFORCE_SANDBOX".equals(connectorType.getName()) || "SALESFORCE_PRODUCTION".equals(connectorType.getName())) {
			
			String status = "connect".equals(request.getStatus()) ? Connector.CONNECTED : Connector.NOT_CONNECTED;
			
			SalesforceAdapter salesforceConnector = SalesforceAdapter.builder()
					.connectorType(connectorType)
					.clientId(request.getClientId())
					.clientSecret(request.getClientSecret())
					.name(isNotNullOrEmpty(request.getName()) ? request.getName() : "New Salesforce Connector")
					.password(request.getPassword())
					.username(request.getUsername())
					.status(status)
					.build();
			
			return salesforceConnector.toConnector();
		}	
		
		return null;
	}
	
	protected Connector connect(Connector original, ConnectorRequest request) {
		
		if ("SALESFORCE_SANDBOX".equals(original.getConnectorType().getName()) || "SALESFORCE_PRODUCTION".equals(original.getConnectorType().getName())) {
				
			SalesforceAdapter adapter = SalesforceAdapter.builder()
					.connector(original)
					.clientId(request.getClientId())
					.clientSecret(request.getClientSecret())
					.name(request.getName())
					.password(request.getPassword())
					.username(request.getUsername())
					.status(Connector.CONNECTED)
					.build();
				
			return adapter.toConnector();
		}
		
		return null;
	}
	
	private ConnectorType getConnectorType(String type) {
		ConnectorType connectorType = connectorTypes.get(type);
		assertNotNull(connectorType, String.format(MessageProvider.getMessage(Locale.getDefault(), MessageConstants.CONNECTOR_INVALID_TYPE), type));
		return connectorType;
	}
}