package com.nowellpoint.client.resource;

import java.util.List;

import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.MediaType;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.http.Status;
import com.nowellpoint.client.model.SalesforceConnector;
import com.nowellpoint.client.model.SalesforceConnectorRequest;
import com.nowellpoint.client.model.Token;
import com.nowellpoint.client.model.UpdateResult;
import com.nowellpoint.client.model.CreateResult;
import com.nowellpoint.client.model.CreateSalesforceConnectorRequest;
import com.nowellpoint.client.model.DeleteResult;
import com.nowellpoint.client.model.Environment;
import com.nowellpoint.client.model.EnvironmentRequest;
import com.nowellpoint.client.model.Error;
import com.nowellpoint.client.model.GetResult;
import com.nowellpoint.client.model.NotFoundException;
import com.nowellpoint.client.model.SObjectDetail;

public class SalesforceConnectorResource extends AbstractResource {
	
	private static final String RESOURCE_CONTEXT = "connectors";
	
	public SalesforceConnectorResource(Token token) {
		super(token);
	}
	
	public CreateResult<SalesforceConnector> create(CreateSalesforceConnectorRequest request) {
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
    			.parameter("id", request.getId())
    			.parameter("instanceUrl", request.getInstanceUrl())
    			.parameter("accessToken", request.getAccessToken())
    			.parameter("refreshToken", request.getRefreshToken())
    			.execute();
		
		CreateResult<SalesforceConnector> result = null;
    	
    	if (httpResponse.getStatusCode() == Status.CREATED) {
    		SalesforceConnector resource = httpResponse.getEntity(SalesforceConnector.class);
    		result = new CreateResultImpl<SalesforceConnector>(resource);
    	} else {
    		Error error = httpResponse.getEntity(Error.class);
    		result = new CreateResultImpl<SalesforceConnector>(error);
    	}
    	
    	return result;
	}
	
	public GetResult<List<SalesforceConnector>> getSalesforceConnectors() {
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
    			.execute();
		
		GetResult<List<SalesforceConnector>> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			List<SalesforceConnector> resources = httpResponse.getEntityList(SalesforceConnector.class);
    		result = new GetResultImpl<List<SalesforceConnector>>(resources); 
    	} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
    		Error error = httpResponse.getEntity(Error.class);
			result = new GetResultImpl<List<SalesforceConnector>>(error);
    	}
    	
    	return result;
	}
	
	public GetResult<SalesforceConnector> get(String id) {
		HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
				.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
				.bearerAuthorization(token.getAccessToken())
				.path(RESOURCE_CONTEXT)
    			.path("salesforce")
    			.path(id)
    			.execute();
		
		GetResult<SalesforceConnector> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			SalesforceConnector resource = httpResponse.getEntity(SalesforceConnector.class);
    		result = new GetResultImpl<SalesforceConnector>(resource); 
    	} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else {
    		Error error = httpResponse.getEntity(Error.class);
			result = new GetResultImpl<SalesforceConnector>(error);
    	}
    	
    	return result;
	}
	
	public UpdateResult<SalesforceConnector> update(String id, SalesforceConnectorRequest salesforceConnectorRequest) {
		HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
				.header("Content-Type", "application/x-www-form-urlencoded")
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
    			.path(id)
    			.parameter("tag", salesforceConnectorRequest.getTag())
    			.parameter("name", salesforceConnectorRequest.getName())
    			.execute();
		
		UpdateResult<SalesforceConnector> result = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			SalesforceConnector resource = httpResponse.getEntity(SalesforceConnector.class);
			result = new UpdateResultImpl<SalesforceConnector>(resource);  
		} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
			throw new NotFoundException(httpResponse.getAsString());
		} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
			Error error = httpResponse.getEntity(Error.class);
			result = new UpdateResultImpl<SalesforceConnector>(error);
		}
		
		return result;
	}
	
	public DeleteResult delete(String id) {
		HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
				.bearerAuthorization(token.getAccessToken())
				.path("connectors")
    			.path("salesforce")
    			.path(id)
    			.execute();
		
		DeleteResult deleteResult = null;
		
		if (httpResponse.getStatusCode() == Status.OK) {
			deleteResult = new DeleteResultImpl();
		} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
			Error error = httpResponse.getEntity(Error.class);
			deleteResult = new DeleteResultImpl(error);
		}
		
		return deleteResult;
	}
	
	public EnvironmentResource environment() {
		return new EnvironmentResource(token);
	}
	
	public class SObjectDetailResource extends AbstractResource {
		
		public SObjectDetailResource(Token token) {
			super(token);
		}
		
		public GetResult<SObjectDetail> get(String salesforceConnectorId, String key, String sobjectName) {
			HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
					.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
					.bearerAuthorization(token.getAccessToken())
					.path(RESOURCE_CONTEXT)
	    			.path("salesforce")
	    			.path(salesforceConnectorId)
	    			.path("environment")
	    			.path(key)
	    			.path("sobject")
	    			.path(sobjectName)
	    			.execute();
			
			GetResult<SObjectDetail> result = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				SObjectDetail resource = httpResponse.getEntity(SObjectDetail.class);
				result = new GetResultImpl<SObjectDetail>(resource);  
			} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
				throw new NotFoundException(httpResponse.getAsString());
			} 
			
			return result;
		}
	}
	
	public class EnvironmentResource extends AbstractResource {
		
		public EnvironmentResource(Token token) {
			super(token);
		}
		
		public GetResult<Environment> get(String salesforceConnectorId, String key) {
			HttpResponse httpResponse = RestResource.get(API_ENDPOINT)
					.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
					.bearerAuthorization(token.getAccessToken())
					.path(RESOURCE_CONTEXT)
	    			.path("salesforce")
	    			.path(salesforceConnectorId)
	    			.path("environment")
	    			.path(key)
	    			.execute();
			
			GetResult<Environment> result = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				Environment resource = httpResponse.getEntity(Environment.class);
				result = new GetResultImpl<Environment>(resource);  
			} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
				throw new NotFoundException(httpResponse.getAsString());
			} 
			
			return result;
		}
		
		public CreateResult<Environment> add(String salesforceConnectorId, EnvironmentRequest environmentRequest) {
			Environment environment = new Environment()
					.withIsActive(environmentRequest.getIsActive())
					.withAuthEndpoint(environmentRequest.getAuthEndpoint())
					.withEnvironmentName(environmentRequest.getEnvironmentName())
					.withPassword(environmentRequest.getPassword())
					.withUsername(environmentRequest.getUsername())
					.withSecurityToken(environmentRequest.getSecurityToken());
			
			HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
					.path("connectors")
	    			.path("salesforce")
	    			.path(salesforceConnectorId)
	    			.path("environment")
	    			.body(environment)
					.execute();
			
			CreateResult<Environment> result = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				Environment resource = httpResponse.getEntity(Environment.class);
				result = new CreateResultImpl<Environment>(resource);  
			} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
				Error error = httpResponse.getEntity(Error.class);
				result = new CreateResultImpl<Environment>(error);
			}
			
			return result;
		}
		
		public UpdateResult<Environment> update(String salesforceConnectorId, String key, EnvironmentRequest environmentRequest) {
			Environment environment = new Environment()
					.withIsActive(environmentRequest.getIsActive())
					.withAuthEndpoint(environmentRequest.getAuthEndpoint())
					.withEnvironmentName(environmentRequest.getEnvironmentName())
					.withPassword(environmentRequest.getPassword())
					.withUsername(environmentRequest.getUsername())
					.withSecurityToken(environmentRequest.getUsername());
			
			HttpResponse httpResponse = RestResource.put(API_ENDPOINT)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
					.path("connectors")
	    			.path("salesforce")
	    			.path(salesforceConnectorId)
	    			.path("environment")
	    			.path(key)
	    			.body(environment)
					.execute();
			
			UpdateResult<Environment> result = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				Environment resource = httpResponse.getEntity(Environment.class);
				result = new UpdateResultImpl<Environment>(resource);  
			} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
				throw new NotFoundException(httpResponse.getAsString());
			} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
				Error error = httpResponse.getEntity(Error.class);
				result = new UpdateResultImpl<Environment>(error);
			}
			
			return result;
		}
		
		public DeleteResult delete(String salesforceConnectorId, String key) {
			HttpResponse httpResponse = RestResource.delete(API_ENDPOINT)
					.bearerAuthorization(token.getAccessToken())
					.path("connectors")
	    			.path("salesforce")
	    			.path(salesforceConnectorId)
	    			.path("environment")
	    			.path(key)
					.execute();
			
			DeleteResult deleteResult = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				deleteResult = new DeleteResultImpl();
			} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
				Error error = httpResponse.getEntity(Error.class);
				deleteResult = new DeleteResultImpl(error);
			}
			
			return deleteResult;
		}
		
		public UpdateResult<Environment> test(String salesforceConnectorId, String key) {
			HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
					.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
					.bearerAuthorization(token.getAccessToken())
					.path(RESOURCE_CONTEXT)
	    			.path("salesforce")
	    			.path(salesforceConnectorId)
	    			.path("environment")
	    			.path(key)
	    			.path("actions")
	    			.path("test")
	    			.path("invoke")
	    			.execute();
			
			UpdateResult<Environment> result = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				Environment resource = httpResponse.getEntity(Environment.class);
				result = new UpdateResultImpl<Environment>(resource);  
			} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
				throw new NotFoundException(httpResponse.getAsString());
			} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
				Error error = httpResponse.getEntity(Error.class);
				result = new UpdateResultImpl<Environment>(error);
			}
			
			return result;
		}
		
		public UpdateResult<Environment> build(String salesforceConnectorId, String key) {
			HttpResponse httpResponse = RestResource.post(API_ENDPOINT)
					.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
					.bearerAuthorization(token.getAccessToken())
					.path(RESOURCE_CONTEXT)
	    			.path("salesforce")
	    			.path(salesforceConnectorId)
	    			.path("environment")
	    			.path(key)
	    			.path("actions")
	    			.path("build")
	    			.path("invoke")
	    			.execute();
			
			UpdateResult<Environment> result = null;
			
			if (httpResponse.getStatusCode() == Status.OK) {
				Environment resource = httpResponse.getEntity(Environment.class);
				result = new UpdateResultImpl<Environment>(resource);  
			} else if (httpResponse.getStatusCode() == Status.NOT_FOUND) {
				throw new NotFoundException(httpResponse.getAsString());
			} else if (httpResponse.getStatusCode() == Status.BAD_REQUEST) {
				Error error = httpResponse.getEntity(Error.class);
				result = new UpdateResultImpl<Environment>(error);
			}
			
			return result;
		}
		
		public SObjectDetailResource sobjectDetail() {
			return new SObjectDetailResource(token);
		}
	}
}