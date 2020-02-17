package com.github.twitch4j.common.feign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import feign.Request;
import feign.Response;

/**
 * Logs to SLF4J at the debug level, if the underlying logger has debug logging enabled. The
 * underlying logger can be specified at construction-time, defaulting to the logger for
 * {@link feign.Logger}.
 */
public class Twitch4jFeignSlf4jLogger extends feign.Logger {
    
    private final Logger logger;
    
    public Twitch4jFeignSlf4jLogger() {
        this(feign.Logger.class);
    }
    
    public Twitch4jFeignSlf4jLogger(Class<?> clazz) {
        this(LoggerFactory.getLogger(clazz));
    }
    
    public Twitch4jFeignSlf4jLogger(String name) {
        this(LoggerFactory.getLogger(name));
    }
    
    Twitch4jFeignSlf4jLogger(Logger logger) {
        this.logger = logger;
    }
    
    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        if(logger.isTraceEnabled()) super.logRequest(configKey, Level.FULL, request);
        else if(logger.isDebugEnabled()) super.logRequest(configKey, Level.HEADERS, request);
        else if(logger.isInfoEnabled()) super.logRequest(configKey, Level.BASIC, request);
        else super.logRequest(configKey, Level.NONE, request);
    }
    
    @Override
    protected Response logAndRebufferResponse(String configKey,
                                              Level logLevel,
                                              Response response,
                                              long elapsedTime)
        throws IOException {
        if(logger.isTraceEnabled()) return super.logAndRebufferResponse(configKey, Level.FULL, response, elapsedTime);
        else if(logger.isDebugEnabled()) return super.logAndRebufferResponse(configKey, Level.HEADERS, response, elapsedTime);
        else if(logger.isInfoEnabled()) return super.logAndRebufferResponse(configKey, Level.BASIC, response, elapsedTime);
        else return super.logAndRebufferResponse(configKey, Level.NONE, response, elapsedTime);
    }
    
    @Override
    protected void log(String configKey, String format, Object... args) {
        if(logger.isTraceEnabled()) logger.trace(String.format(methodTag(configKey) + format, args));
        else if(logger.isDebugEnabled()) logger.debug(String.format(methodTag(configKey) + format, args));
        else if(logger.isInfoEnabled()) logger.info(String.format(methodTag(configKey) + format, args));
    }
}
