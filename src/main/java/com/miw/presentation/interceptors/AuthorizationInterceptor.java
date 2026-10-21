package com.miw.presentation.interceptors;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import com.miw.presentation.di.HttpSessionAware;

import jakarta.servlet.http.HttpSession;

public class AuthorizationInterceptor
        implements Interceptor, HttpSessionAware {

    public static final String USER_SESSION_ATTRIBUTE =
            "authenticatedUser";

    private HttpSession session;

    private final Properties users;
    private final Properties permissions;

    public AuthorizationInterceptor() {
        users = loadProperties("users.properties");
        permissions = loadProperties("permissions.properties");
    }

    @Override
    public void setHttpSession(HttpSession session) {
        this.session = session;
    }

    @Override
    public boolean before(InvocationContext context) {
        Object value = session.getAttribute(USER_SESSION_ATTRIBUTE);

        if (!(value instanceof String username) || username.isBlank()) {
            return false;
        }

        String roles = users.getProperty(
                "user." + username + ".roles");

        if (roles == null) {
            return false;
        }

        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .anyMatch(role ->
                        roleAllowsAction(role, context.getAction()));
    }

    @Override
    public void after(
            InvocationContext context,
            Throwable failure) {
        return; 
    }
    private boolean roleAllowsAction(
            String role,
            String action) {

        String allowedActions = permissions.getProperty(
                "role." + role + ".commands",
                "");

        return Arrays.stream(allowedActions.split(","))
                .map(String::trim)
                .anyMatch(action::equals);
    }

    private Properties loadProperties(String resourceName) {
        Properties properties = new Properties();

        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(resourceName)) {

            if (input == null) {
                throw new IllegalStateException(
                        "No se encuentra " + resourceName);
            }

            properties.load(input);
            return properties;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se pudo leer " + resourceName,
                    e);
        }
    }
}