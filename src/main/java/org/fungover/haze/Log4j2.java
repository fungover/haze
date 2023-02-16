package org.fungover.haze;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.*;

public class Log4j2 {
    static Logger logger = LogManager.getLogger();

    public static void info(String message) {
        logger.info(message);
    }

    public static void error(String message) {
        logger.error(message);
    }

    public static void warning(String message) {
        logger.warn(message);
    }

    public static void debug(String message) {
        logger.debug(message);
    }
}
