package com.miw.presentation.interceptores;

public interface Interceptor {
	
	public boolean before(InvocationContext context); 
	
	public void after(InvocationContext context, Throwable Exception); 
	
	
}
