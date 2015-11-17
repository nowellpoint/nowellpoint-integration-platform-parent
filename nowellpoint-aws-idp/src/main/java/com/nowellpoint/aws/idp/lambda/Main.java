package com.nowellpoint.aws.idp.lambda;

import java.io.File;
import com.nowellpoint.aws.tools.Deployer;

public class Main {

	public static void main(String[] args) {
		System.out.println(args[0]);
		System.out.println(args[1]);
		
		Deployer deployer = new Deployer();
		
		deployer.doDeploy(new File(args[0]), new File(args[1]));
	}
}