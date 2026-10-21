package com.miw.presentation.interceptors;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.Logger;

import com.miw.infrastructure.Helper;
import com.miw.presentation.di.HttpSessionAware;
import com.miw.presentation.di.ServletRequestAware;

import jakarta.servlet.http.HttpServletRequest;

public class PropertiesInterceptorsFactory
        implements InterceptorsFactory {

    private final Properties commandConfiguration;

    public PropertiesInterceptorsFactory() {
        commandConfiguration =
                loadProperties("/command.properties");
    }

    @Override
    public List<Interceptor> createInterceptors(
            String action,
            HttpServletRequest request,
            Logger logger) {

        String key = "command." + action + ".interceptors";

        String configuredIds =
                commandConfiguration.getProperty(key, "").trim();

        if (configuredIds.isEmpty()) {
            return List.of();
        }

        List<Interceptor> result = new ArrayList<>();

        for (String value : configuredIds.split(",", -1)) {
            String id = value.trim();

            if (id.isEmpty()) {
                throw new IllegalStateException(
                        "Identificador vacío en " + key);
            }

            Interceptor interceptor = createInterceptor(id);

            injectDependencies(interceptor, request, logger);

            result.add(interceptor);
        }

        return List.copyOf(result);
    }

    private Interceptor createInterceptor(String id) {
        return Helper.createInstance(
                "/interceptors.properties",
                "interceptor." + id + ".class",
                Interceptor.class);
    }

    private void injectDependencies(
            Interceptor interceptor,
            HttpServletRequest request,
            Logger logger) {

        // Inyección por interfaz
        if (interceptor instanceof HttpSessionAware sessionAware) {
            sessionAware.setHttpSession(request.getSession());
        }

        if (interceptor instanceof ServletRequestAware requestAware) {
            requestAware.setServletRequest(request);
        }

        // Inyección por setter
        injectLogger(interceptor, logger);
    }

    private void injectLogger(
            Interceptor interceptor,
            Logger logger) {

        Method setter;

        try {
            setter = interceptor.getClass()
                    .getMethod("setLogger", Logger.class);

        } catch (NoSuchMethodException e) {
            return;
        }

        try {
            setter.invoke(interceptor, logger);

        } catch (IllegalAccessException
                 | InvocationTargetException e) {

            throw new IllegalStateException(
                    "No se pudo inyectar el logger en "
                            + interceptor.getClass().getName(),
                    e);
        }
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