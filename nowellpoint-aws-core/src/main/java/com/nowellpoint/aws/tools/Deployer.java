package com.nowellpoint.aws.tools;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaAsync;
import com.amazonaws.services.lambda.AWSLambdaAsyncClient;
import com.amazonaws.services.lambda.model.CreateFunctionRequest;
import com.amazonaws.services.lambda.model.CreateFunctionResult;
import com.amazonaws.services.lambda.model.FunctionCode;
import com.amazonaws.services.lambda.model.GetFunctionRequest;
import com.amazonaws.services.lambda.model.GetFunctionResult;
import com.amazonaws.services.lambda.model.ResourceNotFoundException;
import com.amazonaws.services.lambda.model.UpdateFunctionCodeRequest;
import com.amazonaws.services.lambda.model.UpdateFunctionConfigurationRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.tools.model.Code;
import com.nowellpoint.aws.tools.model.Lambda;
import com.nowellpoint.aws.tools.model.Function;

public class Deployer {
	
	private static AmazonS3 s3Client = new AmazonS3Client(new EnvironmentVariableCredentialsProvider());
	private static AWSLambdaAsync lambdaClient = new AWSLambdaAsyncClient(new EnvironmentVariableCredentialsProvider());
	
	public static void main(String[] args) {
		System.out.println(new Date() + " running deployer for: ".concat(args[0]).concat(" with config file: " + args[1]));
		Deployer deployer = new Deployer();
		deployer.doDeploy(new File(args[0]), new File(args[1]));
	}

	private void doDeploy(File jar, File config) {
		
		s3Client.setRegion(Region.getRegion(Regions.US_EAST_1));
		lambdaClient.setRegion(Region.getRegion(Regions.US_EAST_1));
			
		PutObjectRequest objectRequest = new PutObjectRequest("aws-microservices", jar.getName(), jar);
	    	
		s3Client.putObject(objectRequest);
	    	
		try {
			List<Function> functions = new ObjectMapper().readValue(config, new TypeReference<List<Function>>() { });
			functions.forEach( f -> {
				
				Lambda configuration = f.getConfiguration();
				Code code = f.getCode();
				
				System.out.println(new Date() + " " + configuration.getFunctionName() );
				
				GetFunctionRequest functionRequest = new GetFunctionRequest().withFunctionName(configuration.getFunctionName());
				
				try {
					
					GetFunctionResult functionResult = lambdaClient.getFunction(functionRequest);
					
					System.out.println(new Date() + " " + functionResult.getConfiguration().getFunctionArn());
					
					UpdateFunctionConfigurationRequest updateFunctionConfigurationRequest = new UpdateFunctionConfigurationRequest().withFunctionName(configuration.getFunctionName())
							.withHandler(configuration.getHandler())
							.withDescription(configuration.getDescription())
							.withMemorySize(configuration.getMemorySize())
							.withTimeout(configuration.getTimeout());
					
					lambdaClient.updateFunctionConfiguration(updateFunctionConfigurationRequest);
					
					UpdateFunctionCodeRequest updateFunctionCodeRequest = new UpdateFunctionCodeRequest().withFunctionName(configuration.getFunctionName())
							.withS3Bucket(code.getS3Bucket())
							.withS3Key(code.getS3Key())
							.withPublish(Boolean.TRUE)
							.withS3ObjectVersion(code.getS3ObjectVersion());
					
					lambdaClient.updateFunctionCode(updateFunctionCodeRequest);
					
				} catch (ResourceNotFoundException e) {
					
					FunctionCode functionCode = new FunctionCode().withS3Bucket(code.getS3Bucket())
							.withS3Key(code.getS3Key())
							.withS3ObjectVersion(code.getS3ObjectVersion());
					
					CreateFunctionRequest createFunctionRequest = new CreateFunctionRequest().withCode(functionCode)
							.withDescription(configuration.getDescription())
							.withFunctionName(configuration.getFunctionName())
							.withHandler(configuration.getHandler())
							.withMemorySize(configuration.getMemorySize())
							.withPublish(Boolean.TRUE)
							.withRole(configuration.getRole())
							.withRuntime(configuration.getRuntime())
							.withTimeout(configuration.getTimeout());
					
					CreateFunctionResult createFunctionResult = lambdaClient.createFunction(createFunctionRequest);
					
					System.out.println(new Date() + " " + createFunctionResult.getFunctionArn());
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}