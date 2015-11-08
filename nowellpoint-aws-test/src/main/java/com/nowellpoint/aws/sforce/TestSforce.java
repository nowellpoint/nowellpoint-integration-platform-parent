package com.nowellpoint.aws.sforce;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.http.HttpResponse;
import com.nowellpoint.aws.http.RestResource;
import com.nowellpoint.aws.lambda.sforce.model.Field;
import com.nowellpoint.aws.lambda.sforce.model.Identity;
import com.nowellpoint.aws.lambda.sforce.model.SObjectMapping;
import com.nowellpoint.aws.lambda.sforce.model.Token;

public class TestSforce {

	@Test
	public void main() {
		
		HttpResponse response;
		try {
			response = RestResource.post("https://rsbjc5t3q4.execute-api.us-east-1.amazonaws.com/v1/salesforce/oauth/token")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.accept(MediaType.APPLICATION_JSON)
					.parameter("username", System.getenv("SALESFORCE_USERNAME"))
					.parameter("password", System.getenv("SALESFORCE_PASSWORD").concat(System.getenv("SALESFORCE_SECURITY_TOKEN")))
					.execute();
			
			Token token = response.getEntity(Token.class);
			
			response = RestResource.get(token.getId())
					.queryParameter("version", "latest")
					.acceptCharset(StandardCharsets.UTF_8)
					.bearerAuthorization(token.getAccessToken())
					.accept(MediaType.APPLICATION_JSON)
					.execute();
			
			Identity identity = response.getEntity(Identity.class);
			
			System.out.println(new ObjectMapper().writeValueAsString(identity));
			
			response = RestResource.get(identity.getUrls().getSObjects())
					.path("Opportunity/describe")
					.accept(MediaType.APPLICATION_JSON)
					.bearerAuthorization(token.getAccessToken())
					.execute();
			
			System.out.println(response.getURL());
			
			SObjectMapping sobjectDefinition = response.getEntity(SObjectMapping.class);
			
			System.out.println(sobjectDefinition.getName());
			
			List<Field> fields = sobjectDefinition.getFields();
			
			Set<String> fieldSet = new HashSet<String>();
			for (Field field : fields) {
				if ( field.getCustom() && (field.getMapped() != null ? field.getMapped() : true ) ) {
					fieldSet.add( field.getName() );
				} else if (field.getRelationshipName() != null) {
					fieldSet.add(field.getRelationshipName().concat(".").concat("Id"));
				} else {
					fieldSet.add( field.getName() );
				}
			}
			
			StringBuilder sb = new StringBuilder().append( "SELECT " )
					.append( fieldSet.stream()
							.map( field -> field )
							.collect( Collectors.joining (",") ) )
					.append( " FROM " )
					.append( sobjectDefinition.getName() );
			
			System.out.println(sb.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}