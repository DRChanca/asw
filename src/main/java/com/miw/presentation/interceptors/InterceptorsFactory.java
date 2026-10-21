package com.miw.presentation.interceptors;

import java.util.List;
import org.apache.logging.log4j.*;

import jakarta.servlet.http.HttpServletRequest;

public interface InterceptorsFactory {
    List<Interceptor> createInterceptors(
            String action,
            HttpServletRequest request,
            Logger logger);
}
