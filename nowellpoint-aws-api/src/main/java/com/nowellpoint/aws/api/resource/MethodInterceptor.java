package com.nowellpoint.aws.api.resource;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

public class MethodInterceptor {
	
	@AroundInvoke
	public Object interceptorMethod(InvocationContext context) throws Exception{
		System.out.println("method invoked");
		return context.proceed();
	}
}