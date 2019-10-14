package com.nowellpoint.client.sforce.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.nowellpoint.client.sforce.Authenticators;
import com.nowellpoint.client.sforce.OauthAuthenticationResponse;
import com.nowellpoint.client.sforce.OauthException;
import com.nowellpoint.client.sforce.OauthRequests;
import com.nowellpoint.client.sforce.Salesforce;
import com.nowellpoint.client.sforce.SalesforceClientBuilder;
import com.nowellpoint.client.sforce.UsernamePasswordGrantRequest;
import com.nowellpoint.client.sforce.model.Identity;
import com.nowellpoint.client.sforce.model.Token;
import com.nowellpoint.util.SecretsManager;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sforce.soap.metadata.AsyncResult;
import com.sforce.soap.metadata.CodeCoverageWarning;
import com.sforce.soap.metadata.DeployDetails;
import com.sforce.soap.metadata.DeployMessage;
import com.sforce.soap.metadata.DeployOptions;
import com.sforce.soap.metadata.DeployResult;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.metadata.RunTestFailure;
import com.sforce.soap.metadata.RunTestsResult;

public class TestMetadataApi {
	
	private static final long ONE_SECOND = 1000;
	private static final int MAX_NUM_POLL_REQUESTS = 50;

	@Test
	public void testDeployChangeEvent() {
		
		UsernamePasswordGrantRequest request = OauthRequests.PASSWORD_GRANT_REQUEST.builder()
				.setClientId(SecretsManager.getSalesforceClientId())
				.setClientSecret(SecretsManager.getSalesforceClientSecret())
				.setUsername(System.getenv("SALESFORCE_USERNAME"))
				.setPassword(System.getenv("SALESFORCE_PASSWORD"))
				.setSecurityToken(System.getenv("SALESFORCE_SECURITY_TOKEN"))
				.build();
		
		try {
			OauthAuthenticationResponse response = Authenticators.PASSWORD_GRANT_AUTHENTICATOR
					.authenticate(request);
			
			Token token = response.getToken();
			
			Salesforce client = SalesforceClientBuilder.defaultClient(token);
			
			Identity identity = client.getIdentity();
			
			System.out.println("connected as: " + identity.getUsername());
			
			final ConnectorConfig config = new ConnectorConfig();
			config.setServiceEndpoint(identity.getUrls().getMetadata());
			config.setSessionId(token.getAccessToken());
			MetadataConnection connection = new MetadataConnection(config);
			
			deployZip(connection);
			
		} catch (OauthException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getMessage());
			System.out.println(e.getErrorDescription());
		} catch (ConnectionException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 	
	}
	
	private void deployZip(MetadataConnection connection) throws Exception {
        byte zipBytes[] = readZipFile();
        DeployOptions deployOptions = new DeployOptions();
        deployOptions.setPerformRetrieve(false);
        deployOptions.setRollbackOnError(true);
        AsyncResult asyncResult = connection.deploy(zipBytes, deployOptions);
        DeployResult result = waitForDeployCompletion(connection, asyncResult.getId());
        if (!result.isSuccess()) {
            printErrors(result, "Final list of failures:\n");
            throw new Exception("The files were not successfully deployed");
        }
        System.out.println("The file package.xml was successfully deployed\n");
    }
	
	private DeployResult waitForDeployCompletion(MetadataConnection connection, String asyncResultId) throws Exception {
        int poll = 0;
        long waitTimeMilliSecs = ONE_SECOND;
        DeployResult deployResult;
        boolean fetchDetails;
        do {
            Thread.sleep(waitTimeMilliSecs);
            waitTimeMilliSecs *= 2;
            if (poll++ > MAX_NUM_POLL_REQUESTS) {
                throw new Exception(
                    "Request timed out. If this is a large set of metadata components, " +
                    "ensure that MAX_NUM_POLL_REQUESTS is sufficient.");
            }

            fetchDetails = (poll % 3 == 0);

            deployResult = connection.checkDeployStatus(asyncResultId, fetchDetails);
            System.out.println("Status is: " + deployResult.getStatus());
            if (!deployResult.isDone() && fetchDetails) {
                printErrors(deployResult, "Failures for deployment in progress:\n");
            }
        }
        while (!deployResult.isDone());

        if (!deployResult.isSuccess() && deployResult.getErrorStatusCode() != null) {
            throw new Exception(deployResult.getErrorStatusCode() + " msg: " +
                    deployResult.getErrorMessage());
        }
        
        if (!fetchDetails) {
            deployResult = connection.checkDeployStatus(asyncResultId, true);
        }
        
        return deployResult;
    }
	
	private void printErrors(DeployResult result, String messageHeader) {
        DeployDetails details = result.getDetails();
        StringBuilder stringBuilder = new StringBuilder();
        if (details != null) {
            DeployMessage[] componentFailures = details.getComponentFailures();
            for (DeployMessage failure : componentFailures) {
                String loc = "(" + failure.getLineNumber() + ", " + failure.getColumnNumber();
                if (loc.length() == 0 && !failure.getFileName().equals(failure.getFullName()))
                {
                    loc = "(" + failure.getFullName() + ")";
                }
                stringBuilder.append(failure.getFileName() + loc + ":" 
                    + failure.getProblem()).append('\n');
            }
            RunTestsResult rtr = details.getRunTestResult();
            if (rtr.getFailures() != null) {
                for (RunTestFailure failure : rtr.getFailures()) {
                    String n = (failure.getNamespace() == null ? "" :
                        (failure.getNamespace() + ".")) + failure.getName();
                    stringBuilder.append("Test failure, method: " + n + "." +
                            failure.getMethodName() + " -- " + failure.getMessage() + 
                            " stack " + failure.getStackTrace() + "\n\n");
                }
            }
            if (rtr.getCodeCoverageWarnings() != null) {
                for (CodeCoverageWarning ccw : rtr.getCodeCoverageWarnings()) {
                    stringBuilder.append("Code coverage issue");
                    if (ccw.getName() != null) {
                        String n = (ccw.getNamespace() == null ? "" :
                        (ccw.getNamespace() + ".")) + ccw.getName();
                        stringBuilder.append(", class: " + n);
                    }
                    stringBuilder.append(" -- " + ccw.getMessage() + "\n");
                }
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.insert(0, messageHeader);
            System.out.println(stringBuilder.toString());
        }
    }
	
	private byte[] readZipFile() throws Exception {
        byte[] result = null;
        
        Path resources = Paths.get("src","test","resources");
        File zipFile = new File(resources + "/deployment.zip");
        if (!zipFile.exists() || !zipFile.isFile()) {
            throw new Exception("Cannot find the zip file for deploy() on path:"
                + zipFile.getAbsolutePath());
        }

        FileInputStream fileInputStream = new FileInputStream(zipFile);
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead = 0;
            while (-1 != (bytesRead = fileInputStream.read(buffer))) {
                bos.write(buffer, 0, bytesRead);
            }

            result = bos.toByteArray();
        } finally {
            fileInputStream.close();
        }
        return result;
    }
}