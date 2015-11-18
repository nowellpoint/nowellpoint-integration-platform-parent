package com.nowellpoint.aws.util.lambda;

import java.io.File;

import com.nowellpoint.aws.tools.Deployer;

public class Main {
	
	public static void main(String[] args) {
		Deployer deployer = new Deployer();
		deployer.doDeploy(new File(args[0]), new File(args[1]));
	}
}