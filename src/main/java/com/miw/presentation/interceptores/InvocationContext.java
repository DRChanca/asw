package com.miw.presentation.interceptores;


import jakarta.servlet.http.HttpServletRequest;

public class InvocationContext {
	
	public InvocationContext(String nombre, Class<?> commandClass, HttpServletRequest request) {
		super();
		this.action = nombre;
		this.commandClass = commandClass;
		this.request = request;
	}
	private String action; 
	private final Class<?> commandClass; 
	private final HttpServletRequest request;
	
	public String getAction() {
		return action;
	}
	public Class<?> getCommandClass() {
		return commandClass;
	}
	public HttpServletRequest getRequest() {
		return request;
	} 
	
	

}
