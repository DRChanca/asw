package com.miw.presentation.commands;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.miw.presentation.di.HttpSessionAware;
import com.miw.presentation.di.ServletRequestAware;
import com.miw.presentation.interceptores.AuthorizationInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class LoginCommand
        implements Command, HttpSessionAware, ServletRequestAware {

    private HttpSession session;
    private HttpServletRequest request;
    private String username;

    // populateParameters lo llama al recibir el campo "username".
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void setHttpSession(HttpSession session) {
        this.session = session;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void execute() {
        Properties users = loadUsers();

        String selectedUser =
                username == null ? "" : username.trim();

        if (!users.containsKey("user." + selectedUser + ".roles")) {
            session.removeAttribute(
                    AuthorizationInterceptor.USER_SESSION_ATTRIBUTE);

            request.setAttribute(
                    "loginMessage",
                    "El usuario indicado no existe.");

            return;
        }

        // Cambia el identificador de sesión al iniciar sesión.
        request.changeSessionId();

        // AQUÍ guardamos la identidad que leerá autorización.
        session.setAttribute(
                AuthorizationInterceptor.USER_SESSION_ATTRIBUTE,
                selectedUser);

        request.setAttribute(
                "loginMessage",
                "Identificación realizada correctamente.");
    }

    private Properties loadUsers() {
        Properties users = new Properties();

        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("users.properties")) {

            if (input == null) {
                throw new IllegalStateException(
                        "No se encuentra users.properties");
            }

            users.load(input);
            return users;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se pudo leer users.properties", e);
        }
    }
}