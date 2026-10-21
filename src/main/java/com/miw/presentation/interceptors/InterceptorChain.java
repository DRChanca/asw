package com.miw.presentation.interceptors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.miw.presentation.commands.Command;

public final class InterceptorChain {

    private final List<Interceptor> interceptors;
    private final Command command;
    private final InvocationContext context;

    public InterceptorChain(
            List<Interceptor> interceptors,
            Command command,
            InvocationContext context) {

        this.interceptors = List.copyOf(interceptors);
        this.command = Objects.requireNonNull(command);
        this.context = Objects.requireNonNull(context);
    }

    public void proceed() {
        List<Interceptor> entered = new ArrayList<>();
        Throwable failure = null;
        boolean allowed = true;

        try {
            for (Interceptor interceptor : interceptors) {
                entered.add(interceptor);

                allowed = interceptor.before(context);

                if (!allowed) {
                    break;
                }
            }

            if (allowed) {
                command.execute();
            } else {
                throw new AccessDeniedException(
                        "La ejecución ha sido bloqueada por un interceptor.");
            }

        } catch (RuntimeException | Error e) {
            failure = e;

        } finally {
            for (int i = entered.size() - 1; i >= 0; i--) {
                try {
                    entered.get(i).after(context, failure);

                } catch (RuntimeException | Error e) {
                    if (failure == null) {
                        failure = e;
                    } else if (failure != e) {
                        failure.addSuppressed(e);
                    }
                }
            }
        }

        if (failure instanceof RuntimeException exception) {
            throw exception;
        }

        if (failure instanceof Error error) {
            throw error;
        }
    }
}