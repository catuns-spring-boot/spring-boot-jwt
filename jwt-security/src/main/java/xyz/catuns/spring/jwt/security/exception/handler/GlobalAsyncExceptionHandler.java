package xyz.catuns.spring.jwt.security.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

public class GlobalAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalAsyncExceptionHandler.class);

    /**
     * Handles uncaught exceptions that occur during the execution of asynchronous methods.
     * This method is called when an asynchronous method throws an exception that was not
     * caught within the method itself.
     *
     * @param ex     The uncaught Throwable that was thrown during the asynchronous method execution.
     * @param method The Method object representing the asynchronous method that threw the exception.
     * @param params An array of Objects representing the parameters that were passed to the asynchronous method.
     */
    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("[{}] {} {}", ex.getCause(), ex.getMessage(), method);
    }
}
