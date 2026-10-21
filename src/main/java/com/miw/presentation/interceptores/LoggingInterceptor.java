package com.miw.presentation.interceptores;

import java.time.Instant;

import org.apache.logging.log4j.Logger;

public class LoggingInterceptor implements Interceptor {

    private Logger logger;
    private long startNanos;

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean before(InvocationContext context) {
        startNanos = System.nanoTime();

        logger.info(
                "Inicio: comando={}, acción={}, fecha={}",
                context.getCommandClass().getName(),
                context.getAction(),
                Instant.now());
        return true; 
    }

    @Override
    public void after(
            InvocationContext context,
            Throwable failure) {

        double durationMs =
                (System.nanoTime() - startNanos) / 1_000_000.0;

        String commandName =
                context.getCommandClass().getName();

        if (failure == null) {
            logger.info(
                    "Finalización correcta: comando={}, duraciónMs={}",
                    commandName,
                    durationMs);

        } else if (failure instanceof AccessDeniedException) {
            logger.warn(
                    "Acceso denegado: comando={}, duraciónMs={}, motivo={}",
                    commandName,
                    durationMs,
                    failure.getMessage());

        } else {
            logger.error(
                    "Finalización con excepción: comando="
                            + commandName
                            + ", duraciónMs="
                            + durationMs,
                    failure);
        }
    }
}