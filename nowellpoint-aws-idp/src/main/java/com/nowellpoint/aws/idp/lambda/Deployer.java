package com.nowellpoint.aws.idp.lambda;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Deployer {

	public static void main(String[] args) {
		System.out.println(args[0].concat(".jar"));
		
		System.out.println("running deployer");
		File deploymentFile = new File(Deployer.class.getClassLoader().getResource("nowellpoint-aws-deploy.json").getFile());
		System.out.println("found: " + deploymentFile.exists());
		
		try {
			List<Function> functions = new ObjectMapper().readValue(deploymentFile, new TypeReference<List<Function>>() { });
			functions.forEach( f -> System.out.println( f.getConfiguration().getFunctionName()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}