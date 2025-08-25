package com.example.weesh.core.foundation.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingUtil {
    private static final Logger logger = LoggerFactory.getLogger(LoggingUtil.class);

    public static void info(String message, String username) {
        logger.info(message, username);
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void error(String message, String message2) {
        logger.error(message, message2);
    }

    public static void debug(String message) {
        logger.debug(message);
    }

    public static void error(String s, String requestURI, String message) {
        logger.error(requestURI, message, s);
    }

    public static void warn(String message, String message1) {
        logger.warn(message, message1);
    }

    public static void info(String s, String key, String value) {
        logger.info(s, key, value);
    }
}