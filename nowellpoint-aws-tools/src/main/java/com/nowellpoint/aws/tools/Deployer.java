package com.nowellpoint.aws.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

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
import com.nowellpoint.aws.tools.model.Configuration;
import com.nowellpoint.aws.tools.model.Function;

public class Deployer {
	
	private static Logger logger = Logger.getLogger(Deployer.class.getName());
	private static AmazonS3 s3Client = new AmazonS3Client(new EnvironmentVariableCredentialsProvider());
	private static AWSLambdaAsync lambdaClient = new AWSLambdaAsyncClient(new EnvironmentVariableCredentialsProvider());

	//public static void main(String[] args) {
	public void doDeploy(File jar, File function) {
		
		//String[] jars = {
		//		"../nowellpoint-aws/nowellpoint-aws-idp/target/nowellpoint-aws-idp-0.0.2-SNAPSHOT.jar",
		//		"../nowellpoint-aws/nowellpoint-aws-sforce/target/nowellpoint-aws-sforce-0.0.2-SNAPSHOT.jar",
		//		"../nowellpoint-aws/nowellpoint-aws-util/target/nowellpoint-aws-util-0.0.2-SNAPSHOT.jar"
		//};
		
		s3Client.setRegion(Region.getRegion(Regions.US_EAST_1));
		lambdaClient.setRegion(Region.getRegion(Regions.US_EAST_1));
		
		//long startTime = System.currentTimeMillis();
		
		//MongoClientURI mongoClientURI = new MongoClientURI("mongodb://".concat(System.getenv("MONGO_CLIENT_URI")));
		//MongoClient mongoClient = new MongoClient(mongoClientURI);
		//MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoClientURI.getDatabase());
		
		//logger.info("connect time: " + (System.currentTimeMillis() - startTime));
		
		//Arrays.asList(jars).forEach( jar -> {
			
			//File file = new File(jar);
			
		PutObjectRequest objectRequest = new PutObjectRequest("aws-microservices", jar.getName(), jar);
	    	
		s3Client.putObject(objectRequest);
	    	
		//} );
		
		//FindIterable<Document> documents = mongoDatabase.getCollection("aws.lambda.functions").find();
			
		try {
			List<Function> functions = new ObjectMapper().readValue(function, new TypeReference<List<Function>>() { });
			functions.forEach( f -> {
				
				Configuration configuration = f.getConfiguration();
				Code code = f.getCode();
				
				logger.info( configuration.getFunctionName() );
				
				GetFunctionRequest functionRequest = new GetFunctionRequest().withFunctionName(configuration.getFunctionName());
				
				try {
					
					GetFunctionResult functionResult = lambdaClient.getFunction(functionRequest);
					
					logger.info(functionResult.getConfiguration().getFunctionArn());
					
					UpdateFunctionConfigurationRequest updateFunctionConfigurationRequest = new UpdateFunctionConfigurationRequest().withFunctionName(configuration.getFunctionName())
							.withHandler(configuration.getHandler())
							.withDescription(configuration.getDescription())
							.withMemorySize(configuration.getMemorySize())
							.withTimeout(configuration.getTimeout());
					
					lambdaClient.updateFunctionConfiguration(updateFunctionConfigurationRequest);
					
					UpdateFunctionCodeRequest updateFunctionCodeRequest = new UpdateFunctionCodeRequest().withFunctionName(configuration.getFunctionName())
							.withS3Bucket(code.getS3Bucket())
							.withS3Key(code.getS3Key())
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
					
					logger.info(createFunctionResult.getFunctionArn());
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		
		
//		for (Document document : documents) {
//			Document configuration = document.get("configuration", Document.class);
//			Document code = document.get("code", Document.class);
//			
//			String functionName = configuration.getString("functionName");
//			String handler = configuration.getString("handler");
//			String description = configuration.getString("description");
//			Integer memorySize = configuration.getInteger("memorySize");
//			Integer timeout = configuration.getInteger("timeout");
//			String role = configuration.getString("role");
//			String runtime = configuration.getString("runtime");
//			
//			logger.info(functionName);
//			
//			if (configuration.getString("functionArn") == null) {
//				
//				FunctionCode functionCode = new FunctionCode().withS3Bucket(code.getString("s3Bucket"))
//						.withS3Key(code.getString("s3Key"))
//						.withS3ObjectVersion(code.getString("s3ObjectVersion"));
//				
//				CreateFunctionRequest createFunctionRequest = new CreateFunctionRequest().withCode(functionCode)
//						.withDescription(description)
//						.withFunctionName(functionName)
//						.withHandler(handler)
//						.withMemorySize(memorySize)
//						.withPublish(Boolean.TRUE)
//						.withRole(role)
//						.withRuntime(runtime)
//						.withTimeout(timeout);
//				
//				CreateFunctionResult createFunctionResult = lambdaClient.createFunction(createFunctionRequest);
//				
//				configuration.put("functionArn", createFunctionResult.getFunctionArn());
//				configuration.put("codeSize", createFunctionResult.getCodeSize());
//				configuration.put("lastModified", createFunctionResult.getLastModified());
//				configuration.put("codeSha256", createFunctionResult.getCodeSha256());
//				configuration.put("version", createFunctionResult.getVersion());
//
//			} else {
//				
//				UpdateFunctionConfigurationRequest updateFunctionConfigurationRequest = new UpdateFunctionConfigurationRequest().withFunctionName(functionName)
//						.withHandler(handler)
//						.withDescription(description)
//						.withMemorySize(memorySize)
//						.withTimeout(timeout);
//		    	
//		    	UpdateFunctionConfigurationResult updateFunctionConfigurationResult = lambdaClient.updateFunctionConfiguration(updateFunctionConfigurationRequest);
//		    	
//		    	UpdateFunctionCodeRequest updateFunctionCodeRequest = new UpdateFunctionCodeRequest().withFunctionName(functionName)
//		    			.withS3Bucket(code.getString("s3Bucket"))
//		    			.withS3Key(code.getString("s3Key"))
//		    			.withS3ObjectVersion(code.getString("s3ObjectVersion"));
//				
//				lambdaClient.updateFunctionCode(updateFunctionCodeRequest);
//		    	
//		    	configuration.put("codeSize", updateFunctionConfigurationResult.getCodeSize());
//				configuration.put("lastModified", updateFunctionConfigurationResult.getLastModified());
//				configuration.put("codeSha256", updateFunctionConfigurationResult.getCodeSha256());
//				configuration.put("version", updateFunctionConfigurationResult.getVersion());
//			}
//			
//			try {
//				mongoDatabase.getCollection("aws.lambda.functions").replaceOne( new Document("configuration.functionName", functionName ), document, new UpdateOptions().upsert(true) );
//			} catch (MongoException e) {
//				e.printStackTrace();
//			}
//		}
//		
//		mongoClient.close();
	}
}