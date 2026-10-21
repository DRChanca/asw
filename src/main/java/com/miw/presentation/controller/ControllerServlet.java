package com.miw.presentation.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.miw.infrastructure.Factories;
import com.miw.presentation.commands.Command;
import com.miw.presentation.di.HttpSessionAware;
import com.miw.presentation.di.ServletContextAware;
import com.miw.presentation.di.ServletRequestAware;
import com.miw.presentation.interceptores.AccessDeniedException;
import com.miw.presentation.interceptores.Interceptor;
import com.miw.presentation.interceptores.InterceptorChain;
import com.miw.presentation.interceptores.InvocationContext;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(value = "/Controller")
public class ControllerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	protected Logger logger = LogManager.getLogger(getClass());

	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Command command = null;
		String action = req.getParameter("action");
		if (action != null) {
			logger.debug("Executing action" + action);

			command = ControllerConfigurationManager.getInstance().getCommand(action);

			if (command != null) {
				// dependency injection
				if (command instanceof ServletRequestAware) {
					logger.debug("Injecting request in command " + action);
					((ServletRequestAware) command).setServletRequest(req);
				}
				if (command instanceof HttpSessionAware) {
					logger.debug("Injecting session in command " + action);
					((HttpSessionAware) command).setHttpSession(req.getSession());
				}
				if (command instanceof ServletContextAware) {
					logger.debug("Injecting session in command " + action);
					((ServletContextAware) command).setServletContext(req.getServletContext());
				}

				populateParameters(req, resp, command);

				List<Interceptor> interceptors =
				        Factories.interceptors.createInterceptors(
				                action,
				                req,
				                logger);
				
				
				InvocationContext context = new InvocationContext(
				        action,
				        command.getClass(),
				        req);

				InterceptorChain chain = new InterceptorChain(
				        interceptors,
				        command,
				        context);
				try {
				    chain.proceed();

				    String view = ControllerConfigurationManager
				            .getInstance()
				            .getForward(action);

				    req.getRequestDispatcher(view).forward(req, resp);

				} catch (AccessDeniedException e) {
				    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);

				    req.setAttribute("errorMessage", e.getMessage());

				    req.getRequestDispatcher("/accessDenied.jsp")
				            .forward(req, resp);
				}

			}
		}

	}

	private void populateParameters(HttpServletRequest req, HttpServletResponse resp, Command command) {
		for (String s : req.getParameterMap().keySet()) {
			try {
				Method m = command.getClass().getMethod("set" + s.substring(0, 1).toUpperCase() + s.substring(1),
						String.class);
				if (m != null) {
					logger.debug("Found expected parameter " + "set" + s.substring(0, 1).toUpperCase() + s.substring(1)
							+ ", populating the value");
					try {
						m.invoke(command, req.getParameter(s));
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}
}
