package com.nowellpoint.cognito;

import org.junit.Test;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.DeliveryMediumType;
import com.amazonaws.services.cognitoidp.model.TooManyRequestsException;
import com.amazonaws.services.cognitoidp.model.UsernameExistsException;

public class TestCongnito {
	
	@Test
	public void testCreateUser() {
		
		AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();
		
		try {
            AdminCreateUserRequest cognitoRequest = new AdminCreateUserRequest()
                    .withUserPoolId("us-east-1_BfMr4G4nH")
                    .withUsername("john.d.herson@gmail.com")
                    .withUserAttributes(
                            new AttributeType()
                                .withName("email")
                                .withValue("john.d.herson@gmail.com"),
                            new AttributeType()
                                .withName("email_verified")
                                .withValue("true"))
                    .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL)
                    .withForceAliasCreation(Boolean.FALSE);
 
            AdminCreateUserResult result = cognitoClient.adminCreateUser(cognitoRequest);


        } catch (UsernameExistsException e) {
        	e.printStackTrace();
        } catch (TooManyRequestsException e) {
            e.printStackTrace();
        }
	}
}